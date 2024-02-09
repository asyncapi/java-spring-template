{% macro kafkaPublisherImpl(asyncapi, params) %}

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
        {%- for message in channel.subscribe().messages() %}
import {{params['userJavaPackage']}}.model.{{message.payload().uid() | camelCase | upperFirst}};
        {%- endfor -%}
    {% endif -%}
{% endfor %}
import javax.annotation.processing.Generated;
import java.util.HashMap;
import java.util.Map;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    private KafkaTemplate<Integer, Object> kafkaTemplate;
{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
        {%- set hasParameters = channel.hasParameters() %}
        {%- set methodName = channel.subscribe().id() | camelCase %}
        {%- if channel.subscribe().hasMultipleMessages() %}
            {%- set varName = "object" %}
        {%- else %}
            {%- set varName = channel.subscribe().message().payload().uid() | camelCase %}
        {%- endif %}
    {% if channel.description() or channel.subscribe().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.subscribe().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    public void {{methodName}}(Integer key, {{varName | upperFirst}} {{varName}}{% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}, {% if parameter.schema().type() === 'object'%}{{parameterName | camelCase | upperFirst}}{% else %}{{parameter.schema().type() | toJavaType(false)}}{% endif %} {{parameterName | camelCase}}{% endfor %}{% endif %}) {
        Message<{{varName | upperFirst}}> message = MessageBuilder.withPayload({{varName}})
                .setHeader(KafkaHeaders.TOPIC, get{{methodName | upperFirst-}}Topic({% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}{{parameterName | camelCase}}{% if not loop.last %}, {% endif %}{% endfor %}{% endif %}))
                .setHeader(KafkaHeaders.{%- if params.springBoot2 %}MESSAGE_KEY{% else %}KEY{% endif -%}, key)
                .build();
        kafkaTemplate.send(message);
    }

    private String get{{methodName | upperFirst-}}Topic({% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}{% if parameter.schema().type() === 'object'%}{{parameterName | camelCase | upperFirst}}{% else %}{{parameter.schema().type() | toJavaType(false)}}{% endif %} {{parameterName | camelCase}}{% if not loop.last %}, {% endif %}{% endfor %}{% endif %}) {
        Map<String, String> parameters = {% if hasParameters %}new HashMap<>(){% else %}null{% endif %};
        {%- if hasParameters %}
            {%- for parameterName, parameter in channel.parameters() %}
        parameters.put("{{parameterName}}", {{parameterName | camelCase}}{% if parameter.schema().type() !== 'string'%}.toString(){% endif %});
            {%- endfor %}
        {%- endif %}
        return replaceParameters("{{channelName}}", parameters);
    }
    {%- endif %}
{%- endfor %}
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