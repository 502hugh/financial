/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/16 23:35
 * @version 1.0
 */

package com.example.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
@Data
public class Resources implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    @ExcelIgnore
    private Integer id;
    @ExcelProperty("资产名称")
    @ColumnWidth(20)
    private String name;
    @ExcelIgnore
    private String img;

    @ExcelProperty("资产名称")
    @ColumnWidth(20)
    private Double price;

    @ExcelProperty("资产数量")
    @ColumnWidth(20)
    private Integer num;
    @ExcelProperty("资产类型")
    @ColumnWidth(20)
    private String type;

    @Override
    public String toString() {
        return "Resources{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", img='" + img + '\'' +
                ", price=" + price +
                ", num=" + num +
                ", type='" + type + '\'' +
                '}';
    }
}