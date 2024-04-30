package com.example.mapper;

import com.example.entity.Employee;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface EmployeeMapper {

    /**
     * 新增
     */
    int insert(Employee employee);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Employee employee);

    /**
     * 根据ID查询
     */
    Employee selectById(Integer id);

    /**
     * 查询所有
     */
    List<Employee> selectAll(Employee employee);

    @Select("select * from employee where username = #{username}")
    Employee selectByUsername(String username);

    @Select("select * from employee where level = 2 or level = 1")
    List<Employee> selectHeaders();

    Employee selectByIdPower(Employee employee);

    @Select("select * from employee where department_id = #{id}")
    List<Employee> selectByDepartmentId(Integer id);
}