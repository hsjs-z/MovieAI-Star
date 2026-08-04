package com.example.movie.mapper;

import com.example.movie.entity.Notice;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 作者: 枷
 * 时间:2026/8/1 下午1:43
 * 描述:。。。
 */
@Mapper
public interface NoticeMapper {
    @Select("select * from notice")
    Page<Notice> getNoticePage();

    void insert(Notice notice);

    @Select("select * from notice where id = #{id}")
    Notice selectById(Long id);

    void updateById(Notice notice);

    @Delete("delete from notice where id = #{id}")
    int deleteById(Long id);
}
