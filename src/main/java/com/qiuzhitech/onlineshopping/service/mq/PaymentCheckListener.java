package com.qiuzhitech.onlineshopping.service.mq;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping.db.dao.impl.OnlineShoppingOrderDaoImpl;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;
import com.qiuzhitech.onlineshopping.service.RedisService;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RocketMQMessageListener(topic = "paymentCheck", consumerGroup =
        "paymentCheckGroup")
public class PaymentCheckListener implements RocketMQListener<MessageExt> {

    @Resource
    OnlineShoppingOrderDao onlineShoppingOrderDao;

    @Resource
    OnlineShoppingCommodityDao commodityDao;

    @Resource
    private RedisService redisService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());
        log.info("paymentCheck Message Body:{}", message);
        OnlineShoppingOrder orderFromMessage = JSON.parseObject(message,
                OnlineShoppingOrder.class);
        OnlineShoppingOrder order =
                onlineShoppingOrderDao.queryOrderByOrderNo(orderFromMessage.getOrderNo());
        if (order == null) {
            log.error("can't find order in db");
            return;
        }
        // 1. check current Order status in DB
        // Status as below:
        // 0. Invalid order, Since no available stock
        // 1. already create order, pending for payment
        // 2. finished payment
        // 99. invalid order due to payment proceed overtime
        if (order.getOrderStatus() != 2) {
            //2. change order status to 0, invalid the order
            log.info("Didn't pay the order on time, order number:{}",
                    order.getOrderNo());
            order.setOrderStatus(99);
            onlineShoppingOrderDao.updateOrder(order);
            //3. Update Commodity Table
            commodityDao.revertStock(order.getCommodityId());
            //4. Update Redis stock
            String redisKey = "commodity:" + order.getCommodityId();
            redisService.revertStock(redisKey);
            redisService.removeFromDenyList(order.getUserId(),
                    order.getCommodityId());
        } else {
            log.info("Skip operation for order:{}", order);
        }
    }
}
