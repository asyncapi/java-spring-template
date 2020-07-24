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

{% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
import {{ params['userJavaPackage'] }}.model.{{payloadClass}};
{% endif %} {% endfor %}
{% for channelName, channel in asyncapi.channels() %} {% if channel.hasPublish() %}
import {{ params['userJavaPackage'] }}.model.{{payloadClass}};
{% endif %} {% endfor %}
{% if hasSubscribe %}import {{ params['userJavaPackage'] }}.service.PublisherService;{% endif %}
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * Example of tests for kafka based on spring-kafka-test library
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleKafkaTest {
    {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
    private static final String {{channel.subscribe().id() | upper-}}_TOPIC = "{{channelName}}";
    {% endif %} {% if channel.hasPublish() %}
    private static final String {{channel.publish().id() | upper-}}_TOPIC = "{{channelName}}";
    {% endif %} {% endfor %}
    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 1{% for channelName, channel in asyncapi.channels() %}{% if channel.hasSubscribe() %}, {{channel.subscribe().id() | upper-}}_TOPIC{% endif %}{% endfor %});

    private static EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafka.getEmbeddedKafka();

    @DynamicPropertySource
    public static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", embeddedKafkaBroker::getBrokersAsString);
    }

    {% if hasSubscribe %}
    @Autowired
    private PublisherService publisherService;
    {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
    Consumer<Integer, {{payloadClass}}> consumer{{ channelName | camelCase | upperFirst}};
    {% endif %} {% endfor %} {% endif %} {% if hasPublish %}
    Producer<Integer, Object> producer;
    {% endif %}
    @Before
    public void init() {
        {% if hasSubscribe %}
        Map<String, Object> consumerConfigs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "true", embeddedKafkaBroker));
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
        consumer{{ channelName | camelCase | upperFirst}} = new DefaultKafkaConsumerFactory<>(consumerConfigs, new IntegerDeserializer(), new JsonDeserializer<>({{payloadClass}}.class)).createConsumer();
        consumer{{ channelName | camelCase | upperFirst}}.subscribe(Collections.singleton({{channel.subscribe().id() | upper-}}_TOPIC));
        consumer{{ channelName | camelCase | upperFirst}}.poll(Duration.ZERO);
        {% endif %} {% endfor %} {% endif %} {% if hasPublish %}
        Map<String, Object> producerConfigs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producer = new DefaultKafkaProducerFactory<>(producerConfigs, new IntegerSerializer(), new JsonSerializer()).createProducer();
        {% endif %}
    }
    {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
    @Test
    public void {{channel.subscribe().id() | camelCase}}ProducerTest() {
        {{payloadClass}} payload = new {{payloadClass}}();
        Integer key = 1;

        KafkaTestUtils.getRecords(consumer{{ channelName | camelCase | upperFirst}});

        publisherService.{{channel.subscribe().id() | camelCase}}(key, payload);

        ConsumerRecord<Integer, {{payloadClass}}> singleRecord = KafkaTestUtils.getSingleRecord(consumer{{ channelName | camelCase | upperFirst}}, {{channel.subscribe().id() | upper-}}_TOPIC);

        assertEquals("Key is wrong", key, singleRecord.key());
    }
        {% endif %} {% if channel.hasPublish() %}
    @Test
    public void {{channel.publish().id() | camelCase}}ConsumerTest() throws InterruptedException {
        Integer key = 1;
        {{payloadClass}} payload = new {{payloadClass}}();

        ProducerRecord<Integer, Object> producerRecord = new ProducerRecord<>({{channel.publish().id() | upper-}}_TOPIC, key, payload);
        producer.send(producerRecord);
        producer.flush();
        Thread.sleep(1_000);
    }
        {% endif %}
    {% endfor %}
}
