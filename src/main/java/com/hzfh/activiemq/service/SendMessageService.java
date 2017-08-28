package com.hzfh.activiemq.service;

import com.alibaba.fastjson.JSONObject;
import com.hzfh.entity.MessageTest;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.web.AjaxServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.jms.*;

/**
 * com.hzfh.activiemq.service
 *
 * @author rencc
 * @Note
 * @Date 2017-08-22 15:10
 */
@Service
public class SendMessageService {
    @Autowired
    private JmsMessagingTemplate jmsTemplate;
    private static final String url="tcp://127.0.0.1:61616";
    private static final String queueName="2017-08-22 17:19:56";

    private Session session;
    public void sendMessage(String queues, MessageTest message){
        Destination destination = new ActiveMQQueue(queues);
        jmsTemplate.convertAndSend(destination, message);
    }
    public void sendMapMessage(String queues, MessageTest message) throws JMSException {
        Destination destination = new ActiveMQQueue(queues);
        jmsTemplate.convertAndSend(destination, message);
    }

    //创建消费者
    public void createCustomer(String id){
        try {
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
            MessageConsumer consumer = session.createConsumer(destination,"id ="+id);

            //7创建一个监听器
            consumer.setMessageListener(new MessageListener() {
                public void onMessage(Message message) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        JSONObject parse = JSONObject.parseObject(textMessage.getText());
                        MessageTest messageTest = JSONObject.toJavaObject(parse, MessageTest.class);
                        System.out.println("接受到信息"+messageTest.toString());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            //8，关闭连接
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
