/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/19 3:28
 * @version 1.0
 */

package com.example.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MyDateUtils {

    // 辅助方法，判断时间是否在最近一年内
    public static boolean isWithinLastYear(String time) {
        LocalDate now = LocalDate.now();
        LocalDate targetDate = LocalDate.parse(time.substring(0, 7) + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate lastYear = now.minusYears(1);
        return !targetDate.isBefore(lastYear) && !targetDate.isAfter(now);
    }

    public static List<String> generateRecentYearMonths() {
        List<String> months = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // 从当前日期开始，向前推算一年内的月份，并添加到列表中
        for (int i = 0; i < 12; i++) {
            LocalDate monthDate = currentDate.minusMonths(i);
            String monthString = monthDate.format(formatter);
            months.add(monthString);
        }
        return months;
    }
}
