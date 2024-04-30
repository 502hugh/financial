package com.example.common.enums;

public enum PowerEnum {

    DEPARTMENT_FINANCIAL("财务"),
    DEPARTMENT_HUMAN("人事"),
    DEPARTMENT_OPERATE("运营"),
    DEPARTMENT_TECHNOLOGY("技术"),
    DEPARTMENT_PURCHASE("采购"),

    ;

    public String status;

    PowerEnum(String status) {
        this.status = status;
    }
}
