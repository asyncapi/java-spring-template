package {{ params['userJavaPackage'] }}.service;
{%- from "partials/CommonPublisherImpl.java" import commonPublisherImpl -%}
{%- if asyncapi | isProtocol('kafka') -%}
{%- elif asyncapi | isProtocol('amqp') -%}
{%- else -%}
{{- commonPublisherImpl(asyncapi, params) -}}
{%- endif -%}