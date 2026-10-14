package com.example.movie.tools;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.example.movie.Utils.DictConvert;
import com.example.movie.aidomain.BookSeatQuery;
import com.example.movie.aidomain.CancelOrderQuery;
import com.example.movie.aidomain.MovieAIVO;
import com.example.movie.aidomain.MovieQuery;
import com.example.movie.aidomain.MovieSessionQuery;
import com.example.movie.aidomain.MyOrdersQuery;
import com.example.movie.aidomain.SessionSeatInfo;
import com.example.movie.aidomain.SessionTicketInfo;
import com.example.movie.aidomain.SeatRecommendation;
import com.example.movie.aidomain.SeatState;
import com.example.movie.bedto.HallPlus;
import com.example.movie.bedto.SeatDTO;
import com.example.movie.bedto.UserOrderListDTO;
import com.example.movie.common.UserContext;
import com.example.movie.dto.SoldSeatDTO;
import com.example.movie.entity.Hall;
import com.example.movie.entity.Movie;
import com.example.movie.entity.Order;
import com.example.movie.entity.Session;
import com.example.movie.mapper.SessionMapper;
import com.example.movie.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 作者: 枷
 * 时间:2026/10/5 下午11:49
 * 描述:星光影院AI助手的工具类，提供电影查询、场次余票查询、订单取消三大能力
 */
@RequiredArgsConstructor
@Component
public class MovieTools {
    private final MovieAiService movieService;
    private final SessionAiService sessionService;
    private final HallAiService hallService;
    private final OrderAiService orderService;
    private final OrderService orderBizService;
    private final UserOrderService userOrderService;
    private final SessionMapper sessionMapper;
    private final UserHallService uhService;
    private final StringRedisTemplate redisTemplate;

    @Tool(description = "根据条件查询电影信息，可查询电影名称、类型、导演、状态（1-正在上映，0-已下架，2-即将上映）等。返回结果中的类型、国家、语言、状态均已转换为中文名称，可直接展示给用户")
    public List<MovieAIVO> queryMovie(@ToolParam(required = false, description = "电影查询条件") MovieQuery query) {
        QueryChainWrapper<Movie> wrapper = movieService.query();
        wrapper
                .like(query.getName() != null, "name", query.getName())
                .like(query.getTypeCodes() != null, "typeCodes", query.getTypeCodes())
                .eq(query.getStatus() != null, "status", query.getStatus())
                .eq(query.getDirector() != null, "director", query.getDirector())
                .eq(query.getCountryCode() != null, "countryCode", query.getCountryCode())
                .eq(query.getLanguageCode() != null, "languageCode", query.getLanguageCode())
                .eq(query.getReleaseDate() != null, "releaseDate", query.getReleaseDate());
        return wrapper.list().stream()
                .map(this::toAIVO)
                .toList();
    }

    private MovieAIVO toAIVO(Movie m) {
        return MovieAIVO.builder()
                .id(m.getId())
                .name(m.getName())
                .director(m.getDirector())
                .cast(m.getCast())
                .typeNames(DictConvert.typeCodesToNames(m.getTypeCodes()))
                .countryName(DictConvert.countryCodeToName(m.getCountryCode()))
                .languageName(DictConvert.languageCodeToName(m.getLanguageCode()))
                .duration(m.getDuration())
                .releaseDate(m.getReleaseDate())
                .score(m.getScore())
                .statusName(DictConvert.movieStatusToName(m.getStatus()))
                .build();
    }

    @Tool(description = "统一查询电影的上映日期、最早可观看场次、某日余票、某场次余票。" +
            "只需传入movieName，通过不同参数组合覆盖多种场景：" +
            "① 问'xx电影什么时候上映'→只传movieName即可；" +
            "② 问'最快能看xx电影哪一场'→传movieName+earliestOnly=true；" +
            "③ 问'xx电影x号下午2点有没有票'→传movieName+showDate+specificTime；" +
            "④ 问'xx电影x号上午还剩多少位置'→传movieName+showDate+timePeriod；" +
            "⑤ 已知sessionId查某场次详情→传sessionId")
    public List<SessionTicketInfo> queryMovieSession(@ToolParam(required = true, description = "场次查询条件，一个工具覆盖上映日期、最早场次、某日余票、单场次详情等所有查询场景") MovieSessionQuery query) {
        List<SessionTicketInfo> result = new ArrayList<>();

        if (query.getSessionId() != null) {
            return buildSingleSessionResult(query.getSessionId());
        }

        List<Movie> movies = movieService.query()
                .like("name", query.getMovieName() != null ? query.getMovieName() : "")
                .list();
        if (movies.isEmpty()) return result;

        LocalTime timeStart = null;
        LocalTime timeEnd = null;
        if (query.getTimePeriod() != null) {
            switch (query.getTimePeriod()) {
                case "上午" -> { timeStart = LocalTime.of(9, 0); timeEnd = LocalTime.of(12, 0); }
                case "下午" -> { timeStart = LocalTime.of(12, 0); timeEnd = LocalTime.of(18, 0); }
                case "晚上" -> { timeStart = LocalTime.of(18, 0); timeEnd = LocalTime.of(23, 59); }
            }
        }
        LocalTime specificTime = null;
        if (query.getSpecificTime() != null) {
            try { specificTime = LocalTime.parse(query.getSpecificTime()); } catch (Exception ignored) {}
        }
        boolean earliestOnly = Boolean.TRUE.equals(query.getEarliestOnly());
        boolean onlyAvailable = !Boolean.FALSE.equals(query.getOnlyAvailable());
        LocalDateTime now = LocalDateTime.now();

        for (Movie movie : movies) {
            LocalDateTime startTimeFilter;
            LocalDateTime endTimeFilter;

            if (query.getShowDate() != null) {
                startTimeFilter = query.getShowDate().atStartOfDay();
                endTimeFilter = query.getShowDate().plusDays(1).atStartOfDay();
            } else {
                startTimeFilter = now;
                endTimeFilter = null;
            }

            QueryChainWrapper<Session> wrapper = sessionService.query()
                    .eq("movie_id", movie.getId())
                    .eq("status", 1)
                    .ge("start_time", startTimeFilter)
                    .lt(endTimeFilter != null, "start_time", endTimeFilter)
                    .orderByAsc("start_time");

            List<Session> sessions = wrapper.list();

            for (Session session : sessions) {
                LocalTime sessionTime = session.getStartTime().toLocalTime();

                if (timeStart != null) {
                    if (sessionTime.isBefore(timeStart)) continue;
                    if (timeEnd != null && sessionTime.isAfter(timeEnd)) continue;
                }
                if (specificTime != null && sessionTime.getHour() != specificTime.getHour()) continue;

                Hall hall = hallService.getById(session.getHallId());
                if (hall == null) continue;

                int soldCount = sessionMapper.selectSoldSeats(session.getId()).size();
                int remainingSeats = hall.getCapacity() - soldCount;
                boolean available = remainingSeats > 0;

                if (onlyAvailable && !available) continue;

                String statusDesc = available
                        ? "有票，剩余" + remainingSeats + "个座位"
                        : "已满场，无票";

                result.add(SessionTicketInfo.builder()
                        .sessionId(session.getId())
                        .movieName(movie.getName())
                        .releaseDate(movie.getReleaseDate())
                        .hallName(hall.getName())
                        .startTime(session.getStartTime())
                        .endTime(session.getEndTime())
                        .price(session.getPrice())
                        .totalSeats(hall.getCapacity())
                        .soldSeats(soldCount)
                        .remainingSeats(remainingSeats)
                        .available(available)
                        .statusDesc(statusDesc)
                        .build());

                if (earliestOnly && available) break;
            }
            if (earliestOnly && !result.isEmpty()) break;
        }

        if (result.isEmpty() && query.getShowDate() == null) {
            for (Movie movie : movies) {
                result.add(SessionTicketInfo.builder()
                        .movieName(movie.getName())
                        .releaseDate(movie.getReleaseDate())
                        .statusDesc("电影尚未排期，暂无场次。上映日期：" + movie.getReleaseDate())
                        .build());
                break;
            }
        }

        return result;
    }

    private List<SessionTicketInfo> buildSingleSessionResult(Long sessionId) {
        List<SessionTicketInfo> result = new ArrayList<>();
        Session session = sessionService.getById(sessionId);
        if (session == null) return result;

        Movie movie = movieService.getById(session.getMovieId());
        Hall hall = hallService.getById(session.getHallId());
        if (movie == null || hall == null) return result;

        int soldCount = sessionMapper.selectSoldSeats(sessionId).size();
        int remainingSeats = hall.getCapacity() - soldCount;
        boolean available = remainingSeats > 0;

        String statusDesc;
        if (session.getStatus() != 1) {
            statusDesc = "该场次当前" + (session.getStatus() == 0 ? "已停售" : "已结束");
            available = false;
        } else {
            statusDesc = available ? "有票，剩余" + remainingSeats + "个座位" : "已满场，无票";
        }

        result.add(SessionTicketInfo.builder()
                .sessionId(sessionId)
                .movieName(movie.getName())
                .releaseDate(movie.getReleaseDate())
                .hallName(hall.getName())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .price(session.getPrice())
                .totalSeats(hall.getCapacity())
                .soldSeats(soldCount)
                .remainingSeats(remainingSeats)
                .available(available)
                .statusDesc(statusDesc)
                .build());
        return result;
    }

    @Tool(description = "查询当前登录用户自己的订单列表。" +
            "用户说'我的订单'、'火影的订单'、'我还有哪些待支付的单' → 用这个。" +
            "可按电影名模糊过滤、按订单状态过滤（0=待支付，1=已支付，2=已取消，3=已退款）。" +
            "取消订单流程：先用这个工具查出用户要取消的订单→展示给用户确认→确认后再调用cancelMovieOrder")
    public List<UserOrderListDTO> queryMyOrders(@ToolParam(required = false, description = "我的订单查询条件，userId由系统自动获取") MyOrdersQuery query,
                                                ToolContext toolContext) {
        Long userId = resolveUserId(toolContext);
        if (userId == null) {
            throw new IllegalStateException("用户未登录，无法查询订单");
        }
        String movieName = query != null ? query.getMovieName() : null;
        Integer status = query != null ? query.getStatus() : null;
        return userOrderService.getOrderListWithFilter(userId, movieName, status);
    }

    @Tool(description = "取消当前登录用户自己的订单。" +
            "注意：只有待支付(status=0)的订单才能取消。" +
            "前置条件：必须先调用queryMyOrders查出目标订单，向用户展示订单信息（电影名、场次时间、金额）并二次确认后，再执行此操作。" +
            "参数orderIdOrNo传订单ID（纯数字）或订单编号（16位字符串）均可")
    public String cancelMovieOrder(@ToolParam(required = true, description = "要取消的订单ID（纯数字）或订单编号（字符串）") CancelOrderQuery query,
                                   ToolContext toolContext) {
        Long currentUserId = resolveUserId(toolContext);
        if (currentUserId == null) {
            return "用户未登录，无法取消订单";
        }
        try {
            Order order = findOrder(query.getOrderIdOrNo());

            if (order == null) {
                return "未找到该订单，请检查订单ID或订单编号是否正确";
            }

            if (!order.getUserId().equals(currentUserId)) {
                return "该订单不属于当前登录用户，无法取消";
            }

            int status = order.getStatus();
            if (status != 0) {
                String statusDesc = switch (status) {
                    case 1 -> "已支付";
                    case 2 -> "已取消";
                    case 3 -> "已退款";
                    default -> "未知状态";
                };
                return "该订单当前状态为" + statusDesc + "，只有待支付的订单才能取消";
            }

            orderBizService.cancelOrder(order.getId(), currentUserId);

            return "订单取消成功！订单编号：" + order.getOrderNo() + "，座位已释放";
        } catch (Exception e) {
            return "取消订单失败：" + e.getMessage();
        }
    }

    @Tool(description = "查询某场次的座位布局和占用情况（选座抢票的必备前置工具）。影院座位为 rows 行 x cols 列的网格，" +
            "本工具返回可选座位数 freeSeatCount、是否可购票 bookable，以及全部不可用座位明细 unavailableSeats（state 为：已售/已被他人锁定/你已锁定），" +
            "其余座位即为可选。用户表达'选座/抢座/抢票/订票/买票/占座/帮忙锁个位置'等意图时，必须先调用本工具获取可选座位，再调用 lockSeats 锁定。")
    public SessionSeatInfo querySeats(@ToolParam(required = true, description = "场次ID，从queryMovieSession的返回结果中获取") Long sessionId,
                                      ToolContext toolContext) {
        Long currentUserId = resolveUserId(toolContext);

        SeatGrid grid;
        try {
            grid = loadSeatGrid(sessionId, currentUserId);
        } catch (RuntimeException e) {
            // 兜底：sessionId 无效时给出明确指引让模型自愈，避免反复报"场次不存在"卡死流程
            return SessionSeatInfo.builder()
                    .sessionId(sessionId)
                    .bookable(false)
                    .statusDesc("场次ID无效或不存在：" + e.getMessage() + "。请先调用queryMovieSession确认正确的场次ID，再重新调用本工具")
                    .freeSeatCount(0)
                    .unavailableSeats(new ArrayList<>())
                    .build();
        }

        Session session = sessionMapper.getById(sessionId);
        boolean bookable = session != null && session.getStatus() != null && session.getStatus() == 1
                && session.getStartTime() != null && session.getStartTime().isAfter(LocalDateTime.now());
        String statusDesc;
        if (!bookable) {
            statusDesc = (session == null || session.getStatus() == null || session.getStatus() != 1)
                    ? "该场次已停售或已结束，无法购票"
                    : "该场次已开场，无法购票";
        } else {
            statusDesc = "可购票";
        }

        return SessionSeatInfo.builder()
                .sessionId(sessionId)
                .movieName(grid.hallPlus.getMovieName())
                .hallName(grid.hallPlus.getHallName())
                .startTime(grid.hallPlus.getStartTime())
                .endTime(grid.hallPlus.getEndTime())
                .price(grid.hallPlus.getPrice())
                .rows(grid.rows)
                .cols(grid.cols)
                .capacity(grid.hallPlus.getCapacity())
                .freeSeatCount(Math.max(0, grid.total - grid.unavailable.size()))
                .bookable(bookable)
                .statusDesc(statusDesc)
                .unavailableSeats(grid.states)
                .build();
    }

    @Tool(description = "根据用户的模糊座位偏好推荐具体座位（用户说不出精确排/列号时使用，如'最后一排左侧相邻两个'、'前排中间'、'中间靠右'）。" +
            "入参：sessionId（场次ID）、count（票数）、preference（偏好描述，支持关键词：最后一排/最后/第一排/前排/中间/左侧/右侧/相邻/连座/靠窗）。" +
            "返回可直接用于lockSeats的具体座位（排/列）及推荐理由；若场次无效或空位不足会返回原因。")
    public SeatRecommendation recommendSeats(@ToolParam(required = true, description = "场次ID，从queryMovieSession的返回结果中获取") Long sessionId,
                                             @ToolParam(required = true, description = "需要几张票") Integer count,
                                             @ToolParam(required = false, description = "座位偏好描述，如：最后一排靠左侧相邻两个") String preference,
                                             ToolContext toolContext) {
        Long currentUserId = resolveUserId(toolContext);
        int need = (count == null || count < 1) ? 2 : count;

        SeatGrid grid;
        try {
            grid = loadSeatGrid(sessionId, currentUserId);
        } catch (RuntimeException e) {
            return SeatRecommendation.builder()
                    .sessionId(sessionId)
                    .statusDesc("场次ID无效或不存在：" + e.getMessage() + "。请先调用queryMovieSession确认正确的场次ID，再重新调用本工具")
                    .suggestedSeats(new ArrayList<>())
                    .build();
        }

        Session session = sessionMapper.getById(sessionId);
        boolean bookable = session != null && session.getStatus() != null && session.getStatus() == 1
                && session.getStartTime() != null && session.getStartTime().isAfter(LocalDateTime.now());
        int freeCount = grid.total - grid.unavailable.size();
        if (!bookable) {
            String reason = (session == null || session.getStatus() == null || session.getStatus() != 1)
                    ? "该场次已停售或已结束，无法购票"
                    : "该场次已开场，无法购票";
            return SeatRecommendation.builder()
                    .sessionId(sessionId)
                    .statusDesc(reason)
                    .suggestedSeats(new ArrayList<>())
                    .build();
        }
        if (freeCount < need) {
            return SeatRecommendation.builder()
                    .sessionId(sessionId)
                    .statusDesc("该场次剩余可选座位不足，当前可选 " + freeCount + " 个，请换场次或减少票数")
                    .suggestedSeats(new ArrayList<>())
                    .build();
        }

        String pref = preference == null ? "" : preference.trim();
        boolean wantAdjacent = pref.contains("相邻") || pref.contains("连座") || pref.contains("挨着") || pref.contains("一起");
        boolean left = pref.contains("左") || pref.contains("靠窗");
        boolean right = pref.contains("右");
        int preferredStart = right ? grid.cols : (left ? 1 : Math.max(1, grid.cols / 2 - need / 2));

        List<SeatState> picked = new ArrayList<>();
        String reason = "已按" + (pref.isEmpty() ? "默认（中间偏后、相邻连座）" : "「" + pref + "」") + "为你挑选";

        // ① 优先：在偏好行里找连续连座
        outer:
        for (int row : pickRows(grid.rows, pref)) {
            List<Integer> freeCols = new ArrayList<>();
            for (int c = 1; c <= grid.cols; c++) {
                if (!grid.unavailable.contains(row + "-" + c)) freeCols.add(c);
            }
            if (freeCols.size() < need) continue;
            List<Integer> run = consecutiveRun(freeCols, need);
            if (run != null) {
                for (int c : run) picked.add(SeatState.builder().seatRow(row).seatCol(c).state("推荐").build());
                reason += (wantAdjacent || pref.isEmpty()) ? "（相邻连座）" : "（同一排相邻）";
                break outer;
            }
        }

        // ② 兜底：该行选最接近偏好侧的空座（允许不连续）
        if (picked.isEmpty()) {
            for (int row : pickRows(grid.rows, pref)) {
                List<Integer> freeCols = new ArrayList<>();
                for (int c = 1; c <= grid.cols; c++) {
                    if (!grid.unavailable.contains(row + "-" + c)) freeCols.add(c);
                }
                if (freeCols.size() < need) continue;
                List<Integer> sorted = new ArrayList<>(freeCols);
                sorted.sort(Comparator.comparingInt(c -> Math.abs(c - preferredStart)));
                for (int k = 0; k < need; k++) {
                    picked.add(SeatState.builder().seatRow(row).seatCol(sorted.get(k)).state("推荐").build());
                }
                reason += "（该排未能凑到连座，已选最接近" + (left ? "左侧" : right ? "右侧" : "中间") + "的空座）";
                break;
            }
        }

        // ③ 全局兜底：散座
        if (picked.isEmpty()) {
            List<SeatState> all = new ArrayList<>();
            for (int r = 1; r <= grid.rows; r++) {
                for (int c = 1; c <= grid.cols; c++) {
                    if (!grid.unavailable.contains(r + "-" + c)) {
                        all.add(SeatState.builder().seatRow(r).seatCol(c).state("推荐").build());
                    }
                }
            }
            for (int i = 0; i < need && i < all.size(); i++) picked.add(all.get(i));
            reason += "（座位较分散，已选最靠前的可选座位）";
        }

        reason += "：" + describeSeatsFor(picked, grid);

        return SeatRecommendation.builder()
                .sessionId(sessionId)
                .movieName(grid.hallPlus.getMovieName())
                .hallName(grid.hallPlus.getHallName())
                .startTime(grid.hallPlus.getStartTime())
                .price(grid.hallPlus.getPrice())
                .rows(grid.rows)
                .cols(grid.cols)
                .freeSeatCount(freeCount)
                .preference(pref)
                .statusDesc("推荐成功")
                .reason(reason)
                .suggestedSeats(picked)
                .build();
    }

    /** 座位网格快照：布局 + 全部不可用座（已售/Redis被锁） */
    private static class SeatGrid {
        final HallPlus hallPlus;
        final int rows;
        final int cols;
        final int total;
        final Set<String> unavailable;
        final List<SeatState> states;

        SeatGrid(HallPlus hallPlus, int rows, int cols, Set<String> unavailable, List<SeatState> states) {
            this.hallPlus = hallPlus;
            this.rows = rows;
            this.cols = cols;
            this.total = rows * cols;
            this.unavailable = unavailable;
            this.states = states;
        }
    }

    /** 计算场次座位网格：布局 + 已售（有效订单） + Redis 当前锁（区分本人/他人） */
    private SeatGrid loadSeatGrid(Long sessionId, Long currentUserId) {
        HallPlus hallPlus = uhService.getplus(sessionId);

        int[] dims = parseLayout(hallPlus.getSeatLayout(), hallPlus.getCapacity());
        int rows = dims[0];
        int cols = dims[1];

        Set<String> unavailable = new HashSet<>();
        List<SeatState> states = new ArrayList<>();

        // ① 已售（订单有效，已排除已取消/已退款）
        for (SoldSeatDTO sold : sessionMapper.selectSoldSeats(sessionId)) {
            String key = sold.getSeatRow() + "-" + sold.getSeatCol();
            if (unavailable.add(key)) {
                states.add(SeatState.builder()
                        .seatRow(sold.getSeatRow())
                        .seatCol(sold.getSeatCol())
                        .state("已售")
                        .build());
            }
        }

        // ② Redis 中当前被锁定的座位（区分本人/他人）
        Cursor<byte[]> cursor = null;
        try {
            cursor = redisTemplate.getConnectionFactory().getConnection()
                    .scan(ScanOptions.scanOptions().match("seat:" + sessionId + ":*").count(500).build());
            while (cursor.hasNext()) {
                String key = new String(cursor.next());
                String[] parts = key.split(":");
                if (parts.length < 4) continue;
                int row = Integer.parseInt(parts[2]);
                int col = Integer.parseInt(parts[3]);
                String rowCol = row + "-" + col;
                if (unavailable.contains(rowCol)) continue;

                String value = redisTemplate.opsForValue().get(key);
                String owner = value != null && value.contains(":") ? value.split(":")[0] : null;
                boolean isSelf = owner != null && currentUserId != null && owner.equals(String.valueOf(currentUserId));
                states.add(SeatState.builder()
                        .seatRow(row)
                        .seatCol(col)
                        .state(isSelf ? "你已锁定" : "已被他人锁定")
                        .build());
                unavailable.add(rowCol);
            }
        } finally {
            if (cursor != null) {
                try { cursor.close(); } catch (Exception ignored) {}
            }
        }
        return new SeatGrid(hallPlus, rows, cols, unavailable, states);
    }

    /** 按偏好生成行的遍历顺序（先满足偏好的行排前面） */
    private List<Integer> pickRows(int rows, String pref) {
        boolean back = pref.contains("最后") || pref.contains("后") || pref.contains("末") || pref.contains("排尾");
        boolean front = pref.contains("第一") || pref.contains("前") || pref.contains("首");
        List<Integer> order = new ArrayList<>();
        if (back) {
            for (int r = rows; r >= 1; r--) order.add(r);
        } else if (front) {
            for (int r = 1; r <= rows; r++) order.add(r);
        } else {
            // 默认/中间：从中间向两边扩散
            int m = Math.max(1, (rows + 1) / 2);
            order.add(m);
            for (int d = 1; d <= rows; d++) {
                if (m - d >= 1) order.add(m - d);
                if (m + d <= rows) order.add(m + d);
            }
        }
        return new ArrayList<>(new LinkedHashSet<>(order));
    }

    /** 在升序空座列号里找第一个长度为 need 的连续段 */
    private List<Integer> consecutiveRun(List<Integer> freeCols, int need) {
        for (int i = 0; i + need <= freeCols.size(); i++) {
            boolean ok = true;
            for (int j = 1; j < need; j++) {
                if (freeCols.get(i + j) != freeCols.get(i) + j) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                return freeCols.subList(i, i + need);
            }
        }
        return null;
    }

    private String describeSeatsFor(List<SeatState> seats, SeatGrid grid) {
        List<String> parts = new ArrayList<>();
        for (SeatState s : seats) {
            parts.add(s.getSeatRow() + "排" + s.getSeatCol() + "座");
        }
        String price = grid.hallPlus.getPrice() == null ? "" : grid.hallPlus.getPrice() + "元/张";
        return String.join("、", parts) + (price.isEmpty() ? "" : "，票价" + price) + "，共" + seats.size() + "张";
    }

    @Tool(description = "为当前登录用户批量锁定座位（抢座）。前置条件：必须先调用querySeats确认座位可用，且已把座位+票价展示给用户并获得用户明确确认。" +
            "锁定成功后座位保留约5分钟，请尽快调用submitOrder提交订单，否则锁会自动过期。" +
            "座位参数：单座传 seatRow+seatCol；同场次多座传 seats 数组（每项含 seatRow/seatCol）。锁定失败会返回具体原因（如某排某座已被占用）。")
    public String lockSeats(@ToolParam(required = true, description = "抢座参数：sessionId必传；单座传seatRow/seatCol；多座传seats数组") BookSeatQuery query,
                            ToolContext toolContext) {
        Long userId = resolveUserId(toolContext);
        if (userId == null) {
            return "用户未登录，无法抢座";
        }
        try {
            checkSessionBookable(query.getSessionId());
            uhService.goToSeat(toSoldSeatDTO(query), userId);
            return "座位锁定成功！" + describeSeats(query) + " 已为你保留，请尽快提交订单（约5分钟内有效）~";
        } catch (Exception e) {
            return "抢座失败：" + e.getMessage();
        }
    }

    @Tool(description = "提交订单（生成待支付订单，不支付）。前置条件：必须先调用lockSeats成功锁定同一批座位，且用户已明确确认下单。" +
            "下单成功后生成待支付订单，座位正式落库；订单15分钟内未支付会自动取消并释放座位。" +
            "返回订单号和总金额，引导用户前往订单页完成支付。座位参数与lockSeats保持一致（单座seatRow/seatCol，多座seats数组）。")
    public String submitOrder(@ToolParam(required = true, description = "下单参数：sessionId必传；单座传seatRow/seatCol；多座传seats数组") BookSeatQuery query,
                              ToolContext toolContext) {
        Long userId = resolveUserId(toolContext);
        if (userId == null) {
            return "用户未登录，无法下单";
        }
        try {
            checkSessionBookable(query.getSessionId());
            Map<String, Object> order = uhService.seatYes(toSoldSeatDTO(query), userId);
            return "下单成功！订单号：" + order.get("orderNo") + "，共" + order.get("totalPrice") + "元。" +
                    "这是待支付订单，请在15分钟内到订单页完成支付，超时座位将自动释放哦~";
        } catch (Exception e) {
            return "下单失败：" + e.getMessage();
        }
    }

    @Tool(description = "释放当前登录用户已锁定的座位。用户锁定座位后反悔、想换座或暂不下单时调用，释放后可重新选座。座位参数与lockSeats保持一致。")
    public String releaseSeats(@ToolParam(required = true, description = "释放参数：sessionId必传；单座传seatRow/seatCol；多座传seats数组") BookSeatQuery query,
                               ToolContext toolContext) {
        Long userId = resolveUserId(toolContext);
        if (userId == null) {
            return "用户未登录，无法释放座位";
        }
        try {
            uhService.releaseSeat(toSoldSeatDTO(query), userId);
            return "座位已释放，" + describeSeats(query) + " 可重新选座~";
        } catch (Exception e) {
            return "释放失败：" + e.getMessage();
        }
    }

    @Tool(description = "支付当前登录用户的待支付订单。用户说'支付订单/付款/去支付/买单'时调用。" +
            "参数传订单ID（纯数字）或订单编号（字符串）均可。只有待支付(status=0)且归属当前用户的订单才能支付，" +
            "支付成功后订单变为已支付，并从超时取消队列移除，座位正式保留。")
    public String payOrder(@ToolParam(required = true, description = "订单ID（纯数字）或订单编号（字符串）") CancelOrderQuery query,
                           ToolContext toolContext) {
        Long currentUserId = resolveUserId(toolContext);
        if (currentUserId == null) {
            return "用户未登录，无法支付";
        }
        try {
            Order order = findOrder(query.getOrderIdOrNo());
            if (order == null) {
                return "未找到该订单，请检查订单ID或订单编号是否正确";
            }
            if (!order.getUserId().equals(currentUserId)) {
                return "该订单不属于当前登录用户，无法支付";
            }
            if (order.getStatus() != 0) {
                String statusDesc = switch (order.getStatus()) {
                    case 1 -> "已支付";
                    case 2 -> "已取消";
                    case 3 -> "已退款";
                    default -> "未知状态";
                };
                return "该订单当前状态为" + statusDesc + "，只有待支付的订单才能支付";
            }
            orderBizService.setOrder(order.getOrderNo());
            return "支付成功！订单编号：" + order.getOrderNo() + "，座位已正式保留，祝观影愉快~";
        } catch (Exception e) {
            return "支付失败：" + e.getMessage();
        }
    }

    /**
     * 从流式执行上下文取当前用户ID（流式在线程池线程执行，ThreadLocal不可用）。
     * 优先读 ToolContext，取不到再回退 UserContext（兼容非流式/其他调用方）
     */
    private Long resolveUserId(ToolContext toolContext) {
        if (toolContext != null && toolContext.getContext() != null) {
            Object v = toolContext.getContext().get("userId");
            if (v instanceof Number n) {
                return n.longValue();
            }
            if (v instanceof String s) {
                try {
                    return Long.parseLong(s);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return UserContext.get();
    }

    /** 按订单ID（纯数字）或订单编号（字符串）查订单 */
    private Order findOrder(String orderIdOrNo) {
        try {
            Long orderId = Long.parseLong(orderIdOrNo);
            return orderService.getById(orderId);
        } catch (NumberFormatException e) {
            return orderService.query()
                    .eq("order_no", orderIdOrNo)
                    .one();
        }
    }

    private void checkSessionBookable(Long sessionId) {
        if (sessionId == null) {
            throw new RuntimeException("场次ID不能为空");
        }
        Session s = sessionMapper.getById(sessionId);
        if (s == null) {
            throw new RuntimeException("场次不存在");
        }
        if (s.getStatus() == null || s.getStatus() != 1) {
            throw new RuntimeException("该场次当前已停售或已结束，无法购票");
        }
        if (s.getStartTime() != null && s.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("该场次已开场，无法购票");
        }
    }

    private SoldSeatDTO toSoldSeatDTO(BookSeatQuery query) {
        SoldSeatDTO dto = new SoldSeatDTO();
        dto.setSessionId(query.getSessionId());
        dto.setSeatRow(query.getSeatRow());
        dto.setSeatCol(query.getSeatCol());
        dto.setSeats(query.getSeats());
        return dto;
    }

    private String describeSeats(BookSeatQuery query) {
        List<String> parts = new ArrayList<>();
        if (query.getSeats() != null && !query.getSeats().isEmpty()) {
            for (SeatDTO s : query.getSeats()) {
                parts.add(s.getSeatRow() + "排" + s.getSeatCol() + "座");
            }
        } else if (query.getSeatRow() != null && query.getSeatCol() != null) {
            parts.add(query.getSeatRow() + "排" + query.getSeatCol() + "座");
        }
        return String.join("、", parts);
    }

    private int[] parseLayout(String seatLayout, Integer capacity) {
        if (seatLayout != null && seatLayout.contains("rows")) {
            Pattern p = Pattern.compile("\"rows\"\\s*:\\s*(\\d+).*?\"cols\"\\s*:\\s*(\\d+)", Pattern.DOTALL);
            Matcher m = p.matcher(seatLayout);
            if (m.find()) {
                int rows = Integer.parseInt(m.group(1));
                int cols = Integer.parseInt(m.group(2));
                if (rows > 0 && cols > 0) {
                    return new int[]{rows, cols};
                }
            }
        }
        int cols = 10;
        int rows = (int) Math.ceil((capacity == null ? 0 : capacity) * 1.0 / cols);
        return new int[]{Math.max(rows, 1), cols};
    }
}