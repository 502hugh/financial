/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/4/19 21:36
 * @version 1.0
 */

package com.example.entity;

public class Visible {
        private int id;
        private boolean isHomeNoticeVisible;
        private boolean isHomeBarInVisible;
        private boolean isHomeLineInOutVisible;
        private boolean isHomeBarSalaryInVisible;
        private boolean isHomePieInVisible;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isHomeNoticeVisible() {
        return isHomeNoticeVisible;
    }

    public void setHomeNoticeVisible(boolean homeNoticeVisible) {
        isHomeNoticeVisible = homeNoticeVisible;
    }

    public boolean isHomeBarInVisible() {
        return isHomeBarInVisible;
    }

    public void setHomeBarInVisible(boolean homeBarInVisible) {
        isHomeBarInVisible = homeBarInVisible;
    }

    public boolean isHomeLineInOutVisible() {
        return isHomeLineInOutVisible;
    }

    public void setHomeLineInOutVisible(boolean homeLineInOutVisible) {
        isHomeLineInOutVisible = homeLineInOutVisible;
    }

    public boolean isHomeBarSalaryInVisible() {
        return isHomeBarSalaryInVisible;
    }

    public void setHomeBarSalaryInVisible(boolean homeBarSalaryInVisible) {
        isHomeBarSalaryInVisible = homeBarSalaryInVisible;
    }

    public boolean isHomePieInVisible() {
        return isHomePieInVisible;
    }

    public void setHomePieInVisible(boolean homePieInVisible) {
        isHomePieInVisible = homePieInVisible;
    }

    @Override
    public String toString() {
        return "Visible{" +
                "id=" + id +
                ", isHomeNoticeVisible=" + isHomeNoticeVisible +
                ", isHomeBarInVisible=" + isHomeBarInVisible +
                ", isHomeLineInOutVisible=" + isHomeLineInOutVisible +
                ", isHomeBarSalaryInVisible=" + isHomeBarSalaryInVisible +
                ", isHomePieInVisible=" + isHomePieInVisible +
                '}';
    }
}
