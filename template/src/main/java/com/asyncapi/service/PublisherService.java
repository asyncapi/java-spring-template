package {{ params['userJavaPackage'] }}.service;
{%- from "partials/CommonPublisher.java" import commonPublisher -%}
{%- from "partials/KafkaPublisher.java" import kafkaPublisher -%}
{%- from "partials/AmqpPublisher.java" import amqpPublisher -%}
{%- from "partials/WebSocketPublisher.java" import wsPublisher -%}
{%- if asyncapi | isProtocol('kafka') -%}
{{- kafkaPublisher(asyncapi, params) -}}
{%- elif asyncapi | isProtocol('amqp') -%}
{{- amqpPublisher(asyncapi, params) -}}
{%- elif asyncapi | isProtocol('ws') -%}
{{- wsPublisher(asyncapi, params) -}}
{%- else -%}
{{- commonPublisher(asyncapi, params) -}}
{%- endif -%}