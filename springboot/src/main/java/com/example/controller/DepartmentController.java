/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 21:25
 * @version 1.0
 */

package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Department;
import com.example.entity.Employee;
import com.example.service.DepartmentService;
import com.example.service.EmployeeService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/department")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @Resource
    private EmployeeService employeeService;
    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Department department) {
        departmentService.add(department);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        departmentService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        for(int i : ids){
            if(i == 0){
                return Result.success(ResultCodeEnum.DEPARTMENT_DELET_ERORR);
            }
        }
        departmentService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Department department) {
            departmentService.updateById(department);
            return Result.success();

    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Department department = departmentService.selectById(id);
        return Result.success(department);
    }

    /**
     * 根据员工ID查询
     */
    @GetMapping("/selectByEmployeeId/{id}")
    public Result selectByEmployeeId(@PathVariable Integer id) {
        Department department = departmentService.selectByEmployeeId(id);
        if(department  == null){
            return Result.success();
        }else{
            return Result.error(ResultCodeEnum.EMPLOYEE_HAVE_DEPARTMENT_ERORR);
        }
    }
    /**
     * 根据员工ID查询
     */
    @GetMapping("/selectByPowerName/{powerName}")
    public Result selectByPowerName(@PathVariable String powerName) {
        Department department = departmentService.selectByPowerName(powerName);
        if(department  == null){
            return Result.success();
        }else{
            return Result.error(ResultCodeEnum.EMPLOYEE_HAVE_POWERNAME_ERORR);
        }
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Department department ) {
        List<Department> list = departmentService.selectAll(department);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Department department,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Department> page = departmentService.selectPage(department, pageNum, pageSize);
        return Result.success(page);
    }

}