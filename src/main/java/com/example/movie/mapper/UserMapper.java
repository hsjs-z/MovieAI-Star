package com.example.movie.mapper;

import com.example.movie.dto.UserDTO;
import com.example.movie.dto.UserPageQueryDTO;
import com.example.movie.entity.User;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 作者: 枷
 * 时间:2026/7/29 上午11:39
 * 描述:。。。
 */
@Mapper
public interface UserMapper {
    Page<UserDTO> getPageUser(UserPageQueryDTO dto);

    @Select("select * from user where id = #{id}")
    User getById(Long id);

    void updateUser(User user);

    @Update("update user set status = #{status} where id = #{id}")
    void updatestatus(User user);

    @Update("update user set password = #{password} where id = #{id}")
    void updatepassword(User user);

    @Delete("delete from user where id = #{id}")
    void deleteUserById(Long id);
}
