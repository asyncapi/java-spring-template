package com.asyncapi.service;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageHandlerService {

{% for channelName, channel in asyncapi.channels() %}
  {% if channel.hasSubscribe() %}
    public void handle{{channelName | upperFirst}}(Message<?> message) {
        System.out.println("handler {{channelName}}");
        System.out.println(message.getPayload());
    }
  {% endif %}
{% endfor %}

}
