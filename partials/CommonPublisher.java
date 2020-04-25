{% macro commonPublisher(asyncapi) %}
package com.asyncapi.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface PublisherService {

    {% for channelName, channel in asyncapi.channels() %}
        {% if channel.hasSubscribe() %}
    {% if channel.description() or channel.subscribe().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.subscribe().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    @Gateway(requestChannel = "{{channelName | camelCase}}OutboundChannel")
    void {{channel.subscribe().id() | camelCase}}(String data);
        {% endif %}
    {% endfor %}
}
{% endmacro %}