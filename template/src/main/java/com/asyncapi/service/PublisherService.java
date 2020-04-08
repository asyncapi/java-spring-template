package com.asyncapi.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface PublisherService {

  {% for channelName, channel in asyncapi.channels() %}
    {% if channel.hasPublish() %}

    @Gateway(requestChannel = "{{channelName | camelCase}}OutboundChannel")
    void publish{{channelName | capitalize}}(String data);
    {% endif %}
  {% endfor %}
}
