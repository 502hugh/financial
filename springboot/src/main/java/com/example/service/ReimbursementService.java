/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/27 20:46
 * @version 1.0
 */

package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.common.enums.ApplyEnum;
import com.example.common.enums.ReimbursementEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Employee;
import com.example.entity.Resourceapply;
import com.example.entity.Resources;
import com.example.entity.voucher.Reimbursement;
import com.example.entity.voucher.ReimbursementApply;
import com.example.entity.voucher.VoucherPicture;
import com.example.exception.CustomException;
import com.example.mapper.ReimbursementApplyMapper;
import com.example.mapper.ReimbursementMapper;
import com.example.mapper.VoucherPictureMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ReimbursementService {

    @Resource
    private ReimbursementMapper reimbursementMapper;

    @Resource
    private ReimbursementApplyService reimbursementApplyService;


    @Resource
    private VoucherPictureMapper voucherPictureMapper;


    @Resource
    private EmployeeService employeeService;

    /**
     * 分页
     */
    public PageInfo<Reimbursement> selectPage(Reimbursement reimbursement, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if(RoleEnum.ADMIN.name().equals(TokenUtils.getCurrentUser().getRole())){
            reimbursement.setSubmitted(0);
        }else if(RoleEnum.USER.name().equals(TokenUtils.getCurrentUser().getRole())){
            reimbursement.setEmployeeId(TokenUtils.getCurrentUser().getId());
        }
        List<Reimbursement> list = reimbursementMapper.selectAll(reimbursement);
        System.out.println(list.toString());
        for(Reimbursement reimbursement1 : list){

            if(ApplyEnum.APPLY_DONE.status.equals(reimbursement1.getProcess())){

                if(ApplyEnum.APPLY_OK.status.equals(reimbursement1.getStatus())){
                    reimbursement1.setStatus("审批通过");
                }else{
                    reimbursement1.setStatus("审批不通过");
                }
            }
            else{
                reimbursement1.setStatus("待审批");
            }
        }

        return PageInfo.of(list);
    }

    /**
     * 删除
     *
     * @return
     */

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            reimbursementMapper.deleteById(id);
            voucherPictureMapper.deleteByReimbursementId(id);
        }
    }

    public void deleteById(Integer id) {
        reimbursementMapper.deleteById(id);
        voucherPictureMapper.deleteByReimbursementId(id);

    }

    //新增
    public void add(Reimbursement reimbursement) {
        Account currentUser = TokenUtils.getCurrentUser();
        reimbursement.setTime(DateUtil.now());
        reimbursement.setName(currentUser.getUsername());
        reimbursement.setSubmitted(0);
        reimbursement.setEmployeeId(currentUser.getId());
        reimbursementMapper.insert(reimbursement);

        //新增图片到图片表
        List<String> pictures = reimbursement.getPictures();
        if (reimbursement.getPictures() != null) {
            for (String picture : pictures) {
                VoucherPicture voucherPicture = new VoucherPicture();
                voucherPicture.setPicture(picture);
                voucherPicture.setReimbursementId(reimbursement.getId());
                voucherPictureMapper.insert(voucherPicture);
            }
        }
    }


    //修改
    public void updateById(Reimbursement reimbursement) {
        reimbursementMapper.updateById(reimbursement);
        if (reimbursement.getPictures() != null) {
            voucherPictureMapper.deleteByReimbursementId(reimbursement.getId());
            for(String picture : reimbursement.getPictures()){
                VoucherPicture voucherPicture = new VoucherPicture();
                voucherPicture.setPicture(picture);
                voucherPicture.setReimbursementId(reimbursement.getId());
                System.out.println(reimbursement.getId());
                voucherPictureMapper.insert(voucherPicture);
                System.out.println(voucherPicture.getId());
            }
        }
    }

    public PageInfo<Reimbursement> selectPageApply(Reimbursement reimbursement, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Account currentUser = TokenUtils.getCurrentUser();
        List<Reimbursement> list = new ArrayList<>();
        //用户
        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            Employee employee = employeeService.selectById(currentUser.getId());
            if(employee.getLevel() == 1){
                reimbursement.setProcess(ReimbursementEnum.PROCESS_EMPLOYEE_APPLYING.status);
                list = reimbursementMapper.selectApply(reimbursement);
            }
            else if(employee.getLevel() == 2){
                list = reimbursementMapper.selectApply(reimbursement);
                //为了安全地从集合中移除元素，你可以使用迭代器（Iterator）来遍历集合，并在迭代器调用remove
                Iterator<Reimbursement> iterator = list.iterator();
                while (iterator.hasNext()) {
                    Reimbursement l = iterator.next();
                    if (ReimbursementEnum.APPLY_DONE.status.equals(l.getProcess())) {
                        iterator.remove();
                    }
                }
            }
        }else{
            list = reimbursementMapper.selectApply(reimbursement);
        }

        return PageInfo.of(list);
    }

    public Reimbursement selectById(int i) {
        Reimbursement reimbursement = reimbursementMapper.selectById(i);
        return reimbursement;
    }

    public void submitted(Reimbursement reimbursement) {

        List<VoucherPicture> voucherPictures = voucherPictureMapper.selectByReimbursementId(reimbursement.getId());
        if(voucherPictures.isEmpty()){
            throw new CustomException(ResultCodeEnum.LACK_PICTURE);
        }

        //创建审批数据,返回主键
        reimbursementApplyService.addByReimbursementId(reimbursement.getId());
        ReimbursementApply reimbursementApply = reimbursementApplyService.selectByReimbursementId(reimbursement.getId());


        //关联表，更新数据，
        reimbursement.setSubmitted(1);
        reimbursement.setApplyId(reimbursementApply.getId());
        reimbursementMapper.updateById(reimbursement);
    }

    public void updateByAdmin(Reimbursement reimbursement) {

        //获取审批表
        ReimbursementApply reimbursementApply = reimbursementApplyService.selectByReimbursementId(reimbursement.getId());

        if(ReimbursementEnum.PROCESS_EMPLOYEE_APPLYING.status.equals(reimbursement.getProcess())){
            if(ReimbursementEnum.APPLY_WAIT.status.equals(reimbursement.getStatus())){
                reimbursementApply.setStatus(ReimbursementEnum.STATUS_EMPLOYEE_APPLYING.status);
            }

            if(ReimbursementEnum.APPLY_OK.status.equals(reimbursement.getStatus())){
                reimbursementApply.setStatus(ReimbursementEnum.STATUS_HEAD_APPLYING.status);
                reimbursementApply.setProcess(ReimbursementEnum.PROCESS_FINANCIAL_APPLYING.status);
            }

            if(ReimbursementEnum.APPLY_NO.status.equals(reimbursement.getStatus())){
                reimbursementApply.setStatus(ReimbursementEnum.STATUS_EMPLOYEE_APPLY_NO.status);
                reimbursementApply.setProcess(ReimbursementEnum.APPLY_DONE.status);
                reimbursement.setSubmitted(2);
            }

        } else if (ReimbursementEnum.PROCESS_FINANCIAL_APPLYING.status.equals(reimbursement.getProcess())) {
            if(ReimbursementEnum.APPLY_WAIT.status.equals(reimbursement.getStatus())){
                reimbursementApply.setStatus(ReimbursementEnum.STATUS_HEAD_APPLYING.status);
            }

            if(ReimbursementEnum.APPLY_OK.status.equals(reimbursement.getStatus())){
                reimbursementApply.setStatus(ReimbursementEnum.STATUS_HEAD_APPLY_OK.status);
                reimbursementApply.setProcess(ReimbursementEnum.APPLY_DONE.status);
                reimbursement.setSubmitted(2);
            }

            if(ReimbursementEnum.APPLY_NO.status.equals(reimbursement.getStatus())){
                reimbursementApply.setStatus(ReimbursementEnum.STATUS_HEAD_APPLY_NO.status);
                reimbursementApply.setProcess(ReimbursementEnum.APPLY_DONE.status);
                reimbursement.setSubmitted(2);
            }

        } else if (ReimbursementEnum.APPLY_DONE.status.equals(reimbursement.getProcess())) {
            if(ReimbursementEnum.APPLY_OK.status.equals(reimbursement.getStatus())){
                reimbursementApply.setStatus(ReimbursementEnum.STATUS_ADMIN_ALLOW.status);
                reimbursementApply.setProcess(ReimbursementEnum.APPLY_DONE.status);
                reimbursement.setSubmitted(2);
            }
            if(ReimbursementEnum.APPLY_NO.status.equals(reimbursement.getStatus())){
                reimbursementApply.setStatus(ReimbursementEnum.STATUS_ADMIN_STOP.status);
                reimbursementApply.setProcess(ReimbursementEnum.APPLY_DONE.status);
                reimbursement.setSubmitted(2);
            }
        }

        reimbursementApplyService.updateById(reimbursementApply);
        reimbursementMapper.updateById(reimbursement);
    }
}
