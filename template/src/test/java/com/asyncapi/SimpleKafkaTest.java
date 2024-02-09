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

{% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %} {% for message in channel.subscribe().messages() %}
import {{params['userJavaPackage']}}.model.{{message.payload().uid() | camelCase | upperFirst}};
{% endfor %} {% endif %} {% endfor %}
{% for channelName, channel in asyncapi.channels() %} {% if channel.hasPublish() %} {% for message in channel.publish().messages() %}
import {{ params['userJavaPackage'] }}.model.{{message.payload().uid() | camelCase | upperFirst}};
{% endfor %} {% endif %} {% endfor %}
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

import javax.annotation.processing.Generated;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.util.AssertionErrors.assertEquals;

/**
 * Example of tests for kafka based on spring-kafka-test library
 */
@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@RunWith(SpringRunner.class)
@SpringBootTest
public class SimpleKafkaTest {
    {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
    private static final String {{channel.subscribe().id() | upper-}}_SUBSCRIBE_TOPIC = "{{channelName}}";
    {% endif %} {% if channel.hasPublish() %}
    private static final String {{channel.publish().id() | upper-}}_PUBLISH_TOPIC = "{{channelName}}";
    {% endif %} {% endfor %}
    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 1{% for channelName, channel in asyncapi.channels() %}{% if channel.hasSubscribe() %}, {{channel.subscribe().id() | upper-}}_SUBSCRIBE_TOPIC{% endif %}{% endfor %});

    private static EmbeddedKafkaBroker embeddedKafkaBroker = embeddedKafka.getEmbeddedKafka();

    @DynamicPropertySource
    public static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", embeddedKafkaBroker::getBrokersAsString);
    }

    {% if hasSubscribe %}
    @Autowired
    private PublisherService publisherService;
    {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
    {%- if channel.subscribe().hasMultipleMessages() %} {% set typeName = "Object" %} {% else %} {% set typeName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %} {% endif %}
    Consumer<Integer, {{typeName}}> consumer{{ channelName | camelCase | upperFirst}};
    {% endif %} {% endfor %} {% endif %} {% if hasPublish %}
    Producer<Integer, Object> producer;
    {% endif %}
    @Before
    public void init() {
        {% if hasSubscribe %}
        Map<String, Object> consumerConfigs = new HashMap<>(KafkaTestUtils.consumerProps("consumer", "true", embeddedKafkaBroker));
        consumerConfigs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerConfigs.put(JsonDeserializer.TYPE_MAPPINGS,
                    {%- for schema in asyncapi.allSchemas().values() | isObjectType %}
        {%- if schema.uid() | first !== '<' and schema.type() === 'object' %}
        "{{schema.uid()}}:{{params['userJavaPackage']}}.model.{{schema.uid() | camelCase | upperFirst}}{% if not loop.last %}," +{% else %}"{% endif %}
        {% endif -%}
        {% endfor -%}
        );
        consumerConfigs.put(JsonDeserializer.TRUSTED_PACKAGES, "{{params['userJavaPackage']}}.model");

        {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}
        {%- if channel.subscribe().hasMultipleMessages() %} {% set typeName = "Object" %} {% else %} {% set typeName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %} {% endif %}
        consumer{{ channelName | camelCase | upperFirst}} = new DefaultKafkaConsumerFactory<>(consumerConfigs, new IntegerDeserializer(), new JsonDeserializer<>({{typeName}}.class)).createConsumer();
        consumer{{ channelName | camelCase | upperFirst}}.subscribe(Collections.singleton({{channel.subscribe().id() | upper-}}_SUBSCRIBE_TOPIC));
        consumer{{ channelName | camelCase | upperFirst}}.poll(Duration.ZERO);
        {% endif %} {% endfor %} {% endif %} {% if hasPublish %}
        Map<String, Object> producerConfigs = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        producer = new DefaultKafkaProducerFactory<>(producerConfigs, new IntegerSerializer(), new JsonSerializer()).createProducer();
        {% endif %}
    }
    {% for channelName, channel in asyncapi.channels() %} {% if channel.hasSubscribe() %}{% set hasParameters = channel.hasParameters() %}
    @Test
    public void {{channel.subscribe().id() | camelCase}}ProducerTest() {
        {%- if channel.subscribe().hasMultipleMessages() %} {% set typeName = "Object" %} {% else %} {% set typeName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %} {% endif %}
        {{typeName}} payload = new {{typeName}}();
        Integer key = 1;

        KafkaTestUtils.getRecords(consumer{{ channelName | camelCase | upperFirst}});

        publisherService.{{channel.subscribe().id() | camelCase}}(key, payload{% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}, new {% if parameter.schema().type() === 'object'%}{{payloadType}}{% else %}{{parameter.schema().type() | toJavaType(false)}}{% endif %}(){% endfor %}{% endif %});

        ConsumerRecord<Integer, {{typeName}}> singleRecord = KafkaTestUtils.getSingleRecord(consumer{{ channelName | camelCase | upperFirst}}, {{channel.subscribe().id() | upper-}}_SUBSCRIBE_TOPIC);

        assertEquals("Key is wrong", key, singleRecord.key());
    }
        {% endif %} {% if channel.hasPublish() %}
    @Test
    public void {{channel.publish().id() | camelCase}}ConsumerTest() throws InterruptedException {
        Integer key = 1;
        {%- if channel.publish().hasMultipleMessages() %} {% set typeName = "Object" %} {% else %} {% set typeName = channel.publish().message().payload().uid() | camelCase | upperFirst %} {% endif %}
        {{typeName}} payload = new {{typeName}}();

        ProducerRecord<Integer, Object> producerRecord = new ProducerRecord<>({{channel.publish().id() | upper-}}_PUBLISH_TOPIC, key, payload);
        producer.send(producerRecord);
        producer.flush();
        Thread.sleep(1_000);
    }
        {% endif %}
    {% endfor %}
}
