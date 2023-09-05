{% macro amqpConfig(asyncapi, params) %}

import {{params['userJavaPackage']}}.service.MessageHandlerService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Value("${amqp.broker.host}")
    private String host;

    @Value("${amqp.broker.port}")
    private int port;

    @Value("${amqp.broker.username}")
    private String username;

    @Value("${amqp.broker.password}")
    private String password;


    {% for channelName, channel in asyncapi.channels() %}
    {% if channel.hasSubscribe() %}
    @Value("${amqp.{{- channelName -}}.exchange}")
    private String {{channelName}}Exchange;

    @Value("${amqp.{{- channelName -}}.routingKey}")
    private String {{channelName}}RoutingKey;
    {% endif %}

    {% if channel.hasPublish() %}
    @Value("${amqp.{{- channelName -}}.queue}")
    private String {{channelName}}Queue;
    {% endif %}

    {% endfor %}

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPort(port);
        return connectionFactory;
    }

    @Bean
    public Declarables declarables() {
        return new Declarables(
        {% for channelName, channel in asyncapi.channels() %}
        {% if channel.hasSubscribe() %} new TopicExchange({{channelName}}Exchange, true, false)
        {% if not loop.last %},{% endif %}{% endif %}{% endfor %}

        {% for channelName, channel in asyncapi.channels() %}
        {% if channel.hasPublish() %}{% if loop.first %},{% endif %} new Queue({{channelName}}Queue, true, false, false)
        {% if not loop.last %},{% endif %}{% endif %} {% endfor %}
        );
    }


    @Bean
    public MessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public AmqpTemplate template() {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
}
{% endmacro %}
