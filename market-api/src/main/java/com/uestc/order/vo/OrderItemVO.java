package com.uestc.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemVO {

    private String goodsId;

    private String orderId;

    private String title;

    private BigDecimal price;


    private Integer num;


    private BigDecimal totalFee;


    private String picPath;


    private String sellerId;


    private String statusTcc;


    private String message;


}
