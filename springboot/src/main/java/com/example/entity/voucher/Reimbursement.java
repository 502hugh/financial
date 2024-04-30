/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/27 13:53
 * @version 1.0
 */

package com.example.entity.voucher;

import java.sql.Time;
import java.util.List;

public class Reimbursement {


    /**
     * 报账表标识
     */
    private Integer id;
    /**
     * 报账类型
     */
    private String type;

    /**
     * 报账人
     */
    private String name;
    /**
     * 报账时间
     */
    private String time;
    /**
     * 报账备注
     */
    private String note;
    /**
     * 报账附件
     * 这里另外写一个表存储图片
     */

    /**
     * 与审批表的关联
     */
    private Integer applyId;

    /**
     * 金额
     */
    private Double amount;


    private List<String> pictures;


    private Integer submitted;

    private String status;

    private String process;
    private Integer employeeId;

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public Integer getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Integer submitted) {
        this.submitted = submitted;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getApplyId() {
        return applyId;
    }

    public void setApplyId(Integer applyId) {
        this.applyId = applyId;
    }

    @Override
    public String toString() {
        return "Reimbursement{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", note='" + note + '\'' +
                ", applyId=" + applyId +
                ", amount=" + amount +
                ", pictures=" + pictures +
                ", Submitted=" + submitted +
                '}';
    }
}
