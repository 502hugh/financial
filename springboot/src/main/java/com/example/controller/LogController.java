/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/16 14:30
 * @version 1.0
 */

package com.example.controller;

import com.example.common.Result;
import com.example.entity.Admin;
import com.example.entity.Aspect.Log;
import com.example.entity.Salary;
import com.example.service.LogAspectService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController

@RequestMapping("/log")
public class LogController {

    @Resource
    private LogAspectService logAspectService;


    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        logAspectService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        logAspectService.deleteBatch(ids);
        return Result.success();
    }


    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Log log,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Log> page = logAspectService.selectPage(log, pageNum, pageSize);
        return Result.success(page);
    }

}
