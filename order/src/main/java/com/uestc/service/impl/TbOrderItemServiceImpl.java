package com.uestc.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.uestc.dao.TbOrderItemMapper;
import com.uestc.domain.TbOrderItem;
import com.uestc.service.TbOrderItemService;
import com.uestc.stock.StockService;
import org.mengyun.tcctransaction.api.Compensable;
import org.mengyun.tcctransaction.api.Propagation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
@Service
public class TbOrderItemServiceImpl implements TbOrderItemService {
    @Autowired
    TbOrderItemMapper tbOrderItemMapper;

    @Reference
    StockService stockService;

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


    @Compensable(propagation = Propagation.REQUIRES_NEW,confirmMethod = "confirmInsertAll", cancelMethod = "cancelInsertAll", asyncConfirm = true)
    public void insertAll(List<TbOrderItem> tbOrderItems){
        for(TbOrderItem tbOrderItem:tbOrderItems){
            tbOrderItem.setStatusTcc("inserting");
            tbOrderItemMapper.insert(tbOrderItem);
        }
    }

    public void confirmInsertAll(List<TbOrderItem> tbOrderItems){
        for(TbOrderItem tbOrderItem:tbOrderItems){
            tbOrderItem.setStatusTcc("inserted");
            tbOrderItemMapper.updateByPrimaryKeySelective(tbOrderItem);
            //把冻结的库存减掉
            stockService.reduceFreezeCount(tbOrderItem.getGoodsId(),new Date(),tbOrderItem.getNum());
        }
    }

    public void cancelInsertAll(List<TbOrderItem> tbOrderItems){
        for(TbOrderItem tbOrderItem:tbOrderItems){
            tbOrderItemMapper.deleteByPrimaryKey(tbOrderItem.getId());
            //把冻结的库存加回去
            stockService.updateStockCount(tbOrderItem.getGoodsId(),new Date(),tbOrderItem.getNum());
        }
    }
}
