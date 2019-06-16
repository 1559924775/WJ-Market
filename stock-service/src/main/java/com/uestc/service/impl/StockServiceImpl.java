package com.uestc.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.uestc.dao.TbStockMapper;
import com.uestc.domain.TbStock;
import com.uestc.domain.TbStockExample;
import com.uestc.stock.StockService;
import com.uestc.stock.vo.StockVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;
@Service
public class StockServiceImpl implements StockService {
    @Autowired
    TbStockMapper tbStockMapper;


    @Override
    public StockVO selectOne(String goodsId) {
        TbStockExample tbStockExample=new TbStockExample();
        TbStockExample.Criteria criteria=tbStockExample.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        List<TbStock> tbStocks =tbStockMapper.selectByExample(tbStockExample);

        if(tbStocks!=null){
            TbStock tbStock=tbStocks.get(0);
            StockVO stockVO=new StockVO();
            stockVO.setCreatTime(tbStock.getCreatTime());
            stockVO.setGoodsId(tbStock.getGoodsId());
            stockVO.setSellerId(tbStock.getSellerId());
            stockVO.setStock(tbStock.getStock());
            stockVO.setVersion(tbStock.getVersion());
            stockVO.setUpdateTime(tbStock.getUpdateTime());
            return  stockVO;
        }
        return null;
    }


    @Override
    //example是找到对象，tbStock是对象的更新信息
    public int updateStoreCountByVersion(int currentVersion,  String goodsId, Date updateTime,int newNum) {
        TbStock tbStock=new TbStock();
        tbStock.setVersion(currentVersion+1);
        tbStock.setUpdateTime(updateTime);
        tbStock.setStock(newNum);

        TbStockExample tbStockExample=new TbStockExample();
        TbStockExample.Criteria criteria=tbStockExample.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        criteria.andVersionEqualTo(currentVersion);
        //CAS更新
        return tbStockMapper.updateByExampleSelective(tbStock,tbStockExample);
    }


}
