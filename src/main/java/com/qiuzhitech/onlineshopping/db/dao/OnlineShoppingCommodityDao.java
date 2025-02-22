package com.qiuzhitech.onlineshopping.db.dao;

import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;

import java.util.List;

public interface OnlineShoppingCommodityDao {
    int deleteCommodityById(Long commodityId);
    int insertCommodity(OnlineShoppingCommodity record);
    OnlineShoppingCommodity queryCommodityById(Long commodityId);
    int updateCommodity(OnlineShoppingCommodity record);
    List<OnlineShoppingCommodity> listCommodities();
    List<OnlineShoppingCommodity> listCommoditiesByUserId(long l);
    int deductStock(long commodityId);

    void revertStock(Long commodityId);
}
