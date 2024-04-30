package com.example.mapper;

import com.example.entity.Admin;
import com.example.entity.Employee;
import com.example.entity.User;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface UserMapper {

    int insert(User user);

    /**
     * 删除
     */
    int deleteByRoleid(String roleid);

    /**
     * 修改
     */
    void updateByRoleid(User user);
    /**
     * 根据用户名查询
     */
    @Select("select * from user where username = #{username}")
    User selectByUsername(String username);


    List<User> selectAll(User user);


    void deleteByUsername(String username);

}
