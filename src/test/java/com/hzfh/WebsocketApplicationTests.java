package com.hzfh;

import com.alibaba.fastjson.JSONObject;
import com.hzfh.activiemq.CreateMessage;
import com.hzfh.activiemq.service.ConsumerModel;
import com.hzfh.activiemq.service.ProducerModel;
import com.hzfh.activiemq.service.SendMessageService;
import com.hzfh.entity.MessageTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WebsocketApplicationTests {
	@Autowired
	private SendMessageService sendMessageService;
	@Autowired
	private ProducerModel producerModel;
	@Autowired
	private ConsumerModel consumerModel;

	@Test
	public void contextLoads() {
		sendMessageService.createCustomer(110+"");
	}

	@Test
	public void name() throws Exception {
		MessageTest message = new MessageTest();
		message.setId(110);
		message.setMessage("手动send");
		String json = JSONObject.toJSONString(message);
		sendMessageService.sendMessage("2017-08-22 17:19:56",message);
	}

	@Test
	public void producerModellTest() throws Exception {
		MessageTest message = new MessageTest();
		message.setId(110);
		message.setMessage("封装测试");
		producerModel.sendMessage(message);
	}

	@Test
	public void consumerModelllTest() throws Exception {
		MessageTest messageTest = consumerModel.createCustomer(110);
		System.out.println(messageTest==null?"当前用户没有消息（被过滤掉了）":messageTest.toString());
	}
}
