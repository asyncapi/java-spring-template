package com.asyncapi.model;

import javax.validation.constraints.*;
import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;

public class {{schemaName | camelCase | upperFirst}} {
    {% for propName, prop in schema.properties() %}
        {%- if prop.type() === 'object' %}
    private @Valid {{prop.uid() | camelCase | upperFirst}} {{propName | camelCase}};
        {%- elif prop.type() === 'array' %}
            {%- if prop.items().format() %}
    private @Valid {{prop.items().format() | toJavaType}}[] {{propName | camelCase}}Array;
            {%- else %}
    private @Valid {{prop.items().type() | toJavaType}}[] {{propName | camelCase}}Array;
            {%- endif %}
        {%- else %}
            {%- if prop.format() %}
    private @Valid {{prop.format() | toJavaType}} {{propName | camelCase}};
            {%- else %}
    private @Valid {{prop.type() | toJavaType}} {{propName | camelCase}};
            {%- endif %}
        {%- endif %}
    {% endfor %}

    {% for propName, prop in schema.properties() %}
        {%- set varName = propName | camelCase %}
        {%- set className = propName | camelCase | upperFirst %}
        {%- if prop.type() === 'object' %}
            {%- set propType = prop.uid() | camelCase | upperFirst %}
        {%- elif prop.type() === 'array' %}
            {%- set varName = propName | camelCase + 'Array' %}
            {%- if prop.items().format() %}
                {%- set propType = prop.items().format() | toJavaType + '[]' %}
            {%- else %}
                {%- set propType = prop.items().type() | toJavaType + '[]' %}
            {%- endif %}
        {%- else %}
            {%- if prop.format() %}
                {%- set propType = prop.format() | toJavaType %}
            {%- else %}
                {%- set propType = prop.type() | toJavaType %}
            {%- endif %}
        {%- endif %}

    @JsonProperty("{{propName}}")
    {%- if propName | isRequired(schema.required()) %}@NotNull{% endif %}
    {%- if prop.minLength() or prop.maxLength() or prop.maxItems() or prop.minItems() %}@Size({% if prop.minLength() or prop.minItems() %}min = {{prop.minLength()}}{{prop.minItems()}}{% endif %}{% if prop.maxLength() or prop.maxItems() %}{% if prop.minLength() or prop.minItems() %},{% endif %}max = {{prop.maxLength()}}{{prop.maxItems()}}{% endif %}){% endif %}
    {%- if prop.pattern() %}@Pattern(regexp="{{prop.pattern()}}"){% endif %}
    {%- if prop.minimum() %}@Min({{prop.minimum()}}){% endif %}{% if prop.exclusiveMinimum() %}@Min({{prop.exclusiveMinimum() + 1}}){% endif %}
    {%- if prop.maximum() %}@Max({{prop.maximum()}}){% endif %}{% if prop.exclusiveMaximum() %}@Max({{prop.exclusiveMaximum() + 1}}){% endif %}
    public {{propType}} get{{className}}() {
        return {{varName}};
    }

    public void set{{className}}({{propType}} {{varName}}) {
        this.{{varName}} = {{varName}};
    }
    {% endfor %}
}