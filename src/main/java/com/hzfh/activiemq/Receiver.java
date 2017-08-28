package com.hzfh.activiemq;

import com.alibaba.fastjson.JSONObject;
import com.hzfh.entity.MessageTest;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * com.hzfh.activiemq
 *
 * @author rencc
 * @Note 消息队列监听器
 * @Date 2017-08-22 11:26
 */
@Component
public class Receiver {
    private static final String destination="test";//要监听的队列名

    @JmsListener(destination = destination)
    public void receiveMessage(String message){
        JSONObject parse = JSONObject.parseObject(message);
        MessageTest messageTest = JSONObject.toJavaObject(parse, MessageTest.class);
        System.out.println("接收到："+messageTest.toString());
    }
}
