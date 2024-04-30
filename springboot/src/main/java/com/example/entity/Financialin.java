/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/21 14:02
 * @version 1.0
 */

package com.example.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
@Data
public class Financialin implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    @ExcelIgnore
    private Integer id;
    @ExcelProperty("收入类型")
    @ColumnWidth(20)
    private String type;
    @ExcelProperty("收入时间")
    @ColumnWidth(20)
    private String time;
    @ExcelProperty("收入金额")
    @ColumnWidth(20)
    private Double price;
    @ExcelProperty("收入时间")
    @ColumnWidth(20)
    private String name;

    @ExcelIgnore
    private Integer resourcesNum;

    @ExcelIgnore
    private String resourcesName;


    @Override
    public String toString() {
        return "Financialin{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", time='" + time + '\'' +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", resourcesNum=" + resourcesNum +
                ", resourcesName='" + resourcesName + '\'' +
                '}';
    }
}
