package com.example.mapper;

import com.example.entity.Salary;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SalaryMapper {

    /**
     * 新增
     */
    int insert(Salary salary);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Salary salary);

    /**
     * 根据ID查询
     */
    Salary selectById(Integer id);

    /**
     * 查询所有
     */
    List<Salary> selectAll(Salary salary);

    @Select("select year from salary group by year")
    List<Salary> getMonth();

    List<Salary> selectByEmployeeId(Integer employeeId);

    List<Salary> selectByDepartmentId(Integer departmentId);
}