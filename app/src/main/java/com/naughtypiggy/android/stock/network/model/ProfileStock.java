package com.naughtypiggy.android.stock.network.model;

import java.util.Date;

public class ProfileStock {
    private Integer sid;

    private Integer userId;

    private Integer pid;

    private String sname;

    private Float price;

    private Integer share;

    private Date boughtTime;

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getSname() {
        return sname;
    }

    public void setSname(String sname) {
        this.sname = sname == null ? null : sname.trim();
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getShare() {
        return share;
    }

    public void setShare(Integer share) {
        this.share = share;
    }

    public Date getBoughtTime() {
        return boughtTime;
    }

    public void setBoughtTime(Date boughtTime) {
        this.boughtTime = boughtTime;
    }
}