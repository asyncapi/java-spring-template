{% macro kafkaConfig(asyncapi, params) %}
{%- set hasSubscribe = false -%}
{%- set hasPublish = false -%}
{%- for channelName, channel in asyncapi.channels() -%}
    {%- if channel.hasPublish() -%}
        {%- set hasPublish = true -%}
    {%- endif -%}
    {%- if channel.hasSubscribe() -%}
        {%- set hasSubscribe = true -%}
    {%- endif -%}
{%- endfor %}

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
{% if hasPublish %}@EnableKafka{% endif %}
public class Config {
{% if hasSubscribe or hasPublish %}
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;{% endif %}
{% if hasPublish %}
    @Value("${spring.kafka.listener.poll-timeout}")
    private long pollTimeout;

    @Value("${spring.kafka.listener.concurrency}")
    private int concurrency;{% endif %}
{%- if hasSubscribe %}
    @Bean
    public KafkaTemplate<Integer, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<Integer, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    {% if params.addTypeInfoHeader === 'false' %}
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
    {% endif -%}
        props.put(JsonSerializer.TYPE_MAPPINGS,
    {%- for schema in asyncapi.allSchemas().values() | isObjectType %}
        {%- if schema.uid() | first !== '<' and schema.type() === 'object' %}
        "{{schema.uid()}}:{{params['userJavaPackage']}}.model.{{schema.uid() | camelCase | upperFirst}}{% if not loop.last %}," +{% else %}"{% endif %}
        {% endif -%}
    {% endfor -%}
        );
        // See https://kafka.apache.org/documentation/#producerconfigs for more properties
        return props;
    }
{% endif %}
{%- if hasPublish %}
    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, Object>>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(concurrency);
        factory.getContainerProperties().setPollTimeout(pollTimeout);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, Object> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, IntegerDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(JsonDeserializer.TYPE_MAPPINGS,
    {%- for schema in asyncapi.allSchemas().values() | isObjectType %}
        {%- if schema.uid() | first !== '<' and schema.type() === 'object' %}
        "{{schema.uid()}}:{{params['userJavaPackage']}}.model.{{schema.uid() | camelCase | upperFirst}}{% if not loop.last %}," +{% else %}"{% endif %}
        {% endif -%}
    {% endfor -%}
        );
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "{{params['userJavaPackage']}}.model");
        return props;
    }
{% endif %}
}
{% endmacro %}