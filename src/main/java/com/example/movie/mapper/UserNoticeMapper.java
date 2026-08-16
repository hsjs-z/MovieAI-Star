package com.example.movie.mapper;

import com.example.movie.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 作者: 枷
 * 时间:2026/8/16 下午3:02
 * 描述:。。。
 */
@Mapper
public interface UserNoticeMapper {
    @Select("select * from notice where status = 1")
    List<Notice> getLine();
}
