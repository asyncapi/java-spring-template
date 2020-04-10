{% macro kafkaPublisher(asyncapi) %}
package com.asyncapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
{% for channelName, channel in asyncapi.channels() %}
        {%- if channel.hasPublish() %}
import com.asyncapi.model.{{channel.publish().message().payload().uid() | camelCase | upperFirst}};
    {% endif -%}
{% endfor %}

@Service
public class PublisherService {

    @Autowired
    private KafkaTemplate<Integer, String> kafkaTemplate;
{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasPublish() %} {% set varName = channel.publish().message().payload().uid() | camelCase %}
    public void {{channel.publish().id() | camelCase}}({{varName | upperFirst}} {{varName}}) {
        Message<{{varName | upperFirst}}> message = MessageBuilder.withPayload({{varName}})
                .setHeader(KafkaHeaders.TOPIC, "{{channelName}}")
                .build();
        kafkaTemplate.send(message);
    }
    {%- endif %}
{%- endfor %}
}
{% endmacro %}