package com.qiuzhitech.onlineshopping.service;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;

import org.springframework.stereotype.Service;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SearchService {

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    EsService esService;

    public List<OnlineShoppingCommodity> searchCommodityWithMySQL(String keyWord) {
        return commodityDao.searchCommodityByKeyWord(keyWord);
    }

    public List<OnlineShoppingCommodity> searchCommodityWithES(String keyWord) {
        return esService.searchCommodity(keyWord, 0, 10);
    }
}
