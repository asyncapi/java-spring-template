{% macro wsConfig(asyncapi, params) %}

import org.springframework.context.annotation.Configuration;

import javax.annotation.processing.Generated;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat");
        registry.addEndpoint("/chat").withSockJS();
    }
}