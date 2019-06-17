package com.uestc.service;

import com.uestc.domain.TbOrder;
import org.mengyun.tcctransaction.api.Compensable;

public interface TbOrderService {
    @Compensable
    public void insert(TbOrder tbOrder);
}
