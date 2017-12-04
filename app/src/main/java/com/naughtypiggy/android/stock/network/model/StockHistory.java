package com.naughtypiggy.android.stock.network.model;

import java.util.Date;

public class StockHistory {
    private Integer hId;

    private String symbol;

    private Date date;

    private Float open;

    private Float high;

    private Float low;

    private Float clos;

    private Float adjClose;

    private Integer volume;

    public Integer gethId() {
        return hId;
    }

    public void sethId(Integer hId) {
        this.hId = hId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Float getOpen() {
        return open;
    }

    public void setOpen(Float open) {
        this.open = open;
    }

    public Float getHigh() {
        return high;
    }

    public void setHigh(Float high) {
        this.high = high;
    }

    public Float getLow() {
        return low;
    }

    public void setLow(Float low) {
        this.low = low;
    }

    public Float getClos() {
        return clos;
    }

    public void setClos(Float clos) {
        this.clos = clos;
    }

    public Float getAdjClose() {
        return adjClose;
    }

    public void setAdjClose(Float adjClose) {
        this.adjClose = adjClose;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }
}