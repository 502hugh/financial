package com.example.common.enums;

public enum ReimbursementEnum {

    // 两个进度
    PROCESS_EMPLOYEE_APPLYING("财务人员审批中"),
    PROCESS_FINANCIAL_APPLYING("财务部门审批中"),
    APPLY_DONE("审批结束"),

    // 每个进度三种审批状态
    STATUS_EMPLOYEE_APPLYING("待财务人员审批"),
    STATUS_EMPLOYEE_APPLY_OK("财务人员审批通过"),
    STATUS_EMPLOYEE_APPLY_NO("财务人员审批不通过"),

    STATUS_HEAD_APPLYING("待财务部审批"),
    STATUS_HEAD_APPLY_OK("财务部审批通过"),
    STATUS_HEAD_APPLY_NO("财务部审批不通过"),

    STATUS_ADMIN_STOP("审批被强行终止"),

    STATUS_ADMIN_ALLOW("审批被强行通过"),

    APPLY_OK("审批通过"),
    APPLY_NO("审批不通过"),
    APPLY_WAIT("待审批"),
    ;

    public String status;

    ReimbursementEnum (String status) {
        this.status = status;
    }
}
