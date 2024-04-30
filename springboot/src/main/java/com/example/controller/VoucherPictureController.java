/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/29 19:28
 * @version 1.0
 */

package com.example.controller;


import com.example.common.Result;
import com.example.entity.Account;
import com.example.entity.Employee;
import com.example.entity.voucher.Reimbursement;
import com.example.entity.voucher.VoucherPicture;
import com.example.service.EmployeeService;
import com.example.service.VoucherPictureService;
import com.example.utils.TokenUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController

@RequestMapping("/voucherPicture")
public class VoucherPictureController {

    @Resource
    private VoucherPictureService voucherPictureService;

    @Resource
    private EmployeeService employeeService;


    /**
     * 查找
     */
    @GetMapping("/selectByReimbursementId/{id}")
    public Result selectByReimbursementId(@PathVariable Integer id) {
        voucherPictureService.selectByReimbursementId(id);
        return Result.success();
    }
    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        voucherPictureService.deleteById(id);
        return Result.success();
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody VoucherPicture voucherPicture) {
        System.out.println("==================================================add");
        System.out.println(voucherPicture.toString());
        voucherPictureService.add(voucherPicture);
        return Result.success();
    }


    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody VoucherPicture voucherPicture) {
        voucherPictureService.updateById(voucherPicture);
        return Result.success();
    }
}
