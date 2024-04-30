/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/19 21:37
 * @version 1.0
 */

package com.example.controller;

import com.example.common.Result;
import com.example.entity.Admin;
import com.example.entity.User;
import com.example.entity.Visible;
import com.example.service.VisibleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/visible")
public class VisibleController {


    @Resource
    private VisibleService visibleService;

    @PutMapping("/update")
    public Result updateById(@RequestBody Visible visible) {
        System.out.println("==============================================update"+visible.toString());
        visibleService.updateById(visible);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/select")
    public Result selectById() {
        Integer id = 1;
        Visible visible = visibleService.selectById(id);
        System.out.println("=============================================="+visible.toString());
        return Result.success(visible);
    }





}
