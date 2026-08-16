package com.example.movie.service.impl;

import com.example.movie.common.result.PageResult;
import com.example.movie.dto.HallDTO;
import com.example.movie.dto.HallPageQueryDTO;
import com.example.movie.entity.Hall;
import com.example.movie.mapper.HallMapper;
import com.example.movie.mapper.SessionMapper;
import com.example.movie.service.HallService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/25 下午10:11
 * 描述:。。。
 */
@Service
@Slf4j
public class HallServiceImpl implements HallService {
    @Autowired
    private HallMapper hallMapper;
    @Autowired
    private SessionMapper sessionMapper;

    /**
     * 分页查询影厅列表
     * @param pageQueryDTO
     * @return
     */
    public PageResult getHallPage(HallPageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<HallDTO> page = hallMapper.getHallPage(pageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @CacheEvict(value = "hallList", allEntries = true)
    /**
     * 添加影厅
     * @param hallDTO
     */
    public void addHall(HallDTO hallDTO) {
        Hall hall = new Hall();
        BeanUtils.copyProperties(hallDTO, hall);

        // 生成 seat_layout JSON
        String seatLayout = buildSeatLayout(hallDTO.getRowNum(), hallDTO.getColNum());
        hall.setSeatLayout(seatLayout);

        hall.setCreateTime(LocalDateTime.now());
        hallMapper.addHall(hall);
    }

    /**
     * 根据id查询影厅
     * @param id
     * @return
     */
    public HallDTO getHallById(Long id) {
        if (id == null) {
            throw new RuntimeException("id不能为空");
        }
        Hall hall = hallMapper.getHallById(id);
        HallDTO hallDTO = new HallDTO();
        BeanUtils.copyProperties(hall, hallDTO);
        return hallDTO;
    }

    @CacheEvict(value = "hallList", allEntries = true)
    /**
     * 删除影厅
     * @param id
     */
    public void deleteHall(Long id) {
        // 有排片不能删除
        int count = sessionMapper.getSessis(id);
        if (count > 0) {
            throw new RuntimeException("有排片不能修改");
        }

        hallMapper.delHall(id);
    }

    /**
     * 查询影厅名，用于前端回显
     * @return
     */
    public List<HallDTO> getHallName() {
        List<HallDTO> list =  hallMapper.gethallName();
        return list;
    }

    @Cacheable(value = "hallList", key = "'getcountHall'")
    /**
     * 返回影厅
     * @return
     */
    public List<Hall> getHallCon() {
        List<Hall> hall = hallMapper.getHallCon();
        return hall;
    }

    @CacheEvict(value = "hallList", allEntries = true)
    /**
     * 修改影厅
     * @param hallDTO
     */
    public void updateHall(HallDTO hallDTO) {
        // 有排片不能修改
        int count = sessionMapper.getSessis(hallDTO.getId());
        if (count > 0) {
            throw new RuntimeException("有排片不能修改");
        }

        if (hallDTO == null){
            throw new RuntimeException("HallDTO不能为空");
        }
        Hall hall = new Hall();
        BeanUtils.copyProperties(hallDTO, hall);
        hallMapper.updateHall(hall);
    }


    private String buildSeatLayout(Integer rowNum, Integer colNum) {
        int rows = (rowNum == null) ? 8 : rowNum;
        int cols = (colNum == null) ? 10 : colNum;
        return String.format("{\"rows\":%d,\"cols\":%d}", rows, cols);
    }
}
