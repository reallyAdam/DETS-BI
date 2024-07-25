package com.zrd.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class DlxDirectRecv {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {
        //建立链接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        //创建频道
        Channel channel1 = connection.createChannel();

        //建立交换机
        channel1.exchangeDeclare(EXCHANGE_NAME, "direct");

        //创建队列
        String queueName = "工作队列1";
        channel1.queueDeclare(queueName, true, false, false, null);
        //进行绑定
        channel1.queueBind(queueName, EXCHANGE_NAME, "one");

        //创建队列
        String queueName2 = "工作队列2";
        channel1.queueDeclare(queueName2, true, false, false, null);
        //进行绑定
        channel1.queueBind(queueName2, EXCHANGE_NAME, "two");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [工作队列1] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };

        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [工作队列2] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        channel1.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
        channel1.basicConsume(queueName2, true, deliverCallback2, consumerTag -> { });
    }
}