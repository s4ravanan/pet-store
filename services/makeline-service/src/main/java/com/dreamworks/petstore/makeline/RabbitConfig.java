package com.dreamworks.petstore.makeline;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    @Bean
    DirectExchange exchange() {
        return new DirectExchange("petstore.orders");
    }

    @Bean
    Queue queue() {
        return QueueBuilder.durable("petstore.makeline").build();
    }

    @Bean
    Binding binding(Queue q, DirectExchange e) {
        return BindingBuilder.bind(q).to(e).with("order.created");
    }
}
