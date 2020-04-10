package com.asyncapi.model;

public class {{schemaName | camelCase | upperFirst}} {
    {% for propName, prop in schema.properties() %}
        {%- if prop.type() === 'object' %}
    private {{prop.uid() | camelCase | upperFirst}} {{propName | camelCase}};
        {% elif prop.type() === 'array' %}
            {%- if prop.items().format() %}
    private {{prop.items().format() | toJavaType}}[] {{propName | camelCase}}Array;
            {%- else %}
    private {{prop.items().type() | toJavaType}}[] {{propName | camelCase}}Array;
            {% endif %}
        {% else %}
            {%- if prop.format() %}
    private {{prop.format() | toJavaType}} {{propName | camelCase}};
            {%- else %}
    private {{prop.type() | toJavaType}} {{propName | camelCase}};
            {%- endif %}
        {%- endif %}

    {% endfor %}

    {% for propName, prop in schema.properties() %}
        {% set varName = propName | camelCase %}
        {% set className = propName | camelCase | upperFirst %}
        {% if prop.type() === 'object' %}
            {% set propType = prop.uid() | camelCase | upperFirst %}
        {% elif prop.type() === 'array' %}
            {% set varName = propName | camelCase + 'Array' %}
            {% if prop.items().format() %}
                {% set propType = prop.items().format() | toJavaType + '[]' %}
            {% else %}
                {% set propType = prop.items().type() | toJavaType + '[]' %}
            {% endif %}
        {% else %}
            {% if prop.format() %}
                {% set propType = prop.format() | toJavaType %}
            {% else %}
                {% set propType = prop.type() | toJavaType %}
            {% endif %}
        {%- endif %}
    public {{propType}} get{{className}}() {
        return {{varName}};
    }

    public void set{{className}}({{propType}} {{varName}}) {
        this.{{varName}} = {{varName}};
    }
    {% endfor %}
}