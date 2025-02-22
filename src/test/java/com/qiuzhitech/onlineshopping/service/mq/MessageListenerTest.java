package com.qiuzhitech.onlineshopping.service.mq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageListenerTest {

    @Resource
    RocketMQService service;

    @Test
    void onMessage() throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        service.sendMessage("testTopic", "test rocketMQ");
    }
}