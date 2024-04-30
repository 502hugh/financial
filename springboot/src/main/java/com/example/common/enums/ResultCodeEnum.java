package com.example.common.enums;

public enum ResultCodeEnum {
    SUCCESS("200", "成功"),

    PARAM_ERROR("400", "参数异常"),
    TOKEN_INVALID_ERROR("401", "无效的token"),
    TOKEN_CHECK_ERROR("401", "token验证失败，请重新登录"),
    PARAM_LOST_ERROR("4001", "参数缺失"),

    NEI_REGISTER_ERROR("4002","注册，内部错误"),

    SYSTEM_ERROR("500", "系统异常"),
    USER_EXIST_ERROR("5001", "用户名已存在"),
    USER_NOT_LOGIN("5002", "用户未登录"),
    USER_ACCOUNT_ERROR("5003", "账号或密码错误"),
    USER_NOT_EXIST_ERROR("5004", "用户不存在"),
    PARAM_PASSWORD_ERROR("5005", "原密码输入错误"),


    //资产相关
    RESOURCES_NUM_ERROR("5011","申请数量超过库存"),

    //部门相关
    EMPLOYEE_D_L_ERROR("5021","部门和身份不匹配"),

    EMPLOYEE_HAVE_LEADER_ERROR("5022","部门已经有部长"),
    EMPLOYEE_HAVE_DEPARTMENT_ERORR("5023","该员工是其他部门领导" ),
    DEPARTMENT_DELET_ERORR("5024","财务部门不可删除" ),
    DEPARTMENT_UPDATE_ERORR("5025","财务部名称不可修改" ),
    EMPLOYEE_HAVE_POWERNAME_ERORR("5026","该权限已有部门归属，请更换权限" ),

    EMPLOYEE_NO_DEPARTMENT("5027","该员工暂无部门分配"),

    //审批相关
    APPLY_ADMIN_ROLE_ERORR("5031","管理员不参与审批"),
    APPLY_NO_POWER_ERROR("5032","暂无该记录审批权限"),
    APPLY_STATUS_NULL_ERROR("5033","缺失审批状态参数"),
    EMPLOYEE_APPLYING_NO_DELETE("5034","不可删除正在审批的记录"),


    //资产相关
    CASH_FLOW_NO_APPLY("5041","现金流不可被申请"),
    CASH_FLOW_NO_ENOUGH("5042","现金流不足" ),
    //薪资相关
    SALARY_MANAGER_HAVE_ERROR("5051","薪资管理表已有该员工信息" ),
    SALARY_MANAGER_NO_RECORD("5052","薪资管理表暂无薪资记录，请添加"),
    SALARY_MANAGER_GETPRICE_RECORD("5053","获取不到总的薪资"),



    //日志相关
    LOG_UPDATE_ERROR("5061","修改操作-日志添加异常"),
    LOG_DELETE_ERROR("5062","删除操作-日志添加异常"),
    LOG_SELECT_ERROR("5063","查询操作-日志添加异常"),
    LOG_ADD_ERROR("5064","新增操作-日志添加异常"),


    //报账相关
    LACK_PICTURE("5071","缺少附件图片"),
    FILE_UP_NULL("5090","上传文件为空" ),
    DATA_UP_FAILE("5091","全部数据上传失败"),;



    public String code;
    public String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
