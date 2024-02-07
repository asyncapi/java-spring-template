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
{%- if asyncapi | isProtocol('ws') and hasPublish %}
import org.springframework.messaging.handler.annotation.MessageMapping;
{%- endif %}
{%- if asyncapi | isProtocol('kafka') and hasPublish %}
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
{%- endif %}
{%- if asyncapi | isProtocol('mqtt') and hasPublish %}
import org.springframework.integration.mqtt.support.MqttHeaders;
{%- endif %}
{%- if asyncapi | isProtocol('amqp') and hasPublish %}
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
{%- endif %}
{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasPublish() %}
        {%- for message in channel.publish().messages() %}
import {{ params['userJavaPackage'] }}.model.{{message.payload().uid() | camelCase | upperFirst}};
        {%- endfor %}
    {%- endif %}
{% endfor %}
import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.List;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Service
public class MessageHandlerService {

{%- set anyChannelHasParameter = false %}
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerService.class);

{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasPublish() %}
        {%- set hasParameters = channel.hasParameters() %}
        {%- set anyChannelHasParameter = anyChannelHasParameter or hasParameters %}
        {%- set methodName = channel.publish().id() | camelCase%}
        {%- if channel.publish().hasMultipleMessages() %}
            {%- set typeName = "Object" %}
        {%- else %}
            {%- set typeName = channel.publish().message().payload().uid() | camelCase | upperFirst %}
        {%- endif %}
        {% set javaDoc = '' %}
        {% if channel.description() or channel.publish().description() %}
            {%- set javaDoc = javaDoc + '/**\n' %}
            {%- for line in channel.description() | splitByLines %}
            {%- set javaDoc = javaDoc + '    * ' + (line | safe) %}
            {%- set javaDoc = javaDoc + '\n' %}
            {%- endfor %}
            {%- for line in channel.publish().description() | splitByLines %}
            {%- set javaDoc = javaDoc + '    * ' + (line | safe) %}
            {%- set javaDoc = javaDoc + '\n' %}
            {%- endfor %}
            {%- set javaDoc = javaDoc + '    */' %}
        {% endif %}
    {%- if asyncapi | isProtocol('kafka') %}
        {%- set route = channelName | toKafkaTopicString(channel.hasParameters(), channel.parameters()) | safe %}
    {{javaDoc}}
    @KafkaListener({% if hasParameters %}topicPattern{% else %}topics{% endif %} = "{{route}}"{% if channel.publish().binding('kafka') %}, groupId = "{{channel.publish().binding('kafka').groupId}}"{% endif %})
    public void {{methodName}}(@Payload {{typeName}} payload,
                       @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                       @Header(KafkaHeaders.{%- if params.springBoot2 %}RECEIVED_MESSAGE_KEY{% else %}RECEIVED_KEY{% endif -%}) Integer key,
                       @Header(KafkaHeaders.{%- if params.springBoot2 %}RECEIVED_PARTITION_ID{% else %}RECEIVED_PARTITION{% endif -%}) int partition,
                       @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long timestamp) {
        LOGGER.info("Key: " + key + ", Payload: " + payload.toString() + ", Timestamp: " + timestamp + ", Partition: " + partition);
        {%- if hasParameters %}
        List<String> parameters = decompileTopic("{{route}}", topic);
        {{methodName}}({%- for parameterName, parameter in channel.parameters() %}parameters.get({{loop.index0}}), {% endfor %}payload, topic, key, partition, timestamp);
        {%- endif %}
    }
    {%- if hasParameters %}
    {{javaDoc}}
    public void {{methodName}}({%- for parameterName, parameter in channel.parameters() %}String {{parameterName}}, {% endfor %}{{typeName}} payload,
                       String topic, Integer key, int partition, long timestamp) {
        // parametrized listener
    }
    {%- endif %}
    {% elif asyncapi | isProtocol('amqp') %}
        {%- set propertyValueName = channelName | toAmqpNeutral(hasParameters, channel.parameters()) %}
        {%- if hasParameters %}
    @Value("${amqp.{{- propertyValueName -}}.routingKey}")
    private String {{propertyValueName}}RoutingKey;
        {% endif %}
    {{javaDoc}}
    @RabbitListener(queues = "${amqp.{{- propertyValueName -}}.queue}")
    public void {{methodName}}({{typeName}} payload, @Header(AmqpHeaders.RECEIVED_ROUTING_KEY) String routKey) {
        {%- if hasParameters %}
        List<String> parameters = decompileRoutingKey({{propertyValueName}}RoutingKey, routKey);
        {{methodName}}({%- for parameterName, parameter in channel.parameters() %}parameters.get({{loop.index0}}), {% endfor %}payload);
        {% endif %}
        LOGGER.info("Message received from {{- propertyValueName -}} : " + payload);
    }
        {% if hasParameters %}
    {{javaDoc}}
    public void {{methodName}}({%- for parameterName, parameter in channel.parameters() %}String {{parameterName}}, {% endfor %}{{typeName}} payload) {
        // parametrized listener
    }
        {%- endif %}
    {% elif asyncapi | isProtocol('ws') %}
    @MessageMapping("/{{channelName}}")
    public void handle{{methodName | upperFirst}}({{typeName}} payload) {
        LOGGER.info("Message received from {{- channelName -}} : " + payload);
    }
    {%- else %}
        {%- if hasParameters %}
    @Value("${mqtt.topic.{{-methodName-}}}")
    private String {{methodName}}Topic;

        {%- endif %}
    {{javaDoc}}
    public void handle{{methodName | upperFirst}}(Message<?> message) {
        String topic = message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC).toString();
        {%- if hasParameters %}
        List<String> parameters = decodeTopic({{methodName}}Topic, topic);
        {%- endif %}
        {{methodName}}({%- for parameterName, parameter in channel.parameters() %}parameters.get({{loop.index0}}), {% endfor %}({{typeName}}) message.getPayload());
    }
    {{javaDoc}}
    public void {{methodName}}({%- for parameterName, parameter in channel.parameters() %}String {{parameterName}}, {% endfor %}{{typeName}} payload) {
        LOGGER.info("handler {{channelName}}");
        LOGGER.info(String.valueOf(payload.toString()));
    }
    {% endif %}
{% endif %}
{% endfor %}

{%- if anyChannelHasParameter %}
    {%- if asyncapi | isProtocol('kafka') %}
    private List<String> decompileTopic(String topicPattern, String topic) {
        topicPattern = topicPattern.replaceAll("\\.\\*", "(.*)");
        List<String> parameters = new ArrayList<>();
        Pattern pattern  = Pattern.compile(topicPattern);
        Matcher matcher = pattern.matcher(topic);
        if (matcher.find()) {
            for (int i = 0; i < matcher.groupCount(); i++) {
                parameters.add(matcher.group(i + 1));
            }
        }
        return parameters;
    }
    {%- elif asyncapi | isProtocol('amqp') %}
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
    {%- else %}
    private List<String> decodeTopic(String topicPattern, String topic) {
        List<String> parameters = new ArrayList<>();
        int topicPossition = 0;
        int patternPosition = 0;
        while (topicPossition < topic.length()) {
            while (topicPattern.charAt(patternPosition) == topic.charAt(topicPossition)) {
                topicPossition++;
                patternPosition++;
            }
            topicPossition++;
            patternPosition += 2; // skip +
            StringBuilder parameter = new StringBuilder();
            while (topicPattern.charAt(patternPosition) != topic.charAt(topicPossition)) {
                parameter.append(topic.charAt(topicPossition));
                topicPossition++;
            }
            parameters.add(parameter.toString());
        }
        return parameters;
    }
    {%- endif %}
{% endif %}
}
