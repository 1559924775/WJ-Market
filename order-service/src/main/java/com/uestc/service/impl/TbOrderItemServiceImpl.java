package com.uestc.service.impl;

import com.uestc.dao.TbOrderItemMapper;
import com.uestc.domain.TbOrderItem;
import com.uestc.service.TbOrderItemService;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.springframework.beans.factory.annotation.Autowired;

public class TbOrderItemServiceImpl implements TbOrderItemService {
    @Autowired
    TbOrderItemMapper tbOrderItemMapper;

    @Compensable(propagation = Propagation.REQUIRES_NEW,confirmMethod = "confirmInsert", cancelMethod = "cancelInsert", asyncConfirm = true)
    public void insert(TbOrderItem tbOrderItem){
        tbOrderItem.setStatusTcc("inserting");
        tbOrderItemMapper.insert(tbOrderItem);
    }

    public void confirmInsert(TbOrderItem tbOrderItem){
        tbOrderItem.setStatusTcc("inserted");
        tbOrderItemMapper.updateByPrimaryKeySelective(tbOrderItem);
    }

    public void cancelInsert(TbOrderItem tbOrderItem){
        tbOrderItemMapper.deleteByPrimaryKey(tbOrderItem.getId());
    }

}
