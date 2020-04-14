{% macro commonPublisher(asyncapi) %}
package com.asyncapi.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface PublisherService {

    {% for channelName, channel in asyncapi.channels() %}
        {% if channel.hasSubscribe() %}
    @Gateway(requestChannel = "{{channelName | camelCase}}OutboundChannel")
    void {{channel.subscribe().id() | camelCase}}(String data);
        {% endif %}
    {% endfor %}
}
{% endmacro %}