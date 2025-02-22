package com.qiuzhitech.onlineshopping.service;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CommodityService {

  @Resource
  OnlineShoppingCommodityDao dao;

  public OnlineShoppingCommodity queryCommodityById(Long commodityId) {
    return dao.queryCommodityById(commodityId);
  }
}
