package com.itheima.consumer;

import com.itheima.domain.User;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RabbitListener(queues = "delay_queue")  //immediate_queue  delay_queue
public class DeadLetterConsumer {



    /*注解创建绑定交换机队列 (必须在发送方运行前 运行程序)*/
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "listenerAnnotation_queue",
                    durable="true"),
            exchange = @Exchange(value = "listenerAnnotation_exchange",
                    durable="true",
                    type= "topic",
                    ignoreDeclarationExceptions = "true"),
            key = "listen.#"
    ))
 public void frontBindLitener(Message message,Channel channel,@Payload String body){
     long deliveryTag = message.getMessageProperties().getDeliveryTag();

     try {
         System.out.println("获得的消息" + body);

         channel.basicAck(deliveryTag, true);
     } catch (Exception e) {
         e.printStackTrace();
         try {
             channel.basicNack(deliveryTag,true,true);
         } catch (IOException e1) {
             e1.printStackTrace();
         }
     }
 }




  @RabbitHandler
  public void getUser(User user,Channel channel,Message message){

      long deliveryTag = message.getMessageProperties().getDeliveryTag();
      try {
          System.out.println("我的奴才----" + user);
         // int i=1/0;
          channel.basicAck(deliveryTag, true);//如果没有 签收注释掉 获得消息后 消息会残留在队列中
      }catch (Exception e){
          try {
              channel.basicNack(deliveryTag,true,true);
          } catch (IOException e1) {
              e1.printStackTrace();
          }
      }
  }


    @RabbitHandler
    public void processMessage1(@Payload String body, @Headers Map<String, Object> headers, Channel channel, Message message) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        System.out.println("body：" + body);
        System.out.println("Headers：-------------------------" + headers);
        System.out.println("----------------------------------");
        channel.basicAck(deliveryTag, true);

    }
}
