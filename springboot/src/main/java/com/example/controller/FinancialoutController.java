/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 23:09
 * @version 1.0
 */

package com.example.controller;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.Result;
import com.example.entity.Financialin;
import com.example.entity.Financialout;
import com.example.service.FinancialinService;
import com.example.service.FinancialoutService;
import com.example.utils.MyDateUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/financialout")
public class FinancialoutController {

        @Resource
        private FinancialoutService financialoutService;

        @Resource
        private FinancialinService financialinService;

        /**
         * 新增
         */
        @PostMapping("/add")
        public Result add(@RequestBody Financialout financialout) {
            financialoutService.add(financialout);
            return Result.success();
        }

        /**
         * 删除
         */
        @DeleteMapping("/delete/{id}")
        public Result deleteById(@PathVariable Integer id) {
            financialoutService.deleteById(id);
            return Result.success();
        }

        /**
         * 批量删除
         */
        @DeleteMapping("/delete/batch")
        public Result deleteBatch(@RequestBody List<Integer> ids) {
            financialoutService.deleteBatch(ids);
            return Result.success();
        }

        /**
         * 修改
         */
        @PutMapping("/update")
        public Result updateById(@RequestBody Financialout financialout) {
            financialoutService.updateById(financialout);
            return Result.success();
        }

        /**
         * 根据ID查询
         */
        @GetMapping("/selectById/{id}")
        public Result selectById(@PathVariable Integer id) {
            Financialout financialout = financialoutService.selectById(id);
            return Result.success(financialout);
        }

        /**
         * 查询所有
         */
        @GetMapping("/selectAll")
        public Result selectAll(Financialout financialout ) {
            List<Financialout> list = financialoutService.selectAll(financialout);
            return Result.success(list);
        }

        /**
         * 分页查询
         */
        @GetMapping("/selectPage")
        public Result selectPage(Financialout financialout,
                                 @RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize) {
            PageInfo<Financialout> page = financialoutService.selectPage(financialout, pageNum, pageSize);
            return Result.success(page);
        }


    @GetMapping("/getPie")
    public Result pieOut() {

        Map<String, Object> result = financialoutService.pieOut();
        return Result.success(result);
    }

    @GetMapping("/getLine")
    public Result lineInOut() {
        Map<String, Object> result = financialoutService.lineInOut();
        return Result.success(result);
    }



    @GetMapping("/getBar")
    public Result barIn() {

        Map<String, Object> result = financialinService.barIn();
        return Result.success(result);
    }




}
