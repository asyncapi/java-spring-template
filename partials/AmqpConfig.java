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

import javax.annotation.processing.Generated;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
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
    {% set varName = channelName | toAmqpNeutral(channel.hasParameters(), channel.parameters()) %}
    {% if channel.binding('amqp') and channel.binding('amqp').exchange %}
    @Value("${amqp.{{- varName -}}.exchange}")
    private String {{varName}}Exchange;
    {% endif %}

    @Value("${amqp.{{- varName -}}.routingKey}")
    private String {{varName}}RoutingKey;

    {% if channel.binding('amqp') and channel.binding('amqp').queue %}
    @Value("${amqp.{{- varName -}}.queue}")
    private String {{varName}}Queue;
    {% endif %}

    {% set name = varName | camelCase %}
    {% if channel.binding('amqp') and channel.binding('amqp').exchange %}
    {% if channel.binding('amqp').exchange.type and channel.binding('amqp').exchange.type !== 'default' %}{% set type = channel.binding('amqp').exchange.type | camelCase %}{% else %}{% set type = 'Topic' %}{% endif %}
    {% set type = type + 'Exchange' %}
    @Bean
    public {{type}} {{name}}Exchange() {
        return new {{type}}({{varName}}Exchange, {% if channel.binding('amqp').exchange.durable %}{{channel.binding('amqp').exchange.durable}}{% else %}true{% endif%}, {% if channel.binding('amqp').exchange.exclusive %}{{channel.binding('amqp').exchange.exclusive}}{% else %}false{% endif%});
    }

   {% if channel.binding('amqp') and channel.binding('amqp').queue %}
    @Bean
    public Binding binding{{name | upperFirst}}({{type}} {{name}}Exchange, Queue {{name}}Queue) {
        return BindingBuilder.bind({{name}}Queue).to({{name}}Exchange)
        {% if channel.binding('amqp').exchange.type !== 'fanout' %}.with({{varName}}RoutingKey)}{% endif %};
    }
{% endif %}
    {% if channel.binding('amqp') and channel.binding('amqp').queue %}
    @Bean
    public Queue {{name}}Queue() {
        return new Queue({{varName}}Queue, {% if channel.binding('amqp').queue.durable %}{{channel.binding('amqp').queue.durable}}{% else %}true{% endif%}, {% if channel.binding('amqp').queue.exclusive %}{{channel.binding('amqp').queue.exclusive}}{% else %}false{% endif%}, {% if channel.binding('amqp').queue.autoDelete %}{{channel.binding('amqp').queue.autoDelete}}{% else %}false{% endif%});
    }
    {% endif %}
    {%- endfor %}


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setPort(port);
        return connectionFactory;
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
