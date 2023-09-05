package {{ params['userJavaPackage'] }}.service;
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
            {%- for message in channel.publish().messages() %}
import {{ params['userJavaPackage'] }}.model.{{message.payload().uid() | camelCase | upperFirst}};
            {%- endfor %}
        {%- endif %}
    {%- endfor %}
{% endif %}
{% if asyncapi | isProtocol('amqp') and hasPublish %}
import org.springframework.amqp.rabbit.annotation.RabbitListener;
    {% for channelName, channel in asyncapi.channels() %}
            {%- if channel.hasPublish() %}
            {%- for message in channel.publish().messages() %}
import {{ params['userJavaPackage'] }}.model.{{message.payload().uid() | camelCase | upperFirst}};
        {%- endfor %}
        {%- endif %}
        {%- endfor %}
        {% endif %}
@Service
public class MessageHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerService.class);
{% if asyncapi | isProtocol('kafka') %}
    {% for channelName, channel in asyncapi.channels() %}
        {%- if channel.hasPublish() %}
            {%- if channel.publish().hasMultipleMessages() %}
                {%- set typeName = "Object" %}
            {%- else %}
                {%- set typeName = channel.publish().message().payload().uid() | camelCase | upperFirst %}
            {%- endif %}
    {% if channel.description() or channel.publish().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.publish().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    @KafkaListener(topics = "{{channelName}}"{% if channel.publish().binding('kafka') %}, groupId = "{{channel.publish().binding('kafka').groupId}}"{% endif %})
    public void {{channel.publish().id() | camelCase}}(@Payload {{typeName}} payload,
                       @Header(KafkaHeaders.{%- if params.springBoot2 %}RECEIVED_MESSAGE_KEY{% else %}RECEIVED_KEY{% endif -%}) Integer key,
                       @Header(KafkaHeaders.{%- if params.springBoot2 %}RECEIVED_PARTITION_ID{% else %}RECEIVED_PARTITION{% endif -%}) int partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        LOGGER.info("Key: " + key + ", Payload: " + payload.toString() + ", Timestamp: " + timestamp + ", Partition: " + partition);
    }
        {%- endif %}
    {% endfor %}

    {% elif asyncapi | isProtocol('amqp')  %}
    {% for channelName, channel in asyncapi.channels() %}
    {%- set schemaName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %}
    @RabbitListener(queues = "${amqp.{{- channelName -}}.queue}")
    public void {{channel.publish().id() | camelCase}}({{schemaName}} {{channelName}}Payload ){
        LOGGER.info("Message received from {{- schemaName -}} : " + {{channelName}}Payload);
    }
    {% endfor %}

{% else %}
    {% for channelName, channel in asyncapi.channels() %}
      {% if channel.hasPublish() %}
    {% if channel.description() or channel.publish().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.publish().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    public void handle{{channel.publish().id() | camelCase | upperFirst}}(Message<?> message) {
        LOGGER.info("handler {{channelName}}");
        LOGGER.info(String.valueOf(message.getPayload().toString()));
    }
      {% endif %}
    {% endfor %}
{% endif %}
}
