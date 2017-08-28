package com.hzfh.activiemq.service;

import com.alibaba.fastjson.JSONObject;
import com.hzfh.entity.MessageTest;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.stereotype.Component;

import javax.jms.*;

/**
 * com.hzfh.activiemq.service
 *
 * @author rencc
 * @Note 消费者消费发送消息 TextMessage
 * @Date 2017-08-22 17:29
 */
@Component
public class ConsumerModel {
    private static final String url="tcp://127.0.0.1:61616";
    private static final String queueName="2017-08-22";
    public MessageTest createCustomer(int id) throws JMSException, InterruptedException {
         MessageTest messageTest = new MessageTest();
            //1，创建ConnectionFacytory
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);

            //2,创建连接Connection
            Connection connection = connectionFactory.createConnection();

            //3，启动链接
            connection.start();

            //4创建会话
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            //5,创建一个目标
            Destination destination = session.createQueue(queueName);

            //6 创建一个消费者
            MessageConsumer consumer = session.createConsumer(destination,"id ="+id);//消息过滤选择消费

            //7创建一个监听器
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        JSONObject parse = JSONObject.parseObject(textMessage.getText());
                        messageTest = JSONObject.toJavaObject(parse, MessageTest.class);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            //8，关闭连接
            connection.close();
        return messageTest;
    }
}
