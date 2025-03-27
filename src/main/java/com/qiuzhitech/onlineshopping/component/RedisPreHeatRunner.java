package com.qiuzhitech.onlineshopping.component;

import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping.service.EsService;
import com.qiuzhitech.onlineshopping.service.RedisService;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class RedisPreHeatRunner implements ApplicationRunner {
    @Resource
    RedisService redisService;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    EsService esService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<OnlineShoppingCommodity> onlineShoppingCommodities = commodityDao.listCommodities();
        for (OnlineShoppingCommodity commodity : onlineShoppingCommodities) {
            redisService.setValue("commodity:" + commodity.getCommodityId(), commodity.getAvailableStock().toString());
            esService.addCommodity(commodity);
            log.info("Preheat starting, initialize commodity:{}", commodity.getCommodityId());
        }
    }
}
