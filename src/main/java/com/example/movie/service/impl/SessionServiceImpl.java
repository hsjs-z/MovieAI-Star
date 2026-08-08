package com.example.movie.service.impl;

import com.example.movie.common.result.PageResult;
import com.example.movie.dto.*;
import com.example.movie.entity.Session;
import com.example.movie.mapper.MovieMapper;
import com.example.movie.mapper.SessionMapper;
import com.example.movie.service.HallService;
import com.example.movie.service.MovieService;
import com.example.movie.service.SessionService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/26 下午8:59
 * 描述:。。。
 */
@Service
@Slf4j
public class SessionServiceImpl implements SessionService {
    @Autowired
    private SessionMapper sessionMapper;
    @Autowired
    private MovieService movieService;
    @Autowired
    private HallService hallService;
    @Autowired
    private MovieMapper movieMapper;

    /**
     * 根据条件查询排片分页列表
     * @param pageQueryDTO
     * @return
     */
    public PageResult getSessionPage(SessionPageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        LocalDateTime end = pageQueryDTO.getEndTime();
        if(end != null){
            // 把 00:00:00 → 23:59:59
            end = end.toLocalDate().atTime(23,59,59);
        }
        log.info("end{}",end);
        pageQueryDTO.setEndTime(end);
        Page<SessionDTO> page = sessionMapper.getSessionPage(pageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增排片
     * @param dto
     */
    public void addSession(SessionAddDTO dto) {
        //结束时间大于开始时间
        if (dto.getEndTime().isBefore(dto.getStartTime())
                || dto.getEndTime().equals(dto.getStartTime())) {
            throw new RuntimeException("结束时间必须大于开始时间");
        }
        //电影不为空或下架
        MovieDTO movie = movieService.getMovieById(dto.getMovieId());
        if (movie == null || movie.getStatus() != 1) {
            throw new RuntimeException("电影不存在或已下架");
        }
        //影厅不为空
        HallDTO hall = hallService.getHallById(dto.getHallId());
        if (hall == null) {
            throw new RuntimeException("影厅不存在");
        }
        //判断是否与已存在的排片时间冲突
        int conflictCount = sessionMapper.countConflict(
                dto.getHallId(),
                dto.getStartTime(),
                dto.getEndTime()
        );
        if (conflictCount > 0) {
            throw new RuntimeException("影厅在该时间段已有排片");
        }

        Session session = new Session();
        BeanUtils.copyProperties(dto, session);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        //默认状态为上架
        session.setStatus(1);
        sessionMapper.addSession(session);
    }

    /**
     * 根据ID获取排片信息
     * @param id
     * @return
     */
    public SessionAddDTO getById(Long id) {
        Session session = sessionMapper.getById(id);
        SessionAddDTO dto = new SessionAddDTO();
        BeanUtils.copyProperties(session, dto);
        return dto;
    }

    @Transactional
    /**
     * 修改排片信息
     * @param sessionaddDTO
     */
    public void updateSession(SessionAddDTO sessionaddDTO) {
        Session test = sessionMapper.getById(sessionaddDTO.getId());
        if (test == null || test.getStatus().equals(2)) {
            throw new RuntimeException("排片不存在或已结束");
        }
        // 校验：只能编辑未来场次
        if (test.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("只能编辑未来时间的场次");
        }
        log.info("修改排片{}", test);
        Session session = new Session();
        BeanUtils.copyProperties(sessionaddDTO, session);
        session.setUpdateTime(LocalDateTime.now());
        sessionMapper.updateSession(session);
    }

    /**
     * 修改排片状态
     * @param id
     * @param status
     */
    @Transactional
    public void updaStatus(Integer status, Long id) {
        if (id == null){
            throw new RuntimeException("id不能为空");
        }
        Session session = sessionMapper.getById(id);
        if (session == null || session.getStatus() == 2) {
            throw new RuntimeException("排片不存在或已结束");
        }
        session.setStatus(status);
        sessionMapper.updateSession(session);
    }

    @Transactional
    /**
     * 删除排片
     * @param id
     */
    public void deleteSession(Long id) {
        Session session = sessionMapper.getById(id);
        if (session == null) {
            throw new RuntimeException("排片不存在");
        }
        // 校验：是否存在有效订单
        int orderCount = sessionMapper.likeOrderCount(id);
        if (orderCount > 0) {
            throw new RuntimeException("该场次已有订单，无法删除");
        }

        sessionMapper.delById(id);
    }

    /**
     * 根据排片ID查询排片详情
     * @param sessionId
     * @return
     */
    public SessionDetailDTO getSessionDetail(Long sessionId) {
        SessionDetailDTO dto = sessionMapper.selectSessionDetail(sessionId);
        if (dto == null) {
            throw new RuntimeException("排片不存在");
        }
        return dto;
    }

    /**
     * 查询已售座位
     * @param sessionId
     * @return
     */
    public List<SoldSeatDTO> getSoldSeats(Long sessionId) {
        return sessionMapper.selectSoldSeats(sessionId);
    }
}
