package com.zrd.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.zrd.springbootinit.constant.BIMessageConstant;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class BiMessageProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message)
    {
        rabbitTemplate.convertAndSend(BIMessageConstant.BI_EXCHANGE_NAME,BIMessageConstant.BI_ROUTING_KEY_NAME,message);
    }

}
