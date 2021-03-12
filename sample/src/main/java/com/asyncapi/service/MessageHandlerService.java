package com.asyncapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

@Service
public class MessageHandlerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerService.class);

    
      
    /**
     * The topic on which measured values may be produced and consumed.
     */
    public void handleReceiveLightMeasurement(Message<?> message) {
        LOGGER.info("handler smartylighting/streetlights/1/0/event/{streetlightId}/lighting/measured");
        LOGGER.info(String.valueOf(message.getPayload().toString()));
    }
      
    
      
    
      
    
      
    

}
