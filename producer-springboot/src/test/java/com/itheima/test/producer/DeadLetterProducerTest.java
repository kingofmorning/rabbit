package com.itheima.test.producer;

import com.itheima.ProducerApplication;
import com.itheima.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest(classes = ProducerApplication.class)
@RunWith(SpringRunner.class)
public class DeadLetterProducerTest {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 向延迟队列发送消息
     * 注意这里设置的是setExpiration，而不是delay。
     */
    @Test
    public void send() {
        for (int i = 0; i < 10; i++) {

            //两条数据测试 rabbithandle
            rabbitTemplate.convertAndSend(
                    "immediate_exchange",
                    "immediate_routing_key",
                    "fucker!!!!!!!");

            rabbitTemplate.convertAndSend(
                    "immediate_exchange",
                    "immediate_routing_key",
                    new User("zhangsan", "18"));
        }

        /*    *//*计时*//*
        for (int i = 0; i < 10; i++) {
            System.out.println(i+"...");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }*/
    }


    @Test
    public void testPunisherConfirm() {


        rabbitTemplate.convertAndSend("123","123","haha",new CorrelationData("11"));
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm方法被执行了....");
                System.out.println("correlationData---------"+correlationData);

                if (ack) {
                    //接收成功
                    System.out.println("接收成功消息" + cause);
                } else {
                    //接收失败
                    System.out.println("接收失败消息" + cause);
                    //做一些处理，让消息再次发送。
                }
            }
        });
    }



    @Test
    public void testPunisherReturn() {
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message,  int replyCode, String replyText, String exchange, String routingKey) {

                System.out.println("失败啦！！！！！");
                System.out.println(message);
                System.out.println(replyCode);
                System.out.println(replyText);
                System.out.println(exchange);
                System.out.println(routingKey);
            }

        });

        rabbitTemplate.convertAndSend("immediate_exchange","immediate_routing_key1","hahha");
    }



    @Test
    public void testAnnotationOfFront(){

        rabbitTemplate.convertAndSend("listenerAnnotation_exchange","listen.123","congratulations you have succeed!!!");

    }




}