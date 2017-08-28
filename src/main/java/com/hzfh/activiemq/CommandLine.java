package com.hzfh.activiemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.core.JmsTemplate;

/**
 * com.hzfh.activiemq
 *
 * @author rencc
 * @Note
 * @Date 2017-08-22 11:23
 */
@SpringBootApplication
public class CommandLine implements CommandLineRunner{
    public static void main(String[] args) {
        SpringApplication.run(CommandLine.class, args);
    }
    @Autowired
    JmsTemplate jmsTemplate;
    @Override
    public void run(String... strings) throws Exception {
        jmsTemplate.send("distination",new CreateMessage());
    }
}
