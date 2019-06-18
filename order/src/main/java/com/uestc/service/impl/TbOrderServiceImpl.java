package com.uestc.service.impl;

import com.uestc.dao.TbOrderMapper;
import com.uestc.domain.TbOrder;
import com.uestc.service.TbOrderService;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TbOrderServiceImpl implements TbOrderService {
    @Autowired
    TbOrderMapper tbOrderMapper;

    @Compensable(propagation = Propagation.REQUIRES_NEW,confirmMethod = "confirmInsert", cancelMethod = "cancelInsert", asyncConfirm = true)
    public void insert(TbOrder tbOrder){
        tbOrder.setStatusTcc("inserting");
        tbOrderMapper.insert(tbOrder);
    }

    public void confirmInsert(TbOrder tbOrder){
        tbOrder.setStatusTcc("inserted");
        tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
    }

    public void cancelInsert(TbOrder tbOrder){
        tbOrderMapper.deleteByPrimaryKey(tbOrder.getOrderId());
    }

}
