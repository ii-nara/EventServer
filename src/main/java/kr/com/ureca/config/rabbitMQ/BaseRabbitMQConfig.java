package kr.com.ureca.config.rabbitMQ;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

public abstract class BaseRabbitMQConfig {

  protected Queue createQueue(String queueName, String deadLetterExchange) {
    return QueueBuilder.durable(queueName)
        .withArgument("x-dead-letter-exchange", deadLetterExchange)
        .withArgument("x-max-priority", 10)
        .build();
  }

  @Bean
  public Jackson2JsonMessageConverter messageConverter() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  @Bean
  public Queue deadLetterQueue() {
    return new Queue("deadLetterQueue", true);
  }

  @Bean
  public DirectExchange deadLetterExchange() {
    return new DirectExchange("deadLetterExchange");
  }

  @Bean
  public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
    return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with("deadLetterRoutingKey");
  }
}
