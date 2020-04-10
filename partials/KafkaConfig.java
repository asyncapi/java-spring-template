{% macro kafkaConfig(asyncapi) %}
{%- set hasPublish = false -%}
{%- set hasSubscribe = false -%}
{%- for channelName, channel in asyncapi.channels() -%}
    {%- if channel.hasSubscribe() -%}
        {%- set hasSubscribe = true -%}
    {%- endif -%}
    {%- if channel.hasPublish() -%}
        {%- set hasPublish = true -%}
    {%- endif -%}
{%- endfor -%}
package com.asyncapi.infrastructure;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
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
{% if hasSubscribe %}@EnableKafka{% endif %}
public class Config {
{%- if hasPublish or hasSubscribe %}
    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;
{% endif %}
{%- if hasSubscribe %}
    @Value("${kafka.subscribe.pool-timeout")
    private long poolTimeout;

    @Value("${kafka.subscribe.amount-of-listeners")
    private Integer amountOfListeners;
{% endif %}
{%- if hasPublish %}
    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS,
    {%- for schema in asyncapi.allSchemas().values() %}
        {%- if schema.uid() | first !== '<' %}
        "{{schema.uid()}}:com.asyncapi.model.{{schema.uid() | camelCase | upperFirst}}{% if not loop.last %}," +{% else %}"{% endif %}
        {% endif -%}
    {% endfor -%}
        );
        // See https://kafka.apache.org/documentation/#producerconfigs for more properties
        return props;
    }
{% endif %}
{%- if hasSubscribe %}
    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.setConcurrency(amountOfListeners);
        factory.getContainerProperties().setPollTimeout(poolTimeout);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
    }

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonSerializer.TYPE_MAPPINGS,
    {%- for schema in asyncapi.allSchemas().values() %}
        {%- if schema.uid() | first !== '<' %}
        "{{schema.uid()}}:com.asyncapi.model.{{schema.uid() | camelCase | upperFirst}}{% if not loop.last %}," +{% else %}"{% endif %}
        {% endif -%}
    {% endfor -%}
        );
        return props;
    }
{% endif %}
}
{% endmacro %}