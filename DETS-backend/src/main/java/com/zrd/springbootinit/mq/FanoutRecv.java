package com.zrd.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class FanoutRecv {
    private static final String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] argv) throws Exception {
        //创建链接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        //创建频道
        Channel channel = connection.createChannel();

        //创建交换机
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        //创建队列
        //String queueName = channel.queueDeclare().getQueue(); 随机创建一个名称
        String queueName = "订阅工作队列1";
        channel.queueDeclare(queueName, true, false, false, null);
        //进行绑定
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        //创建交换机
        String queueName2 = "订阅工作队列2";
        channel.queueDeclare(queueName2, true, false, false, null);
        //进行绑定
        channel.queueBind(queueName2,EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" 工作队列1 Received '" + message + "'");
        };

        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" 工作队列2 Received '" + message + "'");
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        channel.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });
    }
}