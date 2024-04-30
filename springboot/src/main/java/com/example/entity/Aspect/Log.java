/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/16 12:58
 * @version 1.0
 */

package com.example.entity.Aspect;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class Log {
    @ExcelIgnore
    private Integer id;
    @ExcelProperty("操作类型")
    @ColumnWidth(20)
    private String operationType;
    @ExcelProperty("操作时间")
    @ColumnWidth(20)
    private String operationTime;
    @ExcelProperty("操作用户")
    @ColumnWidth(20)
    private String operationUser;
    @ExcelProperty("日志详情")
    @ColumnWidth(20)
    private String operationContent;

    @Override
    public String toString() {
        return "Log{" +
                "id=" + id +
                ", operationType='" + operationType + '\'' +
                ", operationTime='" + operationTime + '\'' +
                ", operationUser='" + operationUser + '\'' +
                ", operationContent='" + operationContent + '\'' +
                '}';
    }
}
