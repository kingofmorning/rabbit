package com.itheima.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * dlx+tll实现延迟队列
 */
@Configuration
public class DeadDelayMQConfig {


    /*普通队列*/
    @Bean
    public Queue immediateQueue() {

        /*通过 map携带args*/
        Map<String, Object> args = new HashMap<>();

        /*绑定 死信交换机*/
        args.put("x-dead-letter-exchange", "dead_letter_exchange");

        /*绑定 向死信队列发送routing——key*/
        args.put("x-dead-letter-routing-key", "delay_routing_key");

        /*设置队列最大长度*/
        args.put("x-max-length",5);
        /*设置ttl*/
        /*args.put("x-message-ttl",1000*10);*/
        return new Queue("immediate_queue", true,false,false,args);
    }


/*死信队列 （推迟队列）*/
    @Bean
    public Queue delayQueue() {
        return new Queue("delay_queue", true, false, false);
    }

/*普通交换机*/
    @Bean
    public DirectExchange immediateExchange() {

        return new DirectExchange("immediate_exchange", true, false);
    }


    /*死信交换机*/
    @Bean
    public DirectExchange deadLetterExchange() {

        return new DirectExchange("dead_letter_exchange", true, false);
    }


    /*交换机队列绑定*/
    @Bean
    public Binding immediateBinding() {
        return BindingBuilder
                .bind(immediateQueue())
                .to(immediateExchange())
                .with("immediate_routing_key");
    }


    /*交换机队列绑定*/
    @Bean
    public Binding delayBinding() {
        return BindingBuilder
                .bind(delayQueue())
                .to(deadLetterExchange())
                .with("delay_routing_key");
    }
}
