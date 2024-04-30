package com.example.common.enums;


public enum ResourcesEnum {




    CASH_FLOW("现金流"),

    FIXED_ASSETS("固定资产"),
    REAL_ESTATE("不动产"),


    ;
    public String status;

    ResourcesEnum (String status) {
        this.status = status;
    }
}
