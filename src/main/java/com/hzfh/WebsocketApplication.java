package com.hzfh;

import com.hzfh.activiemq.CreateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

@SpringBootApplication
public class WebsocketApplication implements CommandLineRunner {
	@Autowired
	JmsTemplate jmsTemplate;
	public static void main(String[] args) {
		SpringApplication.run(WebsocketApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		jmsTemplate.send("test",new CreateMessage());
	}
}
