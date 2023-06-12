{% macro amqpPublisher(asyncapi, params) %}

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
        {%- for message in channel.subscribe().messages() %}
import {{params['userJavaPackage']}}.model.{{message.payload().uid() | camelCase | upperFirst}};
        {%- endfor -%}
    {% endif -%}
{% endfor %}


@Service
public class PublisherService {
    @Autowired
    private RabbitTemplate template;

    {% for channelName, channel in asyncapi.channels() %}
    @Value("${amqp.{{- channelName -}}.exchange}")
    private String {{channelName}}Exchange;

    @Value("${amqp.{{- channelName -}}.routingKey}")
    private String {{channelName}}RoutingKey;
    {% endfor %}

    {% for channelName, channel in asyncapi.channels() %}
    {%- set schemaName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %}
    public void {{channel.subscribe().id() | camelCase}}(){
        {{schemaName}} {{channelName}}Payload = new {{schemaName}}();
        template.convertAndSend({{channelName}}Exchange, {{channelName}}RoutingKey,  {{channelName}}Payload);
    }
    {% endfor %}
}

{% endmacro %}