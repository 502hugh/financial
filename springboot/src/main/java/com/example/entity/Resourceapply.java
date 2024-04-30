/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 23:49
 * @version 1.0
 */

package com.example.entity;

import java.io.Serializable;

public class Resourceapply implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private Integer id;
    private Integer employeeId;
    private Integer departmentId;
    private Integer resourcesId;
    private Integer num;
    private String time;
    private String process;
    private String status;
    private String note;

    private String employeeName;
    private String departmentName;
    private String resourcesName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getResourcesId() {
        return resourcesId;
    }

    public void setResourcesId(Integer resourcesId) {
        this.resourcesId = resourcesId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getResourcesName() {
        return resourcesName;
    }

    public void setResourcesName(String resourceName) {
        this.resourcesName = resourceName;
    }

    @Override
    public String toString() {
        return "Resourceapply{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", departmentId=" + departmentId +
                ", resourcesId=" + resourcesId +
                ", num=" + num +
                ", time='" + time + '\'' +
                ", process='" + process + '\'' +
                ", status='" + status + '\'' +
                ", note='" + note + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", resourcesName='" + resourcesName + '\'' +
                '}';
    }
}