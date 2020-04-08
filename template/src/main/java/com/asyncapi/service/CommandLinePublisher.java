package com.asyncapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLinePublisher implements CommandLineRunner {

    @Autowired
    PublisherService publisherService;

    @Override
    public void run(String... args) {
        System.out.println("******* Sending message: *******");

        {% for channelName, channel in asyncapi.channels() %}
            {% if channel.hasPublish() %}
        publisherService.publish{{channelName | capitalize}}("Hello World from {{channelName}}");
            {% endif %}
        {% endfor %}
        System.out.println("Message sent");
    }
}
