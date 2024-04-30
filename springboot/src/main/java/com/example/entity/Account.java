package com.example.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.Data;

/**
 * 角色用户父类
 */
@Data
public class Account {
    @ExcelIgnore
    private Integer id;
    /** 用户名 */
    @ExcelIgnore
    private String username;
    /** 名称 */
    @ExcelIgnore
    private String name;
    /** 密码 */
    @ExcelIgnore
    private String password;
    /** 角色标识 */
    @ExcelIgnore
    private String role;
    /** 新密码 */
    @ExcelIgnore
    private String newPassword;
    /** 头像 */
    @ExcelIgnore
    private String avatar;
    @ExcelIgnore
    private String token;
    @ExcelIgnore
    private String powerName;



    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", newPassword='" + newPassword + '\'' +
                ", avatar='" + avatar + '\'' +
                ", token='" + token + '\'' +
                ", powerName='" + powerName + '\'' +
                '}';
    }
}
