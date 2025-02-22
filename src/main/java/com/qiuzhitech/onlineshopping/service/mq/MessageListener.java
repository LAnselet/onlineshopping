package com.qiuzhitech.onlineshopping.service.mq;

import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RocketMQMessageListener(topic = "testTopic", consumerGroup =
        "testConsumerGroup")
public class MessageListener implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        log.info("receive message:" + new String(messageExt.getBody()));
    }
}
