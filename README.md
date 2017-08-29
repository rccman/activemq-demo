# activemq-demo
ActiveMQDemo Spring Boot 后台的生产者消费者，监听器等

CSDN：[加了白糖的老干妈](http://blog.csdn.net/qq_21358931/article/details/77574178)


# 我与ActiveMQ的恩怨情仇

## 概要
为什么写道***我与ActiveMQ的恩怨情仇*** ，其实这一切缘于最初对ActiveMQ的学习和应用.......

[TOC]

## ActiveMQ 介绍
ActiveMQ 是Apache出品，最流行的，能力强劲的开源消息总线。ActiveMQ 是一个完全支持JMS1.1和J2EE 1.4规范的 JMS Provider实现，尽管JMS规范出台已经是很久的事情了，但是JMS在当今的J2EE应用中间仍然扮演着特殊的地位。
## ActiveMQ特性
- ⒈ 多种语言和协议编写客户端。语言: Java,C,C++,C#,Ruby,Perl,Python,PHP。应用协议： OpenWire,Stomp REST,WS Notification,XMPP,AMQP

- ⒉ 完全支持JMS1.1和J2EE 1.4规范 （持久化，XA消息，事务)

- ⒊ 对spring的支持，ActiveMQ可以很容易内嵌到使用spring的系统里面去，而且也支持Spring2.0的特性

- ⒋ 通过了常见J2EE服务器（如 Geronimo,JBoss 4,GlassFish,WebLogic)的测试，其中通过JCA 1.5 resource adaptors的配置，可以让ActiveMQ可以自动的部署到任何兼容J2EE 1.4 商业服务器上

- ⒌ 支持多种传送协议：in-VM,TCP,SSL,NIO,UDP,JGroups,JXTA

- ⒍ 支持通过JDBC和journal提供高速的消息持久化

- ⒎ 从设计上保证了高性能的集群，客户端-服务器，点对点

- ⒏ 支持Ajax

- ⒐ 支持与Axis的整合

- ⒑ 可以很容易得调用内嵌JMS provider，进行测试

## 干货开始
### ActiveMQ组成
其实ActiveMQ的组成和其他消息队列类似，或者基本相同，都有生产者生产消息，用于存消息的队列(Queue)或者(Topics)。然后还有从队列里取消息的消费者。

### 基础版的消费者和生产者

-  ***生产者***

```java
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
        textMessage.setIntProperty("id",messageTest.getId());//带"过滤"的消息选择器
        //8发布消息
        producer.send(textMessage);
        //9，关闭连接
        connection.close();
    }
}

```
 
- ***消费者***
```java
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

            //6 创建一个消费者,消息过滤选择消费
            MessageConsumer consumer = session.createConsumer(destination,"id ="+id);

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

```

### 升级版生产者和消费者

所谓升级版，其实是ActiveMQ集成Spring Boot后，采用注解配置后监听器的方式来实现，生产者生产消息，消费者监听消费的队列。（实时消费）

- ***Spring Boot 注解配置（application.properties）***

```xml
spring.activemq.broker-url=tcp://localhost:61616
```
- ***生产消息类***

```java
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
 * @Note 生产消息的类
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

```

- ***生产发送消息实现***

仅仅是为了实现效果，我在Spring Boot的启动类里直接使用发消息。

```java
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
		jmsTemplate.send("test",new CreateMessage());//test 为队列名
	}
}

```

- ***消息监听器***

```java
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

```
## 恩怨情仇

以上内容都是干货，自此之后开始唠叨，我与ActiveMQ的恩怨情仇。<br/> 
&emsp;&emsp;研究ActiveMQ是因为，公司开发的基于Spring Boot架构的企业后台管理系统，在工作流的待审待办消息那儿，没有实现实时的消息提醒，以至于用户每次查看新消息都需要F5,用户体验性差。所以前几天接到任务，实现公司现有系统的待审待办消息实时提醒功能。<br/> 
&emsp;&emsp;百度相关内容之后，发现实现方式要么Ajax轮询或者借助消息中间件。至此决定采用ActiveMQ消息队列。初步研究后，使用SpringBoot搭建一个Demo实现了后台生产消息，监听器消费消息。但是我需要的是要实现浏览器实时的更新提醒消息。所以深入研究之后，从官网上找到了ajax方式监听消息队列的方式。可就在此时，难题来了，ajax方式需要配置servlet来后台处理，官网所有文档指出的都是在web.xml里配置servlet。

```xml
<!-- 配置支持ajax的jms -->  
    <context-param>  
        <param-name>org.apache.activemq.brokerURL</param-name>  
        <param-value>tcp://localhost:61616</param-value>  
        <description>连接到消息中间件的URL</description>  
    </context-param>  
    <servlet>  
    <servlet-name>AjaxServlet</servlet-name>  
    <servlet-class>org.apache.activemq.web.AjaxServlet</servlet-class>  
    <load-on-startup>1</load-on-startup>  
    </servlet>  
    <servlet-mapping>  
    <servlet-name>AjaxServlet</servlet-name>  
    <url-pattern>/amq/*</url-pattern>  
    </servlet-mapping>  
```
&emsp; 但是SpringBoot是没有web.xml的，后来就去研究SpringBoot注册Servlet的方式，使用注解也好，还是在配置类里注册也好，都是不行。哎，头疼。

- ***注册方式***

```java
package org.springboot.sample;

import org.springboot.sample.servlet.MyServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
public class SpringBootSampleApplication {

    /**
     * 使用代码注册Servlet
     *
     * @return
     * @author SHANHY
     * @create  2016年1月6日
     */
    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        return new ServletRegistrationBean(new MyServlet(), "/amp/*");
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootSampleApplication.class, args);
    }
}
```
但是还需要再MyServlet类前添加
> @WebServlet(urlPatterns="/xs/*", description="Servlet的说明")

&emsp;&emsp; 孰不知我要配置的是官方jar包里的类，这怎么加注解....后来打算，大不了重写官方给的AjaxServlet.不管怎么样，重写肯定OK的。<br/>
&emsp;&emsp; 记得决定这样做当时已经是下班了，回家的路上，我在10号线地铁上反复思考，假如这种方式可以，那如何保证队列里的消息和数据库待审待办表里的数据永远保持一致呢？队列里添加消息容易，但是删除呢？思考良久发现不可行。至此，我两天的研究的时间白费了。前期考察不充分，血淋林赤裸裸的教训。<br/>

&emsp;&emsp; 第三天痛定思痛，采用ajax轮询实现。

## 写在最后
> ActiveMQ应用于异步消息同步，多用于系统之间消息同步，减缓用户延迟等场景。