{% macro amqpPublisherImpl(asyncapi, params) %}

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

import javax.annotation.processing.Generated;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Service
public class PublisherServiceImpl implements PublisherService {
    @Autowired
    private RabbitTemplate template;

    {% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
    {%- set varName = channelName | toAmqpNeutral(channel.hasParameters(), channel.parameters()) %}
    @Value("${amqp.{{- varName -}}.exchange}")
    private String {{varName}}Exchange;
    @Value("${amqp.{{- varName -}}.routingKey}")
    private String {{varName}}RoutingKey;
    {%- endif %}
    {% endfor %}

    {% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
        {%- if channel.subscribe().hasMultipleMessages() %}
            {%- set varName = "object" %}
        {%- else %}
            {%- set varName = channel.subscribe().message().payload().uid() | camelCase %}
        {%- endif %}
    {%- set channelVariable = channelName | toAmqpNeutral(channel.hasParameters(), channel.parameters()) %}
    {% if channel.description() or channel.subscribe().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.subscribe().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    public void {{channel.subscribe().id() | camelCase}}({{varName | upperFirst}} payload){
        template.convertAndSend({{channelVariable}}Exchange, {{channelVariable}}RoutingKey,  payload);
    }

    {% endif %}
    {% endfor %}

}

{% endmacro %}