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
    @Value("${amqp.{{- channelName -}}.exchange}")
    private String {{channelName}}Exchange;

    @Value("${amqp.{{- channelName -}}.queue}")
    private String {{channelName}}Queue;

    @Value("${amqp.{{- channelName -}}.routingKey}")
    private String {{channelName}}RoutingKey;
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
        {% for channelName, channel in asyncapi.channels() %}
        Queue {{channelName}}_Queue = new Queue({{channelName}}Queue);
        {% endfor %}

        {% for channelName, channel in asyncapi.channels() %}
        TopicExchange {{channelName}}_Exchange = new TopicExchange({{channelName}}Exchange);
        {% endfor %}

        {% for channelName, channel in asyncapi.channels() %}
        Binding {{channelName}}_Binding = BindingBuilder.bind({{channelName}}_Queue)
                .to({{channelName}}_Exchange).with({{channelName}}RoutingKey);
        {% endfor %}

        return new Declarables(
                {% set i = 1 %}{% for channelName, channel in asyncapi.channels() %}{% if i == asyncapi.channels() | size %}
                    {{channelName}}_Queue,
                    {{channelName}}_Exchange,
                    {{channelName}}_Binding
                {% else %}
                    {{channelName}}_Queue,
                    {{channelName}}_Exchange,
                    {{channelName}}_Binding,
                {% set i = i+1 %} {% endif %} {% endfor %}
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
