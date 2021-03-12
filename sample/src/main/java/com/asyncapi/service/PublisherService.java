package com.asyncapi.service;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface PublisherService {

    
        
    
        
    
    @Gateway(requestChannel = "turnOnOutboundChannel")
    void turnOn(String data);
        
    
        
    
    @Gateway(requestChannel = "turnOffOutboundChannel")
    void turnOff(String data);
        
    
        
    
    @Gateway(requestChannel = "dimLightOutboundChannel")
    void dimLight(String data);
        
    
}
