package com.qiuzhitech.onlineshopping.db.dao.impl;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping.db.mappers.OnlineShoppingOrderMapper;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;

import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

@Repository
public class OnlineShoppingOrderDaoImpl implements OnlineShoppingOrderDao {

  @Resource
  OnlineShoppingOrderMapper onlineShoppingOrderMapper;

  @Override
  public int deleteOrderById(Long orderId) {
    return 0;
  }

  @Override
  public int insertOrder(OnlineShoppingOrder record) {
    return onlineShoppingOrderMapper.insert(record);
  }

  @Override
  public OnlineShoppingOrder queryOrderById(Long orderId) {
    return null;
  }

  @Override
  public int updateOrder(OnlineShoppingOrder record) {
    return 0;
  }

  @Override
  public OnlineShoppingOrder queryOrderByOrderNo(String orderNo) {
    return onlineShoppingOrderMapper.queryOrderByOrderNo(orderNo);
  }

}
