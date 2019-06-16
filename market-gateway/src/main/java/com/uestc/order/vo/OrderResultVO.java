package com.uestc.order.vo;

import lombok.Data;

import java.util.List;
@Data
public class OrderResultVO {
    List<OrderItemVO> orderItemVOs;
    OrderVO orderVo;
}
