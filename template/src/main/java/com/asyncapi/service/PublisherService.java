package com.asyncapi.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface PublisherService {

  {% for channelName, channel in asyncapi.channels() %}
    {% if channel.hasPublish() %}

    @Gateway(requestChannel = "{{channel.x-service-name() | camelCase}}OutboundChannel")
    void publish{{channel.x-service-name() | capitalize}}(String data);
    {% endif %}
  {% endfor %}
}
