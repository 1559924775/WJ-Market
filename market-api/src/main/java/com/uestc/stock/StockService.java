package com.uestc.stock;

import com.uestc.stock.vo.StockVO;

import java.util.Date;

public interface StockService {



    StockVO selectOne( String goodsId);

    public int freezeStoreCountByVersion(int currentVersion,  String goodsId, Date updateTime,int newNum,int freezeNum);

    public void reduceFreezeCount(String goodsId, Date updateTime,int freezeNum);

    public void updateStockCount(String goodsId, Date updateTime,int freezeNum);
}
