package com.uestc.stock.vo;

import lombok.Data;

import java.util.Date;

@Data
public class StockVO {

    private String goodsId;


    private String sellerId;


    private String goodsName;


    private Integer stock;


    private Integer version;


    private Date creatTime;


    private Date updateTime;
}
