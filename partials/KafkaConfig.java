{% macro kafkaConfig(asyncapi, params) %}
{%- set hasSubscribe = false -%}
{%- set hasPublish = false -%}
{%- set hasParameters = false -%}
{%- for channelName, channel in asyncapi.channels() -%}
    {%- if channel.hasPublish() -%}
        {%- set hasPublish = true -%}
    {%- endif -%}
    {%- if channel.hasSubscribe() -%}
        {%- set hasSubscribe = true -%}
    {%- endif -%}
    {%- if channel.hasParameters() -%}
        {%- set hasParameters = true -%}
    {%- endif -%}
{%- endfor %}

{%- set securityProtocol = "PLAINTEXT" -%}
{%- set saslMechanism = null -%}
{%- set saslJaasConfig = null -%}
{%- for serverName, server in asyncapi.servers() -%}
    {%- if server.protocol() == "kafka" -%}
        {%- if server.security() -%}
            {%- set securityProtocol = "SASL_PLAINTEXT" -%}
        {%- else -%}
            {%- set securityProtocol = "PLAINTEXT" -%}
        {%- endif -%}
    {%- elif server.protocol() == "kafka-secure" -%}
        {%- if server.security() -%}
            {%- set securityProtocol = "SASL_SSL" -%}
        {%- else -%}
            {%- set securityProtocol = "SSL" -%}
        {%- endif -%}
    {%- endif -%}

    {%- if asyncapi.hasComponents() and asyncapi.components().hasSecuritySchemes() and server.security() -%}
        {%- for securityRef in server.security() -%}
            {%- for securityName, securityObj in securityRef -%}
                {%- for securityRefName, securityRefObj in securityObj -%}
                    {%- for securitySchemeRef, securityScheme in asyncapi.components().securitySchemes()[securityRefName] -%}
                        {%- if securityScheme.type == "plain" -%}
                            {%- set saslMechanism = "PLAIN" -%}
                            {%- set saslJaasConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username='USERNAME' password='PASSWORD';" -%}
                        {%- elif securityScheme.type == "scramSha256" -%}
                            {%- set saslMechanism = "SCRAM-SHA-256" -%}
                            {%- set saslJaasConfig = "org.apache.kafka.common.security.scram.ScramLoginModule required username='USERNAME' password='PASSWORD';" -%}
                        {%- elif securityScheme.type == "scramSha512" -%}
                            {%- set saslMechanism = "SCRAM-SHA-512" -%}
                            {%- set saslJaasConfig = "org.apache.kafka.common.security.scram.ScramLoginModule required username='USERNAME' password='PASSWORD';" -%}
                        {%- elif securityScheme.type == "oauth2" -%}
                            {%- set saslMechanism = "OAUTHBEARER" -%}
                            {%- set saslJaasConfig = "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required unsecuredLoginStringClaim_sub='LOGINSTRING';" -%}
                        {%- elif securityScheme.type == "gssapi" -%}
                            {%- set saslMechanism = "GSSAPI" -%}
                            {%- set saslJaasConfig = "com.sun.security.auth.module.Krb5LoginModule required useKeyTab=true storeKey=true keyTab='CLIENT.KEYTAB' principal='EMAIL@DOMAIN.COM';" -%}
                        {%- elif securityScheme.type == "X509" -%}
                            {%- set securityProtocol = "SSL" -%}
                        {%- endif -%}
                    {%- endfor -%}
                {%- endfor -%}
            {%- endfor -%}
        {%- endfor -%}
    {%- endif -%}
{%- endfor %}

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.config.SaslConfigs;
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

import javax.annotation.processing.Generated;
{% if hasParameters %}import java.util.LinkedHashMap;{% endif %}
import java.util.HashMap;
import java.util.Map;
{% if hasParameters %}import java.util.regex.Pattern;{% endif %}

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
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
{% if hasParameters %}
    @Bean
    public RoutingKafkaTemplate kafkaTemplate() {
        ProducerFactory<Object, Object> producerFactory = producerFactory();

        Map<Pattern, ProducerFactory<Object, Object>> map = new LinkedHashMap<>();
        {%- for channelName, channel in asyncapi.channels() %}
            {%- set route = channelName | toKafkaTopicString(channel.hasParameters(), channel.parameters()) | safe %}
        map.put(Pattern.compile("{{route}}"), producerFactory);
        {%- endfor %}
        return new RoutingKafkaTemplate(map);
    }

    @Bean
    public ProducerFactory<Object, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }
{% else %}
    @Bean
    public KafkaTemplate<Integer, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ProducerFactory<Integer, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }
{% endif %}
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, IntegerSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "{{ securityProtocol }}");
    {%- if saslMechanism %}
        props.put(SaslConfigs.SASL_MECHANISM, "{{ saslMechanism }}");
    {%- endif -%}
    {%- if saslJaasConfig %}
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "{{ saslJaasConfig | safe }}");
    {% endif -%}
    {%- if params.addTypeInfoHeader === 'false' %}
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
    {% endif %}
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
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "{{ securityProtocol }}");
    {%- if saslMechanism %}
        props.put(SaslConfigs.SASL_MECHANISM, "{{ saslMechanism }}");
    {%- endif -%}
    {%- if saslJaasConfig %}
        props.put(SaslConfigs.SASL_JAAS_CONFIG, "{{ saslJaasConfig | safe }}");
    {% endif %}
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