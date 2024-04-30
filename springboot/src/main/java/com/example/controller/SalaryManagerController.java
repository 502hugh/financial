/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/3 23:44
 * @version 1.0
 */

package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Admin;
import com.example.entity.SalaryManager;
import com.example.entity.User;
import com.example.service.SalaryManagerService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/salaryManager")
public class SalaryManagerController {


    @Resource
    private SalaryManagerService salaryManagerService;
    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody SalaryManager salaryManager) {
        salaryManagerService.add(salaryManager);
        return Result.success();
    }

    @PostMapping("/grant")
    public Result grant(@RequestBody SalaryManager salaryManager) {
        salaryManagerService.grant(salaryManager);
        return Result.success();
    }


    @PostMapping("/grantOne/{id}")
    public Result grantOne(@PathVariable Integer id) {
        System.out.println("=============================================="+ id);
        salaryManagerService.grantOne(id);
        return Result.success();
    }




    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody SalaryManager salaryManager) {
        salaryManagerService.updateById(salaryManager);
        return Result.success();
    }


    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        salaryManagerService.deleteBatch(ids);
        return Result.success();
    }



    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        salaryManagerService.deleteById(id);
        return Result.success();
    }


    @GetMapping("/selectByEmployeeId/{employeeId}")
    public Result selectByEmployeeId(@PathVariable Integer employeeId){
        SalaryManager salaryManager = salaryManagerService.selectByEmployeeId(employeeId);
        if(salaryManager == null){
            return Result.success();
        }else{
            return Result.error(ResultCodeEnum.SALARY_MANAGER_HAVE_ERROR);
        }
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(SalaryManager  salaryManager,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<SalaryManager> page =  salaryManagerService.selectPage( salaryManager, pageNum, pageSize);
        return Result.success(page);
    }



}
