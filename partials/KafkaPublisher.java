{% macro kafkaPublisher(asyncapi, params) %}

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

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Service
public class PublisherService {

    @Autowired
    private KafkaTemplate<Integer, Object> kafkaTemplate;
{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
        {%- if channel.subscribe().hasMultipleMessages() %}
            {%- set varName = "object" %}
        {%- else %}
            {%- set varName = channel.subscribe().message().payload().uid() | camelCase %}
        {%- endif %}
    {% if channel.description() or channel.subscribe().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.subscribe().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    public void {{channel.subscribe().id() | camelCase}}(Integer key, {{varName | upperFirst}} {{varName}}) {
        Message<{{varName | upperFirst}}> message = MessageBuilder.withPayload({{varName}})
                .setHeader(KafkaHeaders.TOPIC, "{{channelName}}")
                .setHeader(KafkaHeaders.MESSAGE_KEY, key)
                .build();
        kafkaTemplate.send(message);
    }
    {%- endif %}
{%- endfor %}
}
{% endmacro %}