{% macro wsConfig(asyncapi, params) %}

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import javax.annotation.processing.Generated;
import java.net.URI;
import java.net.URISyntaxException;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends WebSocketMessageBrokerConfigurer {

    {% for serverName, server in asyncapi.servers() %}
    @Value("${ws.server.{{serverName}}}")
    private String {{serverName}}Url;
    {% endfor %}

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        {% for serverName, server in asyncapi.servers() %}
        registry.addEndpoint(getPath({{serverName}}Url));
        registry.addEndpoint(getPath({{serverName}}Url)).withSockJS();
        {% endfor %}
    }

    private String getPath(String serverURL) {
        String path;
        try {URI uri = null;
            uri = new URI(serverURL);
            path = uri.getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return path;
    }
}
{% endmacro %}