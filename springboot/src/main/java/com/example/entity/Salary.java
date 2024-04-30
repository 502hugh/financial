/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 22:48
 * @version 1.0
 */

package com.example.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.format.NumberFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;


@Data
public class Salary implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    @ExcelIgnore
    private Integer id;
    @ExcelIgnore
    private Integer employeeId;
    @ExcelIgnore
    private Integer departmentId;
    @ExcelIgnore
    private String year;


    @ExcelProperty("员工")
    @ColumnWidth(20)
    private String employeeName;
    @ExcelProperty("部门")
    @ColumnWidth(20)
    private String departmentName;
    @ExcelProperty("时间")
    @DateTimeFormat("yyyy-MM-dd")
    @ColumnWidth(20)
    private String time;
    @ExcelProperty("薪资")
    @NumberFormat("#.##")
    @ColumnWidth(20)
    private Double price;
    @ExcelProperty("备注")
    @ColumnWidth(20)
    private String note;


    @Override
    public String toString() {
        return "Salary{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", departmentId=" + departmentId +
                ", year='" + year + '\'' +
                ", time='" + time + '\'' +
                ", price=" + price +
                ", note='" + note + '\'' +
                ", employeeName='" + employeeName + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}
