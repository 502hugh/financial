/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/27 15:28
 * @version 1.0
 */

package com.example.entity.voucher;

public class ReimbursementApply {
    private Integer id;
    private Integer reimbursementId;
    private Integer employeeApply;
    private Integer employeeSolve;

    private String time;
    private String status;
    private String process;

    private String note;

    private Integer employeeId;

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getReimbursementId() {
        return reimbursementId;
    }

    public void setReimbursementId(Integer reimbursementId) {
        this.reimbursementId = reimbursementId;
    }



    public Integer getEmployeeSolve() {
        return employeeSolve;
    }

    public void setEmployeeSolve(Integer employeeSolve) {
        this.employeeSolve = employeeSolve;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


    @Override
    public String toString() {
        return "ReimbursementApply{" +
                "id=" + id +
                ", reimbursementId=" + reimbursementId +
                ", employeeApply=" + employeeApply +
                ", employeeSolve=" + employeeSolve +
                ", time='" + time + '\'' +
                ", status='" + status + '\'' +
                ", process='" + process + '\'' +
                ", note='" + note + '\'' +
                ", employeeId=" + employeeId +
                '}';
    }


}
