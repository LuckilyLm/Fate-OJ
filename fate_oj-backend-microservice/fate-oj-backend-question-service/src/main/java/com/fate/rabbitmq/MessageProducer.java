package com.fate.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * RabbitMQ消息生产者
 * @Author: Fate
 * @Date: 2024/7/7 21:11
 **/

@Component
public class MessageProducer
{
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String exchange, String routingKey, String message){
        System.err.println("Sending message: " + message);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
