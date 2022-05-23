package {{ params['userJavaPackage'] }}.service;

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

        {%- for channelName, channel in asyncapi.channels() %}
            {%- if channel.hasSubscribe() %}
                {%- for message in channel.subscribe().messages() %}
        publisherService.{{channel.subscribe().id() | camelCase}}({% if asyncapi | isProtocol('kafka') %}(new Random()).nextInt(), new {{ params['userJavaPackage'] }}.model.{{message.payload().uid() | camelCase | upperFirst}}(){% else %}"Hello World from {{channelName}}"{% endif %});
                {% endfor -%}
            {% endif -%}
        {%- endfor %}
        System.out.println("Message sent");
    }
}
