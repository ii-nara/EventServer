package kr.com.ureca.config.rabbitMQ.event;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventMQ extends EventRabbitMQConfig {

  private static final String QUEUE_NAME = "eventQueue";
  private static final String ROUTING_KEY = "eventRoutingKey";
  private static final String DEAD_LETTER_EXCHANGE = "deadLetterExchange";

  @Bean
  public Queue EventQueue() {
    return this.createQueue(QUEUE_NAME, DEAD_LETTER_EXCHANGE);
  }

  @Bean
  public Binding EventBinding(Queue EventQueue, DirectExchange eventExchange) {
    return BindingBuilder.bind(EventQueue).to(eventExchange).with(ROUTING_KEY);
  }

  @Bean
  public CommandLineRunner initializeEventQueue(
      @Qualifier("eventRabbitAdmin") RabbitAdmin rabbitAdmin) {
    return args -> {
      rabbitAdmin.declareQueue(EventQueue());
      rabbitAdmin.declareExchange(this.eventExchange());
      rabbitAdmin.declareBinding(EventBinding(EventQueue(), this.eventExchange()));
    };
  }
}
