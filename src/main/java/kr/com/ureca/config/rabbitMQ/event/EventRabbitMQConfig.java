package kr.com.ureca.config.rabbitMQ.event;

import kr.com.ureca.config.rabbitMQ.BaseRabbitMQConfig;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class EventRabbitMQConfig extends BaseRabbitMQConfig {

  private static final String EXCHANGE_NAME = "eventExchange";
  private static final int PREFETCH_COUNT = 500; // 메시지를 동시에 처리할 수 있도록 적절히 조정
  private static final int CHANNEL_CACHE_SIZE = 200; // 채널 캐시 크기 조정 (1채널당 1,000~5,000 메시지)

  @Value("${spring.event.rabbitmq.host}")
  private String host;

  @Value("${spring.event.rabbitmq.port}")
  private int port;

  @Value("${spring.event.rabbitmq.username}")
  private String username;

  @Value("${spring.event.rabbitmq.password}")
  private String password;

  @Bean
  public DirectExchange eventExchange() {
    return new DirectExchange(EXCHANGE_NAME);
  }

  @Primary
  @Bean(name = "eventRabbitTemplate")
  public RabbitTemplate rabbitTemplate(
      @Qualifier("eventConnectionFactory") ConnectionFactory connectionFactory) {
    RabbitTemplate template = new RabbitTemplate(connectionFactory);
    template.setMessageConverter(messageConverter());
    return template;
  }

  @Bean(name = "eventRabbitAdmin")
  public RabbitAdmin rabbitAdmin(
      @Qualifier("eventConnectionFactory") ConnectionFactory connectionFactory) {
    return new RabbitAdmin(connectionFactory);
  }

  @Bean
  public SimpleRabbitListenerContainerFactory eventListenerContainerFactory(
      @Qualifier("eventConnectionFactory") ConnectionFactory connectionFactory) {
    SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
    factory.setConnectionFactory(connectionFactory);
    factory.setErrorHandler(new ConditionalRejectingErrorHandler());
    factory.setMessageConverter(messageConverter());
    factory.setPrefetchCount(PREFETCH_COUNT); // 동시에 가져오는 메시지 수
    return factory;
  }

  @Bean(name = "eventConnectionFactory")
  public ConnectionFactory eventConnectionFactory() {
    CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
    connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
    connectionFactory.setPort(port);
    connectionFactory.setUsername(username);
    connectionFactory.setPassword(password);
    connectionFactory.setChannelCacheSize(CHANNEL_CACHE_SIZE); // 성능을 위한 캐시 채널 수
    return connectionFactory;
  }
}
