/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/14 23:16
 * @version 1.0
 */

package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.*;
import com.example.service.DepartmentService;
import com.example.service.EmployeeService;
import com.example.service.UserService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;
    @Resource
    private UserService userService;
    @Resource
    private DepartmentService departmentService;




    @GetMapping("/selectHeaders")
    public Result selectHeaders(){
        List<Employee> employee =employeeService.selectHeaders();
        return Result.success(employee);
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Employee employee) {


        if (employee.getLevel() == null && employee.getDepartmentId() == null){
            employeeService.add(employee);
        }
        else{
            //根据部门id去找部门
            Department department = departmentService.selectById(employee.getDepartmentId());
            if(department.getEmployeeId() != null && employee.getLevel() == 2){
                //如果有部长，并且希望成为部长，那就over
                return Result.error(ResultCodeEnum.EMPLOYEE_HAVE_LEADER_ERROR);
            }
            else if(department.getEmployeeId() == null && employee.getLevel() == 2) {
                //如果没有部长，并且希望成为部长，
                department.setEmployeeId(employee.getId());
                employeeService.add(employee);
            }else{
                employeeService.add(employee);
            }
            departmentService.updateById(department);
        }
        Employee employee1 = employeeService.selectByUsername(employee.getUsername());
        User user = userService.change(employee1);
        userService.add(user);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        employeeService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        for (Integer id : ids) {
            employeeService.deleteById(id);
        }
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Employee employee) {
        //提交数据不准确
        employeeService.updateById(employee);
//        if((employee.getLevel() == null && employee.getDepartmentId() != null) ||
//                (employee.getLevel() != null && employee.getDepartmentId() == null) ){
//            return Result.error(ResultCodeEnum.EMPLOYEE_D_L_ERROR);
//        }
//
//        // 根据id查找出原先的level,数据库的
//        Employee employee_o = employeeService.selectById(employee.getId());
//        Integer level = employee_o.getLevel();
//        Integer departmentId = employee_o.getDepartmentId();
//
//        if (employee.getLevel() == null && employee.getDepartmentId() == null){
//            employeeService.updateById(employee);
//            User user = userService.change(employee);
//            userService.updateByRoleid(user);
//        }
//        else{
//            //根据部门id去找部门
//            Department department = departmentService.selectById(employee.getDepartmentId());
//
//            if(department.getEmployeeId() != null && employee.getLevel() == 2 && !Objects.equals(employee.getDepartmentId(), departmentId)){
//                //如果有部长，并且希望成为部长，那就over
//                return Result.error(ResultCodeEnum.EMPLOYEE_HAVE_LEADER_ERROR);
//            }
//
//            else{
//
//                Department department1 = departmentService.selectById(employee.getDepartmentId());
//                //原来是部长，现在需要修改为职工，删除部门表绑定的部长
//                if(level == 2 && employee.getLevel() == 1 && employee.getDepartmentId().equals(departmentId)){
//                    department1.setEmployeeId(null);
//                    departmentService.updateById(department1);
//                }//原来不是部长，现在成为部长，绑定id到部门
//                else if(level != 2 && employee.getLevel() == 2 && employee.getDepartmentId().equals(departmentId)){
//                    department1.setEmployeeId(employee.getId());
//                    departmentService.updateById(department1);
//                }
//                //原来没有部长
//                employeeService.updateById(employee);
//                User user = userService.change(employee);
//                userService.updateByRoleid(user);
//            }
//        }
        return Result.success();
    }


    /**
     * 修改个人信息，
     */
    @PutMapping("/update1")
    public Result updateById1(@RequestBody Employee employee) {
        employeeService.updateById1(employee);
        User user = userService.change(employee);
        userService.updateByRoleid(user);
        return Result.success();
    }
    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Employee employee = employeeService.selectById(id);
        return Result.success(employee);
    }

    @GetMapping("/selectByUsername/{username}")
    public Result selectById(@PathVariable String username) {
        Employee employee = employeeService.selectByUsername(username);
        if(employee  == null){
            return Result.success();
        }else{
            return Result.error(ResultCodeEnum.USER_EXIST_ERROR);
        }
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Employee employee ) {
        List<Employee> list = employeeService.selectAll(employee);
        return Result.success(list);
    }

    /**
     * 查询当前用户部门等级
     * @return
     */
    @GetMapping("/selectPowerName")
    public Result selectPowerName(){
        Account currentUser = TokenUtils.getCurrentUser();
        if(RoleEnum.USER.name().equals(currentUser.getRole())) {
            Employee employee = employeeService.selectByIdPower();
            return Result.success(employee);
        }
        else{
            return Result.success("用户是管理员，不做处理");
        }
    }
    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Employee employee,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Employee> page = employeeService.selectPage(employee, pageNum, pageSize);
        return Result.success(page);
    }

}