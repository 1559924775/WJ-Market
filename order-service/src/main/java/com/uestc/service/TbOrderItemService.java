package com.uestc.service;

import com.uestc.domain.TbOrderItem;
import org.mengyun.tcctransaction.api.Compensable;

public interface TbOrderItemService {
    @Compensable
    public void insert(TbOrderItem tbOrderItem);
}
