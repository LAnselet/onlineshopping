package com.qiuzhitech.onlineshopping.db.dao.impl;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.mappers.OnlineShoppingCommodityMapper;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;

import org.springframework.stereotype.Repository;

import java.util.List;

import javax.annotation.Resource;

@Repository
public class OnlineShoppingCommodityDaoImpl implements OnlineShoppingCommodityDao {

  @Resource
  OnlineShoppingCommodityMapper commodityMapper;

  @Override
  public int deleteCommodityById(Long commodityId) {
    return 0;
  }

  @Override
  public int insertCommodity(OnlineShoppingCommodity record) {
    return commodityMapper.insert(record);
  }

  @Override
  public OnlineShoppingCommodity queryCommodityById(Long commodityId) {
    return commodityMapper.selectByPrimaryKey(commodityId);
  }

  @Override
  public int updateCommodity(OnlineShoppingCommodity record) {
    return commodityMapper.updateByPrimaryKey(record);
  }

  @Override
  public List<OnlineShoppingCommodity> listCommodities() {
    return commodityMapper.listCommodities();
  }

  @Override
  public List<OnlineShoppingCommodity> listCommoditiesByUserId(long l) {
    return commodityMapper.listCommoditiesByUserId(l);
  }

  @Override
  public int deductStock(long commodityId) {
    return commodityMapper.deductStock(commodityId);
  }

  @Override
  public void revertStock(Long commodityId) {
    commodityMapper.revertStock(commodityId);
  }

}
