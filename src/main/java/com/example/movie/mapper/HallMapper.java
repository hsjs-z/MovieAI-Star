package com.example.movie.mapper;

import com.example.movie.dto.HallDTO;
import com.example.movie.dto.HallPageQueryDTO;
import com.example.movie.entity.Hall;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/7/25 下午10:12
 * 描述:。。。
 */
@Mapper
public interface HallMapper {
    Page<HallDTO> getHallPage(HallPageQueryDTO pageQueryDTO);

    void addHall(Hall hall);

    @Select("select * from hall where id = #{id}")
    Hall getHallById(Long id);

    void updateHall(Hall hall);

    @Delete("delete from hall where id = #{id}")
    void delHall(Long id);

    @Select("select id,name from hall")
    List<HallDTO> gethallName();

    @Select("select * from hall")
    List<Hall> getHallCon();
}
