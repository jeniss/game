package com.game.model;

import java.util.Date;

/**
 * Created by jeniss on 17/6/11.
 */
public class TradeFlow {
    private Integer id;
    private Game game;
    private ServerArea serverArea;
    private GameCategory gameCategory;
    private String name;
    private Double price;
    private Integer stock;
    private Double totalPrice;
    private Double unitPrice;
    private String unitPriceDesc;
    private String tradeStatus;
    private Date entryDatetime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public ServerArea getServerArea() {
        return serverArea;
    }

    public void setServerArea(ServerArea serverArea) {
        this.serverArea = serverArea;
    }

    public GameCategory getGameCategory() {
        return gameCategory;
    }

    public void setGameCategory(GameCategory gameCategory) {
        this.gameCategory = gameCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getUnitPriceDesc() {
        return unitPriceDesc;
    }

    public void setUnitPriceDesc(String unitPriceDesc) {
        this.unitPriceDesc = unitPriceDesc;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Date getEntryDatetime() {
        return entryDatetime;
    }

    public void setEntryDatetime(Date entryDatetime) {
        this.entryDatetime = entryDatetime;
    }
}
