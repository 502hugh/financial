/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/31 23:24
 * @version 1.0
 */

package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.common.enums.*;
import com.example.entity.*;
import com.example.entity.voucher.Reimbursement;
import com.example.entity.voucher.ReimbursementApply;
import com.example.exception.CustomException;
import com.example.mapper.ReimbursementApplyMapper;
import com.example.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ReimbursementApplyService {


    @Resource
    private ReimbursementApplyMapper reimbursementApplyMapper;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private ReimbursementService reimbursementService;

    @Resource
    private FinancialoutService financialoutService;

    @Resource
    private ResourcesService resourcesService;


    public int addByReimbursementId(Integer id) {
        ReimbursementApply reimbursementApply = new ReimbursementApply();
        reimbursementApply.setReimbursementId(id);
        reimbursementApply.setProcess(ReimbursementEnum.PROCESS_EMPLOYEE_APPLYING.status);
        reimbursementApply.setStatus(ReimbursementEnum.STATUS_EMPLOYEE_APPLYING.status);
        return reimbursementApplyMapper.insert(reimbursementApply);
    }

    public void updateById(ReimbursementApply reimbursementApply) {
        reimbursementApplyMapper.updateById(reimbursementApply);

    }

    public ReimbursementApply selectById(Integer id) {
        ReimbursementApply reimbursementApply = reimbursementApplyMapper.selectById(id);
        return reimbursementApply;
    }

    public ReimbursementApply selectByReimbursementId(Integer reimbursementId) {
        ReimbursementApply reimbursementApply = reimbursementApplyMapper.selectByReimbursementId(reimbursementId);
        return reimbursementApply;
    }

    public void updateSubmit(ReimbursementApply reimbursementApply) {
        Account currentUser = TokenUtils.getCurrentUser();
        if(reimbursementApply.getStatus() == null){
            throw new CustomException(ResultCodeEnum.APPLY_STATUS_NULL_ERROR);
        }

        if(RoleEnum.USER.name().equals(currentUser.getRole())){
            //判断是否财务部门的员工
            Employee employee = employeeService.selectById(currentUser.getId());

            if(employee.getDepartmentId() == null){
                //员工没有权限
                throw new CustomException(ResultCodeEnum.APPLY_NO_POWER_ERROR);
            }
            Department department = departmentService.selectById(employee.getDepartmentId());
            Reimbursement reimbursement = reimbursementService.selectById(reimbursementApply.getReimbursementId());

            if(PowerEnum.DEPARTMENT_FINANCIAL.status.equals(department.getPowerName())){
                ReimbursementApply  reimbursementApply1 = reimbursementApplyMapper.selectByReimbursementId(reimbursementApply.getReimbursementId());
                reimbursementApply.setId(reimbursementApply1.getId());

                if((employee.getLevel() == 1 &&
                        ReimbursementEnum.PROCESS_EMPLOYEE_APPLYING.status.equals(reimbursementApply1.getProcess())) ||
                        (employee.getLevel() == 2 &&
                                ReimbursementEnum.PROCESS_EMPLOYEE_APPLYING.status.equals(reimbursementApply1.getProcess()))
                ){
                    //把自己的id写入
                    reimbursementApply.setEmployeeId(employee.getId());
                    reimbursementApply.setTime(DateUtil.now());

                    if(ReimbursementEnum.APPLY_OK.status.equals(reimbursementApply.getStatus())){
                        reimbursementApply.setProcess(ReimbursementEnum.PROCESS_FINANCIAL_APPLYING.status);
                        reimbursementApply.setStatus(ReimbursementEnum.STATUS_EMPLOYEE_APPLY_OK.status);
                    }else{
                        //结束
                        reimbursementApply.setProcess(ReimbursementEnum.APPLY_DONE.status);
                        reimbursementApply.setStatus(ReimbursementEnum.APPLY_NO.status);


                    }
                    reimbursementApplyMapper.updateById(reimbursementApply);
                    reimbursement.setSubmitted(2);
                    reimbursementService.updateById(reimbursement);

                }else if(employee.getLevel() == 2 &&
                        ReimbursementEnum.PROCESS_FINANCIAL_APPLYING.status.equals(reimbursementApply1.getProcess())){
                    if(ReimbursementEnum.APPLY_OK.status.equals(reimbursementApply.getStatus())){
                        reimbursementApply.setStatus(ReimbursementEnum.STATUS_HEAD_APPLY_OK.status);
                        //财务部审批结束
                        //更改submitted状态
                        reimbursement.setSubmitted(2);
                        //增加财务支出
                        Employee employee1 = employeeService.selectByUsername(reimbursement.getName());
                        Financialout financialout = new Financialout();
                        financialout.setTime(reimbursement.getTime());
                        financialout.setDepartmentId(employee1.getDepartmentId());
                        financialout.setPrice(reimbursement.getAmount());
                        financialout.setName(reimbursement.getType());

                        //更新当前现金流
                        Resources resources = resourcesService.selectByType(ResourcesEnum.CASH_FLOW.status);
                        resources.setPrice(resources.getPrice() - reimbursement.getAmount());

                        //更新数据
                        resourcesService.updateById(resources);
                        financialoutService.add(financialout);
                        reimbursementService.updateById(reimbursement);

                    }else{
                        //结束
                        reimbursementApply.setStatus(ReimbursementEnum.STATUS_EMPLOYEE_APPLY_NO.status);
                    }
                    reimbursementApply.setProcess(ReimbursementEnum.APPLY_DONE.status);
                    reimbursementApplyMapper.updateById(reimbursementApply);

                }else{
                    //部长审批到员工的工作了
                    throw new CustomException(ResultCodeEnum.APPLY_NO_POWER_ERROR);
                }

            }else{
                //非财务员工
                throw new CustomException(ResultCodeEnum.APPLY_NO_POWER_ERROR);
            }

        }
        else{
            //管理员
            throw new CustomException(ResultCodeEnum.APPLY_ADMIN_ROLE_ERORR);
        }

    }
}
