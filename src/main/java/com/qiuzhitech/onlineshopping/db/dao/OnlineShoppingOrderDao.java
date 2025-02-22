package com.qiuzhitech.onlineshopping.db.dao;

import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;

public interface OnlineShoppingOrderDao {
  int deleteOrderById(Long orderId);
  int insertOrder(OnlineShoppingOrder record);
  OnlineShoppingOrder queryOrderById(Long orderId);
  int updateOrder(OnlineShoppingOrder record);

  OnlineShoppingOrder queryOrderByOrderNo(String orderNo);
}
