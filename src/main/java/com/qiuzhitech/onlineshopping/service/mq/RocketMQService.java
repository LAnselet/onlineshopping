package com.qiuzhitech.onlineshopping.service.mq;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RocketMQService {

    @Resource
    RocketMQTemplate template;

    public void sendMessage(String topic, String messageContent) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message message = new Message(topic, messageContent.getBytes());
        template.getProducer().send(message);
    }

    public void sendDelayMessage(String topic, String body, int delayTimeLevel) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        Message message = new Message(topic, body.getBytes());
        message.setDelayTimeLevel(delayTimeLevel);
        template.getProducer().send(message);
    }


}
