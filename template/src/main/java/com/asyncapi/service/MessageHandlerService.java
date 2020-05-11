package com.asyncapi.service;
{%- set hasSubscribe = false -%}
{%- set hasPublish = false -%}
{%- for channelName, channel in asyncapi.channels() -%}
    {%- if channel.hasPublish() -%}
        {%- set hasPublish = true -%}
    {%- endif -%}
    {%- if channel.hasSubscribe() -%}
        {%- set hasSubscribe = true -%}
    {%- endif -%}
{%- endfor %}

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
{% if asyncapi | isProtocol('kafka') and hasPublish %}
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
    {% for channelName, channel in asyncapi.channels() %}
        {%- if channel.hasPublish() %}
import com.asyncapi.model.{{channel.publish().message().payload().uid() | camelCase | upperFirst}};
        {% endif -%}
    {% endfor -%}
{% endif %}
@Service
public class MessageHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerService.class);
{% if asyncapi | isProtocol('kafka') %}
    {% for channelName, channel in asyncapi.channels() %}
        {%- if channel.hasPublish() %}

    {% if channel.description() or channel.publish().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.publish().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    @KafkaListener(topics = "{{channelName}}"{% if channel.publish().binding('kafka') %}, groupId = "{{channel.publish().binding('kafka').groupId}}"{% endif %})
    public void {{channel.publish().id() | camelCase}}(@Payload {{channel.publish().message().payload().uid() | camelCase | upperFirst}} payload,
                       @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) Integer key,
                       @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        LOGGER.info("Key: " + key + ", Payload: " + payload + ", Timestamp: " + timestamp + ", Partition: " + partition);
    }
        {%- endif %}
    {% endfor %}
{% else %}
    {% for channelName, channel in asyncapi.channels() %}
      {% if channel.hasPublish() %}
    {% if channel.description() or channel.publish().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.publish().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    public void handle{{channelName | upperFirst}}(Message<?> message) {
        LOGGER.info("handler {{channelName}}");
        LOGGER.info(message.getPayload());
    }
      {% endif %}
    {% endfor %}
{% endif %}
}
