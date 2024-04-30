/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/16 12:54
 * @version 1.0
 */

package com.example.Aspect;


import cn.hutool.core.date.DateUtil;
import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.*;
import com.example.entity.voucher.Reimbursement;
import com.example.entity.voucher.ReimbursementApply;
import com.example.exception.CustomException;
import com.example.service.*;
import com.example.entity.Aspect.Log;
import com.example.utils.TokenUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Aspect
@Component
public class LogAspect {

    @Resource
    private LogAspectService logAspectService;

    @Resource
    private EmployeeService employeeService;

    @Resource
    private AdminService adminService;

    @Resource
    private DepartmentService departmentService;

    @Resource
    private FinancialoutService financialoutService;

    @Resource
    private FinancialinService financialinService;

    @Resource
    private NoticeService noticeService;

    @Resource
    private SalaryManagerService salaryManagerService;

    @Resource
    private SalaryService salaryService;

    @Resource
    private ResourceapplyService resourceapplyService;
    @Resource
    private ResourcesService resourcesService;

    @Resource
    private ReimbursementService reimbursementService;

    @Resource
    private ReimbursementApplyService reimbursementApplyService;



    @Around("execution(* com.example.service.*.updateById(..))")
    public Object logUpdate(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        Object result = joinPoint.proceed();
        if(args[0] == null){
            return result;
        }
        // 获取修改前后的对象
        Object beforeEntity = null;
        Object afterEntity = null;
        // 判断参数类型并获取对应的服务类
        if (args[0] instanceof Employee) {
            beforeEntity = employeeService.selectById(((Employee) args[0]).getId());
            afterEntity = (Employee) args[0];
        } else if (args[0] instanceof Admin) {
            beforeEntity = adminService.selectById(((Admin) args[0]).getId());
            afterEntity = (Admin) args[0];
        } else if (args[0] instanceof Department) {
            beforeEntity = departmentService.selectById(((Department) args[0]).getId());
            afterEntity = (Department) args[0];
        }else if (args[0] instanceof Financialin) {
            beforeEntity = financialinService.selectById(((Financialin) args[0]).getId());
            afterEntity = (Financialin) args[0];
        }else if (args[0] instanceof Financialout) {
            beforeEntity = financialoutService.selectById(((Financialout) args[0]).getId());
            afterEntity = (Financialout) args[0];
        }else if (args[0] instanceof SalaryManager) {
            beforeEntity = salaryManagerService.selectById(((SalaryManager) args[0]).getId());
            afterEntity = (SalaryManager) args[0];
        }else if (args[0] instanceof Notice) {
            beforeEntity = noticeService.selectById(((Notice) args[0]).getId());
            afterEntity = (Notice) args[0];
        }else if (args[0] instanceof Salary) {
            beforeEntity = salaryService.selectById(((Salary) args[0]).getId());
            afterEntity = (Salary) args[0];
        }else if (args[0] instanceof Resourceapply) {
            beforeEntity = resourceapplyService.selectById(((Resourceapply) args[0]).getId());
            afterEntity = (Resourceapply) args[0];
        }else if (args[0] instanceof Resources) {
            beforeEntity = resourcesService.selectById(((Resources) args[0]).getId());
            afterEntity = (Resources) args[0];
        }else if (args[0] instanceof Reimbursement) {
            beforeEntity = reimbursementService.selectById(((Reimbursement) args[0]).getId());
            afterEntity = (Reimbursement) args[0];
        }else if (args[0] instanceof ReimbursementApply) {
            beforeEntity = reimbursementApplyService.selectById(((ReimbursementApply) args[0]).getId());
            afterEntity = (ReimbursementApply) args[0];
        }
        if(beforeEntity == null || afterEntity == null){
            return result;
        }
        // 记录操作日志
        Log log = new Log();
        log.setOperationType("UPDATE");
        log.setOperationTime(DateUtil.now());
        log.setOperationUser(TokenUtils.getCurrentUser().getRole() + ":" + TokenUtils.getCurrentUser().getUsername());
        log.setOperationContent("修改前：" + beforeEntity.toString() + "\n修改后：" + afterEntity.toString());
        logAspectService.save(log);
        return result;
    }

    @Around("execution(* com.example.service.*.deleteById(..))")
    public Object logDelete(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        // 获取目标对象
        Object target = joinPoint.getTarget();
        // 声明变量保存删除前的对象
        Object beforeEntity = null;
        System.out.println("=================================DELETE");
        System.out.println(target.getClass());

        if (target instanceof EmployeeService) {
            beforeEntity = employeeService.selectById((Integer) args[0]);
        } else if (target instanceof AdminService) {
            beforeEntity = adminService.selectById((Integer) args[0]);
        } else if (target instanceof DepartmentService) {
            beforeEntity = departmentService.selectById((Integer) args[0]);
        } else if (target instanceof FinancialinService) {
            beforeEntity = financialinService.selectById((Integer) args[0]);
        } else if (target instanceof FinancialoutService) {
            beforeEntity = financialoutService.selectById((Integer) args[0]);
        } else if (target instanceof SalaryManagerService) {
            beforeEntity = salaryManagerService.selectById((Integer) args[0]);
        } else if (target instanceof NoticeService) {
            beforeEntity = noticeService.selectById((Integer) args[0]);
        } else if (target instanceof SalaryService) {
            beforeEntity = salaryService.selectById((Integer) args[0]);
        } else if (target instanceof ResourceapplyService) {
            beforeEntity = resourceapplyService.selectById((Integer) args[0]);
        } else if (target instanceof ResourcesService) {
            beforeEntity = resourcesService.selectById((Integer) args[0]);
        } else if (target instanceof ReimbursementService) {
            beforeEntity = reimbursementService.selectById((Integer) args[0]);
        } else if (target instanceof ReimbursementApplyService) {
            beforeEntity = reimbursementApplyService.selectById((Integer) args[0]);
        }


        // 执行目标方法，并获取返回结果
        Object result = joinPoint.proceed();
        if(beforeEntity == null){
            return result;
        }
        // 记录操作日志
        Log log = new Log();
        log.setOperationType("DELETE");
        log.setOperationTime(DateUtil.now());
        log.setOperationUser(TokenUtils.getCurrentUser().getRole() + ":" + TokenUtils.getCurrentUser().getUsername());
        log.setOperationContent("删除数据：" + beforeEntity.toString());
        logAspectService.save(log);

        return result;
    }

    @Around("execution(* com.example.service.*.add(..))")
    public Object logAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        Object result = joinPoint.proceed();
        if(args[0] == null){
            return result;
        }
        Object afterEntity = null;
        if (args[0] instanceof Employee) {
            afterEntity = (Employee) args[0];
        } else if (args[0] instanceof Admin) {
            afterEntity = (Admin) args[0];
        } else if (args[0] instanceof Department) {
            afterEntity = (Department) args[0];
        }else if (args[0] instanceof Financialin) {
            afterEntity = (Financialin) args[0];
        }else if (args[0] instanceof Financialout) {
            afterEntity = (Financialout) args[0];
        }else if (args[0] instanceof SalaryManager) {
            afterEntity = (SalaryManager) args[0];
        }else if (args[0] instanceof Notice) {
            afterEntity = (Notice) args[0];
        }else if (args[0] instanceof Salary) {
            afterEntity = (Salary) args[0];
        }else if (args[0] instanceof Resourceapply) {
            afterEntity = (Resourceapply) args[0];
        }else if (args[0] instanceof Resources) {
            afterEntity = (Resources) args[0];
        }else if (args[0] instanceof Reimbursement) {
            afterEntity = (Reimbursement) args[0];
        }else if (args[0] instanceof ReimbursementApply) {
            afterEntity = (ReimbursementApply) args[0];
        }

        if(afterEntity == null){
            return result;
        }
        // 记录操作日志
        Log log = new Log();
        log.setOperationType("ADD");
        log.setOperationTime(DateUtil.now());
        log.setOperationUser(TokenUtils.getCurrentUser().getRole() + ":" + TokenUtils.getCurrentUser().getUsername());
        log.setOperationContent("新增信息：" + afterEntity.toString());
        logAspectService.save(log);

        return result;
    }





}
