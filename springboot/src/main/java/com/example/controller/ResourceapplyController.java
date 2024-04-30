/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 23:51
 * @version 1.0
 */

package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.*;
import com.example.entity.*;
import com.example.service.DepartmentService;
import com.example.service.EmployeeService;
import com.example.service.ResourceapplyService;
import com.example.service.ResourcesService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/resourceapply")
public class ResourceapplyController {

    @Resource
    private ResourceapplyService resourceapplyService;

    @Resource
    private EmployeeService employeeService;
    @Resource
    private DepartmentService departmentService;

    @Resource
    private ResourcesService resourcesService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Resourceapply resourceapply) {
        resourceapplyService.add(resourceapply);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        resourceapplyService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        resourceapplyService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Resourceapply resourceapply) {
        resourceapplyService.updateById(resourceapply);
        return Result.success();
    }

    @PutMapping("/updateByAdmin")
    public Result updateByAdmin(@RequestBody Resourceapply resourceapply) {
        resourceapplyService.updateByAdmin(resourceapply);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Resourceapply resourceapply = resourceapplyService.selectById(id);
        return Result.success(resourceapply);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Resourceapply resourceapply ) {
        List<Resourceapply> list = resourceapplyService.selectAll(resourceapply);
        return Result.success(list);
    }

    /**
     * 申请资产分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Resourceapply resourceapply,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Resourceapply> page = resourceapplyService.selectPage(resourceapply, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 申请审批分页查询
     */
    @GetMapping("/selectPage2")
    public Result selectPage2(Resourceapply resourceapply,
                              @RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Resourceapply> page = resourceapplyService.selectPage2(resourceapply, pageNum, pageSize);
        return Result.success(page);
    }
}