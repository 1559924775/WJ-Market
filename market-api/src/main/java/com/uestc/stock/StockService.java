package com.uestc.stock;

import com.uestc.stock.vo.StockVO;

import java.util.Date;

public interface StockService {



    StockVO selectOne( String goodsId);

    /**
     * 更新库存，返回更新的行数
     * @param currentVersion
     * @param goodsId
     * @param updateTime
     * @return
     */
    int updateStoreCountByVersion(int currentVersion,String goodsId, Date updateTime,int num);


}
