{% macro amqpListener(asyncapi, params) %}

import com.javatechie.rabbitmq.demo.model.LightMeasuredPayload;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
{% for channelName, channel in asyncapi.channels() %}
        {%- if channel.hasSubscribe() %}
        {%- for message in channel.subscribe().messages() %}
import {{params['userJavaPackage']}}.model.{{message.payload().uid() | camelCase | upperFirst}};
        {%- endfor -%}
        {% endif -%}
        {% endfor %}


@Service
public class  MessageHandlerService{

    {% for channelName, channel in asyncapi.channels() %}
    {%- set schemaName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %}
    @RabbitListener(queues = "${amqp.broker.{{- channeName -}}.queue}")
    public void consumeFrom{{schemaName}}({{schemaName}} {{channelName}}Payload ){
        LOGGER.info("Message received from "+ {{schemaName}} " : " + lightMeasuredPayload);
    }
    {% endfor %}
}

{% endmacro %}
