package com.example.mapper;


import com.example.entity.Salary;
import com.example.entity.SalaryManager;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SalaryManagerMapper {

    void deleteById(Integer id);

    SalaryManager selectById(Integer id);

    void updateById(SalaryManager salaryManager);

    void insert(SalaryManager salaryManager);

    List<SalaryManager> selectAll(SalaryManager salaryManager);

    SalaryManager selectByEmployeeId(Integer employeeId);

    List<Salary> selectSalary();

    Salary selectSalaryOne(Integer id);

    void deleteByEmployeeId(Integer EmployeeId);
}
