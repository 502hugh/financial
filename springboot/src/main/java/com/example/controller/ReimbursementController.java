/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/27 20:44
 * @version 1.0
 */

package com.example.controller;

import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Resources;
import com.example.entity.voucher.Reimbursement;
import com.example.entity.voucher.ReimbursementApply;
import com.example.entity.voucher.VoucherPicture;
import com.example.service.ReimbursementApplyService;
import com.example.service.ReimbursementService;
import com.example.service.VoucherPictureService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/reimbursement")
public class ReimbursementController {

    @Resource
    private  ReimbursementService reimbursementService;

    @Resource
    private VoucherPictureService voucherPictureService;

    @Resource
    private ReimbursementApplyService reimbursementApplyService;


    /**
     * 找附件图片
     */
    @GetMapping("/selectPictureByReimbursementId/{id}")
    public Result selectPictureByReimbursementId(@PathVariable Integer id){
        List<VoucherPicture> voucherPicture = voucherPictureService.selectByReimbursementId(id);
        return Result.success(PageInfo.of(voucherPicture));

    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Reimbursement reimbursement) {
        reimbursementService.add(reimbursement);
        return Result.success();
    }


    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Reimbursement reimbursement) {
        reimbursementService.updateById(reimbursement);
        return Result.success();
    }

    @PutMapping("/updateByAdmin")
    public Result updateByAdmin(@RequestBody Reimbursement reimbursement) {
        reimbursementService.updateByAdmin(reimbursement);
        return Result.success();
    }

    /**
     * 提交
     */
    @PutMapping("/submitted")
    public Result submitted(@RequestBody Reimbursement reimbursement) {
        reimbursementService.submitted(reimbursement);
        return Result.success();
    }



    //删除
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        reimbursementService.deleteById(id);
        return Result.success();
    }

    //批量删除
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        for(int i : ids){
            Reimbursement reimbursement = reimbursementService.selectById(i);
            if(reimbursement.getSubmitted() == 1){
                return Result.error(ResultCodeEnum.EMPLOYEE_APPLYING_NO_DELETE);
            }
        }
        reimbursementService.deleteBatch(ids);
        return Result.success();
    }

    //分页
    @GetMapping("/selectPage")
    public Result selectPage(Reimbursement reimbursement,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Reimbursement> page = reimbursementService.selectPage(reimbursement, pageNum, pageSize);
        return Result.success(page);
    }


    @GetMapping("/selectPageApply")
    public Result selectPageApply(Reimbursement reimbursement,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Reimbursement> page = reimbursementService.selectPageApply(reimbursement, pageNum, pageSize);
        return Result.success(page);
    }
}
