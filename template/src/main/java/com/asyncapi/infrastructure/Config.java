package {{ params['userJavaPackage'] }}.infrastructure;

{%- from "partials/AmqpConfig.java" import amqpConfig -%}
{%- from "partials/MqttConfig.java" import mqttConfig -%}
{%- from "partials/KafkaConfig.java" import kafkaConfig -%}

{%- if asyncapi | isProtocol('amqp') -%}
{{- amqpConfig(asyncapi, params) -}}
{%- endif -%}
{%- if asyncapi | isProtocol('mqtt') -%}
{{- mqttConfig(asyncapi, params) -}}
{%- endif -%}
{%- if (asyncapi | isProtocol('kafka')) or (asyncapi | isProtocol('kafka-secure')) -%}
{{- kafkaConfig(asyncapi, params) -}}
{%- endif -%}