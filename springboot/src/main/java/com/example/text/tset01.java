/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/1 13:28
 * @version 1.0
 */

package com.example.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class tset01 {

        public static void main(String[] args) {
            List<String> list = new ArrayList<>();
            list.add("Apple");
            list.add("Banana");
            list.add("Orange");

            Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                String element = iterator.next();
                if ("Banana".equals(element)) {
                    System.out.println(iterator);
                    iterator.remove(); // 删除当前元素
                }
            }

            System.out.println(list); // 输出：[Apple, Orange]
        }
    }

