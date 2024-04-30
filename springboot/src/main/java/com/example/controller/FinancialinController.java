/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/22 14:07
 * @version 1.0
 */

package com.example.controller;

import com.example.common.Result;
import com.example.entity.Financialin;
import com.example.service.FinancialinService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/financialin")
public class FinancialinController {
    @Resource
    private FinancialinService financialinService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Financialin financialin) {
        financialinService.add(financialin);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        financialinService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        financialinService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Financialin financialin) {
        financialinService.updateById(financialin);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Financialin financialin = financialinService.selectById(id);
        return Result.success(financialin);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Financialin financialin ) {
        List<Financialin> list = financialinService.selectAll(financialin);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Financialin financialin,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Financialin> page = financialinService.selectPage(financialin, pageNum, pageSize);
        return Result.success(page);
    }
}
