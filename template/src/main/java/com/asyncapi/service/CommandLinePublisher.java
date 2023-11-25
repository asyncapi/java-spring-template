package {{ params['userJavaPackage'] }}.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.processing.Generated;
import java.util.Random;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Component
public class CommandLinePublisher implements CommandLineRunner {

    @Autowired
    PublisherService publisherService;

    @Override
    public void run(String... args) {
        System.out.println("******* Sending message: *******");

        {%- for channelName, channel in asyncapi.channels() %}
            {%- if channel.hasSubscribe() %}{% set hasParameters = channel.hasParameters() %}
                {%- for message in channel.subscribe().messages() %}
        publisherService.{{channel.subscribe().id() | camelCase}}({% if asyncapi | isProtocol('kafka') %}(new Random()).nextInt(), new {{ params['userJavaPackage'] }}.model.{{message.payload().uid() | camelCase | upperFirst}}()
        {% elif asyncapi | isProtocol('amqp') %}{% else %}new {{ params['userJavaPackage'] }}.model.{{message.payload().uid() | camelCase | upperFirst}}(){% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}, new {% if parameter.schema().type() === 'object'%}{{ params['userJavaPackage'] }}.model.{{parameterName | camelCase | upperFirst}}{% else %}{{parameter.schema().type() | toJavaType(false)}}{% endif %}(){% endfor %}{% endif %}{% endif %});
                {% endfor -%}
            {% endif -%}
        {%- endfor %}
        System.out.println("Message sent");
    }
}
