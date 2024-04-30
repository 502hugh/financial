/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 23:55
 * @version 1.0
 */

package com.example.common.enums;




public enum ApplyEnum {


    // 两个进度
    PROCESS_HEADER_APPLYING("部长审批中"),
    PROCESS_ADMIN_APPLYING("管理员审批中"),

    PROCESS_FINANCIAL_APPLYING("财务部审批中"),

    // 每个进度三种审批状态
    STATUS_HEADER_APPLYING("待部长审批"),
    STATUS_HEADER_APPLY_OK("部长审批通过"),
    STATUS_HEADER_APPLY_NO("部长审批不通过"),

    STATUS_ADMIN_APPLYING("待管理员审批"),
    STATUS_ADMIN_APPLY_OK("管理员审批通过"),
    STATUS_ADMIN_APPLY_NO("管理员审批不通过"),

    STATUS_FINANCIAL_APPLYING("待财务部审批"),
    STATUS_FINANCIAL_APPLY_OK("财务部审批通过"),
    STATUS_FINANCIAL_APPLY_NO("财务部审批不通过"),

    STATUS_ADMIN_STOP("审批被强行终止"),

    STATUS_ADMIN_ALLOW("审批被强行通过"),
    APPLY_OK("审批通过"),
    APPLY_NO("审批不通过"),
    APPLY_DONE("审批结束"),
    APPLY_WAIT("待审批"), ;


    public String status;

    ApplyEnum(String status) {
        this.status = status;
    }
}
