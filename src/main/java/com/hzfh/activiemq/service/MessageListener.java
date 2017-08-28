package com.hzfh.activiemq.service;

import com.alibaba.fastjson.JSONObject;
import com.hzfh.entity.MessageTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * com.hzfh.activiemq.service
 *
 * @author rencc
 * @Note
 * @Date 2017-08-22 17:40
 */
public class MessageListener implements javax.jms.MessageListener {
    private MessageTest messageTest;
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            JSONObject parse = JSONObject.parseObject(textMessage.getText());
            messageTest = JSONObject.toJavaObject(parse, MessageTest.class);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
    public MessageTest getMessageTest(){
        return this.messageTest;
    }
}
