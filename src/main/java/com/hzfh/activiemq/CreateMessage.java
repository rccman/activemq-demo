package com.hzfh.activiemq;

        import com.alibaba.fastjson.JSONObject;
        import com.hzfh.entity.MessageTest;
        import org.apache.activemq.command.ActiveMQQueue;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.jms.core.JmsMessagingTemplate;
        import org.springframework.jms.core.MessageCreator;
        import org.springframework.stereotype.Service;

        import javax.jms.*;

/**
 * com.hzfh.activiemq
 *
 * @author rencc
 * @Note
 * @Date 2017-08-22 11:18
 */
public class CreateMessage implements MessageCreator{
    @Override
    public Message createMessage(Session session) throws JMSException {
        MessageTest message = new MessageTest();
        message.setId(110);
        message.setMessage("测试Object");
        String json = JSONObject.toJSONString(message);
        return session.createTextMessage(json);
    }
}
