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
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
    {% for channelName, channel in asyncapi.channels() %}
            {%- if channel.hasPublish() %}
            {%- for message in channel.publish().messages() %}
import {{ params['userJavaPackage'] }}.model.{{message.payload().uid() | camelCase | upperFirst}};
        {%- endfor %}
        {%- endif %}
        {%- endfor %}
        {% endif %}
import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
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
{%- set route = channelName %}
{%- if channel.hasParameters() %}
    {%- set route = route | replaceAll(".", "\\.") %}
    {%- for parameterName, parameter in channel.parameters() %}
        {%- set route = route | replace("{" + parameterName + "}", ".*") %}
    {%- endfor %}
{%- endif %}
    @KafkaListener({% if channel.hasParameters() %}topicPattern{% else %}topics{% endif %} = "{{route}}"{% if channel.publish().binding('kafka') %}, groupId = "{{channel.publish().binding('kafka').groupId}}"{% endif %})
    public void {{channel.publish().id() | camelCase}}(@Payload {{typeName}} payload,
                       @Header(KafkaHeaders.{%- if params.springBoot2 %}RECEIVED_MESSAGE_KEY{% else %}RECEIVED_KEY{% endif -%}) Integer key,
                       @Header(KafkaHeaders.{%- if params.springBoot2 %}RECEIVED_PARTITION_ID{% else %}RECEIVED_PARTITION{% endif -%}) int partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        LOGGER.info("Key: " + key + ", Payload: " + payload.toString() + ", Timestamp: " + timestamp + ", Partition: " + partition);
    }
        {%- endif %}
    {% endfor %}

    {% elif asyncapi | isProtocol('amqp')  %}
    {%- set anyChannelHasParameter = false %}
    {% for channelName, channel in asyncapi.channels() %}
    {% if channel.hasPublish() %}
    {%- set anyChannelHasParameter = anyChannelHasParameter or channel.hasParameters() %}
    {%- set schemaName = channel.publish().message().payload().uid() | camelCase | upperFirst %}
    {%- set varName = channelName | toAmqpNeutral(channel.hasParameters(), channel.parameters()) %}
    {%- if channel.hasParameters() %}
    @Value("${amqp.{{- varName -}}.routingKey}")
    private String {{varName}}RoutingKey;

    {%- endif %}
    @RabbitListener(queues = "${amqp.{{- varName -}}.queue}")
    public void {{channel.publish().id() | camelCase}}({{schemaName}} payload, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routKey) {
        {%- if channel.hasParameters() %}
        List<String> parameters = decompileRoutingKey({{varName}}RoutingKey, routKey);
        {{channel.publish().id() | camelCase}}({%- for parameterName, parameter in channel.parameters() %}parameters.get({{loop.index0}}), {%- endfor %}payload);
        {% endif %}
        LOGGER.info("Message received from {{- varName -}} : " + payload);
    }
    {%- if channel.hasParameters() %}
    public void {{channel.publish().id() | camelCase}}({%- for parameterName, parameter in channel.parameters() %}String {{parameterName}}, {%- endfor %}{{schemaName}} payload) {
        // parametrized listener
    }
    {% endif %}
    {% endif %}
    {% endfor %}
    {%- if anyChannelHasParameter %}
    private List<String> decompileRoutingKey(String pattern, String routKey) {
        List<String> parameters = new ArrayList<>();
        int routeKeyPossition = 0;
        int patternPosition = 0;
        while (routeKeyPossition < routKey.length()) {
            while (pattern.charAt(patternPosition) == routKey.charAt(routeKeyPossition)) {
                routeKeyPossition++;
                patternPosition++;
            }
            routeKeyPossition++;
            patternPosition += 2; // skip .*
            StringBuilder parameter = new StringBuilder();
            while (pattern.charAt(patternPosition) != routKey.charAt(routeKeyPossition)) {
                parameter.append(routKey.charAt(routeKeyPossition));
                routeKeyPossition++;
            }
            parameters.add(parameter.toString());
        }
        return parameters;
    }
    {%- endif %}
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
