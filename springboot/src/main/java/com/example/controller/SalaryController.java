/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 22:51
 * @version 1.0
 */

package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.entity.Financialout;
import com.example.entity.Salary;
import com.example.service.SalaryService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/salary")
public class SalaryController {

    @Resource
    private SalaryService salaryService;




    @GetMapping("/getLine")
    public Result line() {
        Map<String, Object> result = salaryService.line();
        return Result.success(result);
    }
    @GetMapping("/getPie")
    public Result pie() {

        Map<String, Object> result = salaryService.pie();
        return Result.success(result);
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Salary salary) {
        salaryService.add(salary);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        salaryService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        salaryService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Salary salary) {
        salaryService.updateById(salary);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Salary salary = salaryService.selectById(id);
        return Result.success(salary);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Salary salary ) {
        List<Salary> list = salaryService.selectAll(salary);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Salary salary,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Salary> page = salaryService.selectPage(salary, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/getMonth")
    public Result getMonth() {
        List<Salary> list = salaryService.getMonth();
        return Result.success(list);
    }

    @GetMapping("/getSalary/{month}")
    public Result getSalaryMonth(@PathVariable String month) {
        Salary salary = new Salary();
        salary.setYear(month);
        List<Salary> list = salaryService.selectAll(salary);

        Map<String, Double> collect = list.stream().filter(x -> ObjectUtil.isNotEmpty(x.getDepartmentName()))
                .collect(Collectors.groupingBy(Salary::getDepartmentName, Collectors.reducing(0.0, Salary::getPrice, Double::sum)));
        List<String> xAxis = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        for (String key : collect.keySet()) {
            xAxis.add(key);
            data.add(collect.get(key));
        }
        Map<String, Object> result = new HashMap<>();
        result.put("text", "部门薪资柱状图");
        result.put("subtext", "统计维度：部门");
        result.put("xAxis", xAxis);
        result.put("yAxis", data);
        return Result.success(result);
    }

}
