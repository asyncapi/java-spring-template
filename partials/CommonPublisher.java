{% macro commonPublisher(asyncapi) %}

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

import javax.annotation.processing.Generated;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@MessagingGateway
public interface PublisherService {

    {% for channelName, channel in asyncapi.channels() %}
        {% if channel.hasSubscribe() %}
    {% if channel.description() or channel.subscribe().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.subscribe().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    @Gateway(requestChannel = "{{channel.subscribe().id() | camelCase}}OutboundChannel")
    void {{channel.subscribe().id() | camelCase}}(String data);
        {% endif %}
    {% endfor %}
}
{% endmacro %}