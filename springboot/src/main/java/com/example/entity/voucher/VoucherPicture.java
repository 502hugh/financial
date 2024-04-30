/**
 * 闲人音绕梁，佳人舞偏堂 *
 * *********************
 *
 * @author hugh502
 * @date 2024/3/27 15:20
 * @version 1.0
 */

package com.example.entity.voucher;

public class VoucherPicture {
    private Integer id;

    private String picture;

    private Integer reimbursementId;


    private String username;

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getReimbursementId() {
        return reimbursementId;
    }

    public void setReimbursementId(Integer reimbursementId) {
        this.reimbursementId = reimbursementId;
    }

    @Override
    public String toString() {
        return "VoucherPicture{" +
                "id=" + id +
                ", picture='" + picture + '\'' +
                ", reimbursementId=" + reimbursementId +
                ", username='" + username + '\'' +
                '}';
    }
}
