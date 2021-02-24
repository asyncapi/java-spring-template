package com.asyncapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class CommandLinePublisher implements CommandLineRunner {

    @Autowired
    PublisherService publisherService;

    @Override
    public void run(String... args) {
        System.out.println("******* Sending message: *******");
        publisherService.turnOn("Hello World from smartylighting/streetlights/1/0/action/{streetlightId}/turn/on");
            
        publisherService.turnOff("Hello World from smartylighting/streetlights/1/0/action/{streetlightId}/turn/off");
            
        publisherService.dimLight("Hello World from smartylighting/streetlights/1/0/action/{streetlightId}/dim");
            
        System.out.println("Message sent");
    }
}
