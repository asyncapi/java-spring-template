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
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.shaded.com.google.common.collect.Lists;

import javax.annotation.processing.Generated;
import java.time.Duration;
import java.util.*;

import static java.util.Collections.emptyList;
import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.apache.kafka.clients.producer.ProducerConfig.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Example of tests for kafka based on testcontainers library
 */
@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestcontainerKafkaTest {

    {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
    private static final String {{channel.subscribe().id() | upper-}}_SUBSCRIBE_TOPIC = "{{channelName}}";
    {% endif %} {% if channel.hasPublish() %}
    private static final String {{channel.publish().id() | upper-}}_PUBLISH_TOPIC = "{{channelName}}";
    {% endif %} {% endfor %}
    @ClassRule
    public static KafkaContainer kafka = new KafkaContainer();
    {% if hasSubscribe %}
    @Autowired
    private PublisherService publisherService;
    {% endif %}
    @DynamicPropertySource
    public static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }
    {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
    {%- if channel.subscribe().hasMultipleMessages() %}{% set typeName = "Object" %}{% else %}{% set typeName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %}{% endif %}
    @Test
    public void {{channel.subscribe().id() | camelCase}}ProducerTestcontainers() {
        {{typeName}} payload = new {{typeName}}();
        Integer key = 1;
        Integer wrongKey = key + 1;

        consumeMessages({{channel.subscribe().id() | upper-}}_SUBSCRIBE_TOPIC);

        publisherService.{{channel.subscribe().id() | camelCase}}(key, payload);

        ConsumerRecord<Integer, Object> consumedMessage = consumeMessage({{channel.subscribe().id() |  upper-}}_SUBSCRIBE_TOPIC);

        assertEquals("Key is wrong", key, consumedMessage.key());
        assertNotEquals("Key is wrong", wrongKey, consumedMessage.key());
    }
    {% endif %} {% if channel.hasPublish() %}
    @Test
    public void {{channel.publish().id() | camelCase}}ConsumerTestcontainers() throws Exception {
        Integer key = 1;
        {%- if channel.publish().hasMultipleMessages() %}{% set typeName = "Object" %}{% else %}{% set typeName = channel.publish().message().payload().uid() | camelCase | upperFirst %}{% endif %}
        {{typeName}} payload = new {{typeName}}();

        ProducerRecord<Integer, Object> producerRecord = new ProducerRecord<>({{channel.publish().id() |  upper-}}_PUBLISH_TOPIC, key, payload);

        sendMessage(producerRecord);

        Thread.sleep(1_000);
    }
    {% endif %}
    {% endfor %}
    {% if hasPublish %}
    protected void sendMessage(ProducerRecord message) throws Exception {
        try (KafkaProducer<Integer, Object> kafkaProducer = createProducer()) {
            kafkaProducer.send(message).get();
        }
    }

    protected void sendMessage(String topic, Object message) throws Exception {
        try (KafkaProducer<Integer, Object> kafkaProducer = createProducer()) {
            kafkaProducer.send(new ProducerRecord<>(topic, message)).get();
        }
    }

    protected KafkaProducer<Integer, Object> createProducer() {
        return new KafkaProducer<>(getKafkaProducerConfiguration());
    }

    protected Map<String, Object> getKafkaProducerConfiguration() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        configs.put(KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class.getName());
        configs.put(VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class.getName());
        return configs;
    }
    {% endif %}{% if hasSubscribe %}
    protected ConsumerRecord<Integer, Object> consumeMessage(String topic) {
        return consumeMessages(topic)
                .stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("no message received"));
    }

    protected List<ConsumerRecord<Integer, Object>> consumeMessages(String topic) {
        try (KafkaConsumer<Integer, Object> consumer = createConsumer(topic)) {
            return pollForRecords(consumer);
        }
    }

    protected KafkaConsumer<Integer, Object> createConsumer(String topic) {
        Properties properties = new Properties();
        properties.putAll(getKafkaConsumerConfiguration());
        KafkaConsumer<Integer, Object> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Collections.singleton(topic));
        return consumer;
    }

    protected static <K, V> List<ConsumerRecord<K, V>> pollForRecords(KafkaConsumer<K, V> consumer) {
        ConsumerRecords<K, V> received = consumer.poll(Duration.ofSeconds(10L));
        return received == null ? emptyList() : Lists.newArrayList(received);
    }

    protected Map<String, Object> getKafkaConsumerConfiguration() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        configs.put(GROUP_ID_CONFIG, "testGroup");
        configs.put(AUTO_OFFSET_RESET_CONFIG, "earliest");
        configs.put(KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class.getName());
        configs.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        configs.put(JsonDeserializer.TYPE_MAPPINGS,
                  {%- for schema in asyncapi.allSchemas().values() | isObjectType %}
        {%- if schema.uid() | first !== '<' and schema.type() === 'object' %}
        "{{schema.uid()}}:{{params['userJavaPackage']}}.model.{{schema.uid() | camelCase | upperFirst}}{% if not loop.last %}," +{% else %}"{% endif %}
        {% endif -%}
        {% endfor -%}
        );
        configs.put(JsonDeserializer.TRUSTED_PACKAGES, "{{params['userJavaPackage']}}.model");
        return configs;
    }
    {% endif %}
}
