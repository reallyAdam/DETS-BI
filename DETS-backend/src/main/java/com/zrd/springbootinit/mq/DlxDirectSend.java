package com.zrd.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class DlxDirectSend {

    private static final String EXCHANGE_NAME = "direct_exchange";

    public static void main(String[] argv) throws Exception {
        //建立链接
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             //创建频道
             Channel channel = connection.createChannel()) {
            //创建交换机
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            channel.exchangeDeclare("some.exchange.name", "direct");

            Map<String, Object> args = new HashMap<String, Object>();
            args.put("x-dead-letter-exchange", "some.exchange.name");
            channel.queueDeclare("myqueue", false, false, false, args);
            channel.queueBind("myqueue", EXCHANGE_NAME, "前端.#");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                // 读取用户在控制台输入的下一行文本
                String userInput = scanner.nextLine();
                String[] split = userInput.split(" ");
                if(split.length < 1)
                {
                    continue;
                }
                String message = split[1];
                String routeKey = split[0];
                //发送消息
                channel.basicPublish(EXCHANGE_NAME, routeKey, null, message.getBytes("UTF-8"));
                System.out.println(" [x] Sent '" + routeKey + "':'" + message + "'");
            }
        }
    }
    //..
}
