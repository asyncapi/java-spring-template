{% macro commonPublisherImpl(asyncapi, params) %}

{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
        {%- for message in channel.subscribe().messages() %}
import {{params['userJavaPackage']}}.model.{{message.payload().uid() | camelCase | upperFirst}};
        {%- endfor -%}
    {% endif -%}
    {%- if channel.hasParameters() %}
        {%- for parameterName, parameter in channel.parameters() %}
            {%- if parameter.schema().type() === 'object' %}
import {{params['userJavaPackage']}}.model.{{parameterName | camelCase | upperFirst}};
            {%- endif %}
        {%- endfor -%}
    {% endif -%}
{% endfor %}

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Service;

import javax.annotation.processing.Generated;
import java.util.HashMap;
import java.util.Map;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Service
public class PublisherServiceImpl implements PublisherService {

    {%- for channelName, channel in asyncapi.channels() %}
        {%- if channel.hasSubscribe() %}
    @Value("${mqtt.topic.{{-channel.subscribe().id() | camelCase-}}}")
    private String {{channel.subscribe().id() | camelCase-}}Topic;

    @Autowired
    private MessageHandler {{channel.subscribe().id() | camelCase}}Outbound;

        {%- endif %}
    {%- endfor %}
    {% for channelName, channel in asyncapi.channels() %}
        {% if channel.hasSubscribe() %}
            {%- if channel.subscribe().hasMultipleMessages() %}
                {%- set varName = "object" %}
            {%- else %}
                {%- set varName = channel.subscribe().message().payload().uid() | camelCase %}
            {%- endif %}
    {% if channel.description() or channel.subscribe().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.subscribe().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}{% set hasParameters = channel.hasParameters() %}
    public void {{channel.subscribe().id() | camelCase}}({{varName | upperFirst}} {{varName}}{% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}, {% if parameter.schema().type() === 'object'%}{{parameterName | camelCase | upperFirst}}{% else %}{{parameter.schema().type() | toJavaType(false)}}{% endif %} {{parameterName | camelCase}}{% endfor %}{% endif %}) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(MqttHeaders.TOPIC, get{{channel.subscribe().id() | camelCase-}}Topic({% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}{{parameterName | camelCase}}{% if not loop.last %}, {% endif %}{% endfor %}{% endif %}));
        Message<{{varName | upperFirst}}> message = new GenericMessage<>({{varName}}, headers);
        {{channel.subscribe().id() | camelCase}}Outbound.handleMessage(message);
    }

    private String get{{channel.subscribe().id() | camelCase-}}Topic({% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}{% if parameter.schema().type() === 'object'%}{{parameterName | camelCase | upperFirst}}{% else %}{{parameter.schema().type() | toJavaType(false)}}{% endif %} {{parameterName | camelCase}}{% if not loop.last %}, {% endif %}{% endfor %}{% endif %}) {
        Map<String, String> parameters = {% if hasParameters %}new HashMap<>(){% else %}null{% endif %};
        {%- if hasParameters %}
            {%- for parameterName, parameter in channel.parameters() %}
        parameters.put("{{parameterName}}", {{parameterName | camelCase}}{% if parameter.schema().type() === 'object'%}.toString(){% endif %});
            {%- endfor %}
        {%- endif %}
        return replaceParameters({{channel.subscribe().id() | camelCase-}}Topic, parameters);
    }
        {% endif %}
    {% endfor %}

    private String replaceParameters(String topic, Map<String, String> parameters) {
        if (parameters != null) {
            String compiledTopic = topic;
            for (String key : parameters.keySet()) {
                compiledTopic = compiledTopic.replace("{" + key + "}", parameters.get(key));
            }
            return compiledTopic;
        }
        return topic;
    }
}
{% endmacro %}