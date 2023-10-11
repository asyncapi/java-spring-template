{%- set hasSubscribe = false -%}
{%- set hasPublish = false -%}
{%- for channelName, channel in asyncapi.channels() -%}
    {%- if channel.hasPublish() -%}
        {%- set hasPublish = true -%}
    {%- endif -%}
    {%- if channel.hasSubscribe() -%}
        {%- set hasSubscribe = true -%}
    {%- endif -%}
{%- endfor -%}
package {{ params['userJavaPackage'] }};

{% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}{% for message in channel.subscribe().messages() %}
import {{params['userJavaPackage']}}.model.{{message.payload().uid() | camelCase | upperFirst}};
{% endfor %}{% endif %} {% endfor %}
{% for channelName, channel in asyncapi.channels() %} {% if channel.hasPublish() %}{% for message in channel.publish().messages() %}
import {{ params['userJavaPackage'] }}.model.{{message.payload().uid() | camelCase | upperFirst}};
{% endfor %}{% endif %} {% endfor %}
{% if hasSubscribe %}import {{ params['userJavaPackage'] }}.service.PublisherService;{% endif %}
import org.eclipse.paho.client.mqttv3.*;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.GenericContainer;

import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

/**
 * Example of tests for mqtt based on testcontainers library
 */
@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestcontainerMqttTest {

    {% for channelName, channel in asyncapi.channels() %}{% if channel.hasPublish() %}
    @Value("${mqtt.topic.{{-channel.publish().id() | camelCase-}}}")
    private String {{channel.publish().id() | camelCase-}}PublishTopic;
    {% elif channel.hasSubscribe() %}
    @Value("${mqtt.topic.{{-channel.subscribe().id() | camelCase-}}}")
    private String {{channel.subscribe().id() | camelCase-}}SubscribeTopic;
    {% endif %}{% endfor %}

    @ClassRule
    public static GenericContainer mosquitto = new GenericContainer("eclipse-mosquitto").withExposedPorts(1883);
    {% if hasSubscribe %}
    @Autowired
    private PublisherService publisherService;
    {% endif %}
    private IMqttClient publisher;

    @DynamicPropertySource
    public static void mqttProperties(DynamicPropertyRegistry registry) {
        String address = "tcp://" + mosquitto.getContainerIpAddress() + mosquitto.getMappedPort(1883);
        registry.add("mqtt.broker.address", () -> address);
    }

    @BeforeEach
    public void before() throws MqttException {
        String address = "tcp://" + mosquitto.getContainerIpAddress() + mosquitto.getMappedPort(1883);
        publisher = new MqttClient(address, UUID.randomUUID().toString());
        publisher.connect();
    }

    @AfterEach
    public void after() throws MqttException {
        publisher.disconnect();
    }

    {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
    {% if channel.subscribe().hasMultipleMessages() %}{% set typeName = "Object" %}{% else %}{% set typeName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %}{% endif -%}
    @Test
    public void {{channel.subscribe().id() | camelCase}}ProducerTestcontainers() throws MqttException {
        {{typeName}} payload = new {{typeName}}();

        List<MqttMessage> receivedMessages = new ArrayList<>();
        publisher.subscribe({{channel.subscribe().id() | camelCase-}}SubscribeTopic, (topic, message) -> {
            receivedMessages.add(message);
        });

        publisherService.{{channel.subscribe().id() | camelCase}}(payload.toString());

        MqttMessage message = receivedMessages.get(receivedMessages.size() - 1);

        assertEquals("Message is wrong", payload.toString().getBytes(), message.getPayload());
    }
    {% endif %} {% if channel.hasPublish() %}
    @Test
    public void {{channel.publish().id() | camelCase}}ConsumerTestcontainers() throws Exception {
        {% if channel.publish().hasMultipleMessages() %}{% set typeName = "Object" %}{% else %}{% set typeName = channel.publish().message().payload().uid() | camelCase | upperFirst %}{% endif -%}
        {{typeName}} payload = new {{typeName}}();

        sendMessage({{channel.publish().id() | camelCase-}}PublishTopic, payload.toString().getBytes());

        Thread.sleep(1_000);
    }
    {% endif %}
    {% endfor %}
    {% if hasPublish %}
    protected void sendMessage(String topic, byte[] message) throws Exception {
        publisher.publish(topic, new MqttMessage(message));
    }
    {% endif %}
}
