{%- from "partials/AmqpConfig.java" import amqpConfig -%}
{%- from "partials/MqttConfig.java" import mqttConfig -%}
{%- from "partials/KafkaConfig.java" import kafkaConfig -%}

{%- if asyncapi | isProtocol('amqp') -%}
{{- amqpConfig(asyncapi) -}}
{%- endif -%}
{%- if asyncapi | isProtocol('mqtt') -%}
{{- mqttConfig(asyncapi) -}}
{%- endif -%}
{%- if asyncapi | isProtocol('kafka') -%}
{{- kafkaConfig(asyncapi) -}}
{%- endif -%}