package com.naughtypiggy.android.stock.network.model;

public class StockSymbol {
    private Integer stockId;

    private String symbol;

    private String name;

    private String sector;

    private String industry;

    public Integer getStockId() {
        return stockId;
    }

    public void setStockId(Integer stockId) {
        this.stockId = stockId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol == null ? null : symbol.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector == null ? null : sector.trim();
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry == null ? null : industry.trim();
    }
}