/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 22:50
 * @version 1.0
 */

package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Employee;
import com.example.entity.Financialin;
import com.example.entity.Salary;
import com.example.exception.CustomException;
import com.example.mapper.EmployeeMapper;
import com.example.mapper.SalaryMapper;
import com.example.utils.MyDateUtils;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SalaryService {

    @Resource
    private SalaryMapper salaryMapper;
    @Resource
    private EmployeeMapper employeeMapper;

    /**
     * 新增
     */
    public void add(Salary salary) {
        Integer employeeId = salary.getEmployeeId();
        Employee employee = employeeMapper.selectById(employeeId);
        salary.setDepartmentId(employee.getDepartmentId());
        salaryMapper.insert(salary);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        salaryMapper.deleteById(id);
        //回收工资/财务收入
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            salaryMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Salary salary) {
        salaryMapper.updateById(salary);
    }

    /**
     * 根据ID查询
     */
    public Salary selectById(Integer id) {
        return salaryMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Salary> selectAll(Salary salary) {
        return salaryMapper.selectAll(salary);
    }




    public List<Salary> selectSalaryExcel() {
        Salary salary = new Salary();
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            salary.setEmployeeId(currentUser.getId());
        }
        return salaryMapper.selectAll(salary);
    }




    /**
     * 分页查询
     */
    public PageInfo<Salary> selectPage(Salary salary, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            salary.setEmployeeId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);

        System.out.println("================================================" + salary.toString());
        List<Salary> list = salaryMapper.selectAll(salary);
        return PageInfo.of(list);
    }

    public List<Salary> getMonth() {
        return salaryMapper.getMonth();
    }

    public Map<String, Object> line() {
        Account currentUser = TokenUtils.getCurrentUser();
        List<Salary> list = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();
        if(RoleEnum.ADMIN.name().equals(currentUser.getRole())){
           list = salaryMapper.selectAll(null);

            result.put("text", "薪资折线图");
            result.put("subtext", "统计维度：月份");
        }else{
            list = salaryMapper.selectByEmployeeId(currentUser.getId());

            result.put("text", "个人薪资折线图");
            result.put("subtext", "统计维度：月份");
        }

        // 获取所有的财务信息
        TreeMap<String, Double> collect = list.stream()
                .filter(x -> ObjectUtil.isNotEmpty(x.getTime().substring(0,7)) && MyDateUtils.isWithinLastYear(x.getTime()))
                .collect(Collectors.groupingBy(salary -> salary.getTime().substring(0, 7),TreeMap::new, Collectors.reducing(0.0, Salary::getPrice, Double::sum)));
        List<String> xAxis = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        for (String key : collect.keySet()) {
            xAxis.add(key.substring(0,7));
            data.add(collect.get(key));
        }
        result.put("xAxis", xAxis);
        result.put("yAxis", data);

        return result;
    }

    public Map<String, Object> pie() {
        Account currentUser = TokenUtils.getCurrentUser();
        Employee employee = employeeMapper.selectById(currentUser.getId());

        List<Salary> list = new ArrayList<Salary>();

        Map<String, Object> result = new HashMap<>();

        if(RoleEnum.ADMIN.name().equals(currentUser.getRole())){
            list = salaryMapper.selectAll(null);
            result.put("text", "薪资饼图");
            result.put("subtext", "统计维度：月份");
            result.put("name", "薪资");
        }else{
            if(employee.getDepartmentId() == null){
                throw new CustomException(ResultCodeEnum.PARAM_LOST_ERROR);
            }
            list = salaryMapper.selectByDepartmentId(employee.getDepartmentId());
            result.put("text", "部门薪资饼图");
            result.put("subtext", "统计维度：月份");
            result.put("name", "薪资");
        }
        // 获取所有的薪资信息
        Map<String, Double> collect = list.stream().filter(x -> ObjectUtil.isNotEmpty(x.getTime().substring(0,7)) && MyDateUtils.isWithinLastYear(x.getTime()))
                .collect(Collectors.groupingBy(salary -> salary.getTime().substring(0, 7), // 根据年月分组
                        Collectors.summingDouble(Salary::getPrice) // 将金额相加
                         ));

        List<Map<String, Object>> data = new ArrayList<>();
        for (String key : collect.keySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", key.substring(0,7));
            map.put("value", collect.get(key));
            data.add(map);
        }


        result.put("data", data);
        return result;
    }


}