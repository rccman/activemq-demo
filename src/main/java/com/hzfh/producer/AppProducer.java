package com.hzfh.producer;

import com.alibaba.fastjson.JSONObject;
import com.hzfh.entity.MessageTest;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * com.hzfh.producer
 *
 * @author rencc
 * @Note
 * @Date 2017-08-22 9:29
 */
public class AppProducer {
    private static final String url="tcp://127.0.0.1:61616";
    private static final String queueName="send-only";
    public static void main(String[] args)throws JMSException {
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

        MessageTest message = new MessageTest();
        message.setId(110);
        message.setMessage("指定唯一消费者");
        String json = JSONObject.toJSONString(message);
        TextMessage textMessage = session.createTextMessage(json);
        textMessage.setIntProperty("id",110);
        producer.send(textMessage);
//        for(int i=0;i<100;i++){
//            //7,创建消息
//            TextMessage testMessage= session.createTextMessage("Text"+i);
//            //8发布消息
//            producer.send(testMessage);
//            System.out.println("发送消息"+testMessage.getText());
//        }
        //9，关闭连接
        connection.close();
    }
}
