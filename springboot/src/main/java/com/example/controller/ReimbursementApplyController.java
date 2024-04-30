/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/2 12:49
 * @version 1.0
 */

package com.example.controller;


import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.common.enums.*;
import com.example.entity.*;
import com.example.entity.voucher.Reimbursement;
import com.example.entity.voucher.ReimbursementApply;
import com.example.service.*;
import com.example.utils.TokenUtils;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/reimbursementApply")
public class ReimbursementApplyController {

    @Resource
    private ReimbursementApplyService reimbursementApplyService;

    @Resource
    private ReimbursementService reimbursementService;

    @Resource
    private EmployeeService employeeService;
    @Resource
    private DepartmentService departmentService;

    @Resource
    private FinancialoutService financialoutService;


    @Resource
    private ResourcesService resourcesService;

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateSubmit(@RequestBody ReimbursementApply reimbursementApply) {
        reimbursementApplyService.updateSubmit(reimbursementApply);
        return Result.success();
    }


}
