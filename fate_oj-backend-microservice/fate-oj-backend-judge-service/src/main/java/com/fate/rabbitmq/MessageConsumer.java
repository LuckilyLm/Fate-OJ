package com.fate.rabbitmq;

import com.fate.judge.JudgeService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @Author: Fate
 * @Date: 2024/7/7 21:19
 **/

@Component
@Slf4j
public class MessageConsumer
{

    @Resource
    private JudgeService judgeService;
    // TODO 改为自己的队列名
    @RabbitListener(queues = "code_queue",ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel,@Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {
        long questionSubmitId = Long.parseLong(message);
        System.err.println(questionSubmitId);
        try {
            judgeService.doJudge(questionSubmitId);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error(e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }
}
