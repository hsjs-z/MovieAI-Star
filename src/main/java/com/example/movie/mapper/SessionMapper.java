package com.example.movie.mapper;

import com.example.movie.dto.*;
import com.example.movie.entity.Session;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/26 下午9:03
 * 描述:。。。
 */
@Mapper
public interface SessionMapper {
    Page<SessionDTO> getSessionPage(SessionPageQueryDTO pageQueryDTO);

    void addSession(Session session);

    int countConflict(Long hallId, LocalDateTime startTime, LocalDateTime endTime);

    @Select("select * from session where id = #{id}")
    Session getById(Long id);

    @Update("update session set status = #{status} where id = #{id}")
    void updateSession(Session session);


    @Delete("delete from session where id = #{id}")
    void delById(Long id);

    @Select("SELECT DISTINCT h.location " +
            "FROM session s " +
            "JOIN hall h ON h.id = s.hall_id " +
            "WHERE s.movie_id = #{movieId} " +
            "AND s.status != 2 " +          // 排除已结束
            "AND h.location IS NOT NULL")
    List<MovieUtilsDTO> selcinemove(Long movieId);

    @Select("SELECT COUNT(*) from hall h join `session` s ON h.id = s.hall_id " +
            "WHERE s.`status` = 1 " +
            "AND h.id = #{hallId} " +
            "AND s.start_time > NOW()")
    int getSessis(Long id);

    @Select("SELECT s.id, s.movie_id, m.name AS movieName, m.poster_url AS posterUrl, " +
            "s.hall_id, h.name AS hallName, h.location, " +
            "s.start_time AS startTime, s.end_time AS endTime, " +
            "s.price, s.language_version AS languageVersion, s.status, " +
            "s.create_time AS createTime, s.update_time AS updateTime " +
            "FROM session s " +
            "JOIN movie m ON m.id = s.movie_id " +
            "JOIN hall h ON h.id = s.hall_id " +
            "WHERE s.id = #{sessionId}")
    SessionDetailDTO selectSessionDetail(Long sessionId);

    @Select("SELECT oi.seat_row AS seatRow, oi.seat_col AS seatCol, " +
            "o.order_no AS orderNo, o.status AS orderStatus " +
            "FROM order_item oi " +
            "JOIN `order` o ON o.id = oi.order_id " +
            "WHERE oi.session_id = #{sessionId} " +
            "AND o.status NOT IN (2, 3)")  // 2已取消 3已退款不算
    List<SoldSeatDTO> selectSoldSeats(Long sessionId);

    @Select("SELECT COUNT(*) FROM order_item oi " +
            "JOIN `order` o ON o.id = oi.order_id " +
            "WHERE oi.session_id = #{sessionId} " +
            "AND o.status NOT IN (2, 3)")
    int likeOrderCount(Long id);

    void updateSessions(Session session);

    Integer getSession(Long id);

    int ifsession(LocalDateTime time);
}
