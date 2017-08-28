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
 * @Note 生产者发送消息 TextMessage
 * @Date 2017-08-22 17:23
 */
@Component
public class ProducerModel {
    private static final String url="tcp://127.0.0.1:61616";
    private static final String queueName="2017-08-23";
    public void sendMessage(MessageTest messageTest) throws JMSException {
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

        //6,创建一个生产者
        MessageProducer producer = session.createProducer(destination);

        //7,创建消息
        TextMessage textMessage = session.createTextMessage(JSONObject.toJSONString(messageTest));
        textMessage.setIntProperty("id",messageTest.getId());
        //8发布消息
        producer.send(textMessage);
        //9，关闭连接
        connection.close();
    }
}
