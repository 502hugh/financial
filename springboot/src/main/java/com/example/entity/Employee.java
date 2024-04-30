/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/14 22:49
 * @version 1.0
 */

package com.example.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import javax.annotation.sql.DataSourceDefinition;
import java.io.Serializable;

@Data
public class Employee extends Account implements Serializable {
//            `id` int(10) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
//            `username` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '用户名',
//            `password` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '密码',
//            `name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '姓名',
//            `avatar` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像',
//            `role` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '角色',
//            `phone` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '电话',
//            `email` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
//            `level` int(5) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '身份',
    @ExcelIgnore
    private Integer id;
    @ExcelProperty("账号")
    @ColumnWidth(20)
    private String  username;
    @ExcelIgnore
    private String  password;
    @ExcelProperty("姓名")
    @ColumnWidth(20)
    private String  name;
    @ExcelProperty("电话")
    @ColumnWidth(20)
    private String  phone;
    @ExcelProperty("邮箱")
    @ColumnWidth(20)
    private String  email;
    @ExcelIgnore
    private String  avatar;
    @ExcelIgnore
    private String  role;
    @ExcelProperty("级别")
    @ColumnWidth(20)
    private Integer level; // 1是普通员工 2是部门领导
    @ExcelIgnore
    private Integer departmentId;
    @ExcelProperty("部门")
    @ColumnWidth(20)
    private String departmentName;


    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", role='" + role + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", level=" + level +
                ", departmentId=" + departmentId +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }

}
