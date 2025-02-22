package com.qiuzhitech.onlineshopping.service;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingCommodity;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping.service.mq.RocketMQService;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrderService {

    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @Resource
    OnlineShoppingOrderDao onlineShoppingOrderDao;

    @Resource
    RocketMQService rocketMQService;

    @Resource
    private RedisService redisService;

    public OnlineShoppingOrder createOrder(long commodityId, long userId) {
        OnlineShoppingOrder order = OnlineShoppingOrder
                .builder()
                .commodityId(commodityId)
                .userId(userId)
                .orderNo(UUID.randomUUID().toString())
                .createTime(new Date())
                .orderStatus(1)
                .orderAmount(1L)
                .build();
        onlineShoppingOrderDao.insertOrder(order);
        return order;
    }

    public OnlineShoppingOrder placeOrder(long commodityId, long userId) {
        OnlineShoppingCommodity onlineShoppingCommodity =
                onlineShoppingCommodityDao.queryCommodityById(commodityId);
        int availableStock = onlineShoppingCommodity.getAvailableStock();
        int lockStock = onlineShoppingCommodity.getLockStock();
        if (onlineShoppingCommodity.getAvailableStock() > 0) {
            OnlineShoppingOrder onlineShoppingOrder = createOrder(userId, commodityId);
//            onlineShoppingOrderDao.insertOrder(onlineShoppingOrder);
            availableStock--;
            lockStock++;
            onlineShoppingCommodity.setAvailableStock(availableStock);
            onlineShoppingCommodity.setLockStock(lockStock);
            onlineShoppingCommodityDao.updateCommodity(onlineShoppingCommodity);
            return onlineShoppingOrder;
        } else {
            return null;
        }
    }

    public OnlineShoppingOrder queryOrderByOrderNo(String orderNo) {
        return onlineShoppingOrderDao.queryOrderByOrderNo(orderNo);
    }

    public int updateOrderStatus(String orderNo, int status) {
        OnlineShoppingOrder order = onlineShoppingOrderDao.queryOrderByOrderNo(orderNo);
        order.setOrderStatus(status);
        order.setPayTime(new Date());
        return onlineShoppingOrderDao.updateOrder(order);
    }

    public OnlineShoppingOrder placeOrderOneSQL(long commodityId, long userId) {
        int result = onlineShoppingCommodityDao.deductStock(commodityId);
        if (result > 0) {
            log.info("Place order successfully");
            return createOrder(commodityId, userId);
        }
        log.info("Process order failed");
        return null;
    }

    // redis，先把db数据预热缓存到redis，在引入redis+lua脚本，实现操作原子化
    public OnlineShoppingOrder placeOrderRedis(long commodityId,
                                               long userId) {
        String redisKey = "commodity:" + commodityId;
        long result = redisService.stockDeduct(redisKey);
        if (result >= 0) {
            OnlineShoppingOrder order = placeOrder(commodityId, userId);
            log.info("Place order successfully, orderNum:{}", order.getOrderNo());
            return order;
        } else {
            log.warn("commodity out of stock, commodityId:{}", commodityId);
            return null;
        }
    }

    public OnlineShoppingOrder placeOrderRedisFinal(long commodityId, long userId) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        String redisKey = "commodity:" + commodityId;
        long result = redisService.stockDeduct(redisKey);
        if (result >= 0) {
            OnlineShoppingOrder order = OnlineShoppingOrder
                    .builder()
                    .commodityId(commodityId)
                    .userId(userId)
                    .orderNo(UUID.randomUUID().toString())
                    .createTime(new Date())
                    .orderStatus(1)
                    .orderAmount(1L)
                    .build();
            rocketMQService.sendMessage("createOrder",
                    JSON.toJSONString(order));
            log.info("Place order successfully, orderNum:{}", order.getOrderNo());
            return order;
        } else {
            log.warn("commodity out of stock, commodityId:{}", commodityId);
            return null;
        }
    }

    // distributed lock
    public OnlineShoppingOrder placeOrderDistributedLock(long commodityId,
                                                         long userId) {
        String lockKey = "lock_commodity:" + commodityId;
        String requestId = UUID.randomUUID().toString();
        boolean result = redisService.tryGetDistributedLock(lockKey, requestId,
                5000);
        if (result) {
            OnlineShoppingOrder order = placeOrder(commodityId, userId);
            redisService.releaseDistributedLock(lockKey, requestId);
            return order;
        } else {
            log.warn("process failed, try again later");
        }
        return null;
    }
}
