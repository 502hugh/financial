/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/5 1:21
 * @version 1.0
 */

package com.example.utils;
import java.util.Scanner;
public class ScannerUtils {


    public static double scannerCount (Double total){

            double taxIncome = total - 3500;
            double tax = 0.0;
            if(total < 3500) {
                tax = 0.0;
            }else if(taxIncome <= 4500) {
                tax = taxIncome * 3/100;
            }else if(taxIncome <= 4500) {
                tax = taxIncome * 10/100 - 105;
            }else if(taxIncome <= 9000) {
                tax = taxIncome * 20/100 - 555;
            }else if(taxIncome <= 35000) {
                tax = taxIncome * 25/100 - 1005;
            }else if(taxIncome <= 55000) {
                tax = taxIncome * 30/100 - 2755;
            }else if(taxIncome <= 80000) {
                tax = taxIncome * 35/100 - 5505;
            }else {
                tax = taxIncome * 45/100 - 13505;
            }
            return tax;
        }


}
