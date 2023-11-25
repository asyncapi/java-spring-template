package {{ params['userJavaPackage'] }}.service;
{%- from "partials/CommonPublisherImpl.java" import commonPublisherImpl -%}
{%- from "partials/KafkaPublisherImpl.java" import kafkaPublisherImpl -%}
{%- from "partials/AmqpPublisherImpl.java" import amqpPublisherImpl -%}
{%- if asyncapi | isProtocol('kafka') -%}
{{- kafkaPublisherImpl(asyncapi, params) -}}
{%- elif asyncapi | isProtocol('amqp') -%}
{{- amqpPublisherImpl(asyncapi, params) -}}
{%- else -%}
{{- commonPublisherImpl(asyncapi, params) -}}
{%- endif -%}