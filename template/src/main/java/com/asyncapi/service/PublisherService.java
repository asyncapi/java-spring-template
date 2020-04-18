{%- from "partials/CommonPublisher.java" import commonPublisher -%}
{%- from "partials/KafkaPublisher.java" import kafkaPublisher -%}
{%- if asyncapi | isProtocol('kafka') -%}
{{- kafkaPublisher(asyncapi) -}}
{%- else -%}
{{- commonPublisher(asyncapi) -}}
{%- endif -%}