package com.qiuzhitech.onlineshopping.service.mq;

import com.alibaba.fastjson.JSON;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingCommodityDao;
import com.qiuzhitech.onlineshopping.db.dao.OnlineShoppingOrderDao;
import com.qiuzhitech.onlineshopping.db.po.OnlineShoppingOrder;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RocketMQMessageListener(topic = "createOrder", consumerGroup =
        "createOrderConsumerGroup")
public class CreateOrderListener implements RocketMQListener<MessageExt> {
    @Resource
    OnlineShoppingCommodityDao onlineShoppingCommodityDao;

    @Resource
    OnlineShoppingOrderDao onlineShoppingOrderDao;

    @Resource
    private RocketMQService rocketMQService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String message = new String(messageExt.getBody());
        log.info("createOrder message body:{}", message);
        OnlineShoppingOrder order = JSON.parseObject(message,
                OnlineShoppingOrder.class);
        int res = onlineShoppingCommodityDao.deductStock(order.getCommodityId());
        if (res == 1) {
            // 1. already create order, pending for payment
            order.setOrderStatus(1);
            // Send delay message to MQ for check payment is overtime or not
            // messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m
            // 20m 30m 1h 2h
            try {
                rocketMQService.sendDelayMessage("paymentCheck",
                        JSON.toJSONString(order), 4);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            order.setOrderStatus(0);
        }
        onlineShoppingOrderDao.insertOrder(order);
        log.info("insert order with mq:{}", order);
    }
}
