package com.uestc.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderVO {

    private BigDecimal payment;

    private String paymentType;

    private String postFee;

    private String status;

    private Date createTime;

    private Date updateTime;

    private Date paymentTime;

    private Date consignTime;

    private Date endTime;


    private Date closeTime;

    private String shippingName;

    private String shippingCode;


    private String userId;


    private String buyerMessage;

    private String buyerNick;


    private String buyerRate;

    private String receiverAreaName;


    private String receiverMobile;

    private String receiverZipCode;

    private String receiver;

    private Date expire;

    private String invoiceType;


    private String sourceType;

    private String sellerId;
}
