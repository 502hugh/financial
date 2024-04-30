/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/3 22:54
 * @version 1.0
 */

package com.example.entity;

import java.io.Serializable;
import java.util.Date;

public class SalaryManager implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id (主键)
     * 员工id (外键，关联员工表的id)
     * 基本工资
     * 岗位工资
     * 绩效奖金
     * 其他津贴
     * 社保缴纳情况
     * 公积金缴纳情况
     * 个人所得税
     * 薪资发放日期
     */
    private Integer id;
    private Integer employeeId;
    private Double basicSalary;
    private Double positionSalary;
    private Double performanceBonus;
    private Double otherAllowance;
    private String socialInsurance;
    private String housingFund;
    private Double personalIncomeTax;
    private String salaryDate;

    private String employeeName;

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

    public Double getBasicSalary() {
        return basicSalary;
    }

    public void setBasicSalary(Double basicSalary) {
        this.basicSalary = basicSalary;
    }

    public Double getPositionSalary() {
        return positionSalary;
    }

    public void setPositionSalary(Double positionSalary) {
        this.positionSalary = positionSalary;
    }

    public Double getPerformanceBonus() {
        return performanceBonus;
    }

    public void setPerformanceBonus(Double performanceBonus) {
        this.performanceBonus = performanceBonus;
    }

    public Double getOtherAllowance() {
        return otherAllowance;
    }

    public void setOtherAllowance(Double otherAllowance) {
        this.otherAllowance = otherAllowance;
    }

    public String getSocialInsurance() {
        return socialInsurance;
    }

    public void setSocialInsurance(String socialInsurance) {
        this.socialInsurance = socialInsurance;
    }

    public String getHousingFund() {
        return housingFund;
    }

    public void setHousingFund(String housingFund) {
        this.housingFund = housingFund;
    }

    public Double getPersonalIncomeTax() {
        return personalIncomeTax;
    }

    public void setPersonalIncomeTax(Double personalIncomeTax) {
        this.personalIncomeTax = personalIncomeTax;
    }

    public String getSalaryDate() {
        return salaryDate;
    }

    public void setSalaryDate(String salaryDate) {
        this.salaryDate = salaryDate;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    @Override
    public String toString() {
        return "SalaryManager{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", basicSalary=" + basicSalary +
                ", positionSalary=" + positionSalary +
                ", performanceBonus=" + performanceBonus +
                ", otherAllowance=" + otherAllowance +
                ", socialInsurance='" + socialInsurance + '\'' +
                ", housingFund='" + housingFund + '\'' +
                ", personalIncomeTax=" + personalIncomeTax +
                ", salaryDate=" + salaryDate +
                ", employeeName='" + employeeName + '\'' +
                '}';
    }
}
