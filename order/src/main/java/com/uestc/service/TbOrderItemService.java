package com.uestc.service;

import com.uestc.domain.TbOrderItem;
import org.mengyun.tcctransaction.api.Compensable;

import java.util.List;

public interface TbOrderItemService {
    @Compensable
    public void insert(TbOrderItem tbOrderItem);
    @Compensable
    public void insertAll(List<TbOrderItem> tbOrderItems);
}
