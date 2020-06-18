package {{ params['userJavaPackage'] }};

{%- from "partials/configuration/AmqpConfig.java" import amqpConfig -%}
{%- from "partials/configuration/MqttConfig.java" import mqttConfig -%}
{%- from "partials/configuration/KafkaConfig.java" import kafkaConfig -%}
{%- from "partials/configuration/PulsarConfig.java" import pulsarConfig -%}

{%- if asyncapi | isProtocol('amqp') -%}
{{- amqpConfig(asyncapi, params) -}}
{%- endif -%}
{%- if asyncapi | isProtocol('mqtt') -%}
{{- mqttConfig(asyncapi, params) -}}
{%- endif -%}
{%- if asyncapi | isProtocol('kafka') -%}
{{- kafkaConfig(asyncapi, params) -}}
{%- endif -%}
{%- if asyncapi | isProtocol('pulsar') -%}
{{- pulsarConfig(asyncapi, params) -}}
{%- endif -%}
