/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 23:08
 * @version 1.0
 */

package com.example.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

@Data
public class Financialout  implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    @ExcelIgnore
    private Integer id;
    @ExcelIgnore
    private Integer departmentId;
    @ExcelProperty("支出时间")
    @ColumnWidth(20)
    private String time;
    @ExcelProperty("支出金额")
    @ColumnWidth(20)
    private Double price;
    @ExcelProperty("支出说明")
    @ColumnWidth(20)
    private String name;
    @ExcelProperty("支出部门")
    @ColumnWidth(20)
    private String departmentName;

    @Override
    public String toString() {
        return "Financialout{" +
                "id=" + id +
                ", departmentId=" + departmentId +
                ", time='" + time + '\'' +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", departmentName='" + departmentName + '\'' +
                '}';
    }
}