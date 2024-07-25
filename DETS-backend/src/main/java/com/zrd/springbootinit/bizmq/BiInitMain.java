package com.zrd.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.zrd.springbootinit.constant.BIMessageConstant;

public class BiInitMain {
    public static void main(String[] args) {
        //创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("localhost");
        try {
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            //创建交换机
            channel.exchangeDeclare(BIMessageConstant.BI_EXCHANGE_NAME,"direct");

            //创建队列
            String queueName = BIMessageConstant.BI_QUEUE_NAME;
            channel.queueDeclare(queueName,true,false,false,null);
            channel.queueBind(queueName,BIMessageConstant.BI_EXCHANGE_NAME,BIMessageConstant.BI_ROUTING_KEY_NAME);
        } catch (Exception e) {

        }

    }
}
