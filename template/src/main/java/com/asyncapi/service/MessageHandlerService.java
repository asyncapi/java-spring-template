package com.asyncapi.service;
{%- set hasPublish = false -%}
{%- set hasSubscribe = false -%}
{%- for channelName, channel in asyncapi.channels() -%}
    {%- if channel.hasSubscribe() -%}
        {%- set hasSubscribe = true -%}
    {%- endif -%}
    {%- if channel.hasPublish() -%}
        {%- set hasPublish = true -%}
    {%- endif -%}
{%- endfor %}

import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
{% if asyncapi | isProtocol('kafka') and hasSubscribe %}
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
    {% for channelName, channel in asyncapi.channels() %}
        {%- if channel.hasSubscribe() %}
import com.asyncapi.model.{{channel.subscribe().message().payload().uid() | camelCase | upperFirst}};
        {% endif -%}
    {% endfor -%}
{% endif %}
@Service
public class MessageHandlerService {
{% if asyncapi | isProtocol('kafka') %}
    {% for channelName, channel in asyncapi.channels() %}
        {%- if channel.hasSubscribe() %}
    @KafkaListener(topics = "{{channelName}}"{% if channel.subscribe().binding('kafka') %}, groupId = "{{channel.subscribe().binding('kafka').groupId}}"{% endif %})
    public void {{channel.subscribe().id() | camelCase}}(@Payload {{channel.subscribe().message().payload().uid() | camelCase | upperFirst}} payload,
                       @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        //
    }
        {%- endif %}
    {% endfor %}
{% else %}
    {% for channelName, channel in asyncapi.channels() %}
      {% if channel.hasSubscribe() %}
    public void handle{{channelName | upperFirst}}(Message<?> message) {
        System.out.println("handler {{channelName}}");
        System.out.println(message.getPayload());
    }
      {% endif %}
    {% endfor %}
{% endif %}
}
