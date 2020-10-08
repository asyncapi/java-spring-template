package {{ params['userJavaPackage'] }}.model;

import javax.validation.constraints.*;
import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;
import java.util.Objects;

{% if schema.description() or schema.examples() %}/**{% for line in schema.description() | splitByLines %}
 * {{ line | safe}}{% endfor %}{% if schema.examples() %}
 * Examples: {{schema.examples() | examplesToString | safe}}{% endif %}
 */{% endif %}
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
        {%- elif prop.enum() and (prop.type() === 'string' or prop.type() === 'integer') %}
    public enum {{propName | camelCase | upperFirst}}Enum {
        {% for e in prop.enum() %}
        {%- if prop.type() === 'string'%}
        {{e | upper | createEnum}}(String.valueOf("{{e}}")){% if not loop.last %},{% else %};{% endif %}
        {%- else %}
        NUMBER_{{e}}({{e}}){% if not loop.last %},{% else %};{% endif %}
        {%- endif %}
        {% endfor %}
        private {% if prop.type() === 'string'%}String{% else %}Integer{% endif %} value;

        {{propName | camelCase | upperFirst}}Enum ({% if prop.type() === 'string'%}String{% else %}Integer{% endif %} v) {
            value = v;
        }

        public {% if prop.type() === 'string'%}String{% else %}Integer{% endif %} value() {
            return value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static {{propName | camelCase | upperFirst}}Enum fromValue({% if prop.type() === 'string'%}String{% else %}Integer{% endif %} value) {
            for ( {{propName | camelCase | upperFirst}}Enum b :  {{propName | camelCase | upperFirst}}Enum.values()) {
                if (Objects.equals(b.value, value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

    private @Valid {{propName | camelCase | upperFirst}}Enum {{propName | camelCase}};
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
        {%- elif prop.enum() and (prop.type() === 'string' or prop.type() === 'integer') %}
            {%- set propType = (propName | camelCase | upperFirst) + 'Enum' %}
        {%- else %}
            {%- if prop.format() %}
                {%- set propType = prop.format() | toJavaType %}
            {%- else %}
                {%- set propType = prop.type() | toJavaType %}
            {%- endif %}
        {%- endif %}

    {% if prop.description() or prop.examples()%}/**{% for line in prop.description() | splitByLines %}
     * {{ line | safe}}{% endfor %}{% if prop.examples() %}
     * Examples: {{prop.examples() | examplesToString | safe}}{% endif %}
     */{% endif %}
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
    {% if params.disableEqualsHashCode === 'false' %}@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        {{schemaName | camelCase | upperFirst}} {{schemaName | camelCase}} = ({{schemaName | camelCase | upperFirst}}) o;
        return {% for propName, prop in schema.properties() %}{% set varName = propName | camelCase %}{% if prop.type() === 'array' %}{% set varName = propName | camelCase + 'Array' %}{% endif %}
            {% if prop.type() === 'array' %}Arrays{% else %}Objects{% endif %}.equals(this.{{varName}}, {{schemaName | camelCase}}.{{varName}}){% if not loop.last %} &&{% else %};{% endif %}{% endfor %}
    }

    @Override
    public int hashCode() {
        return Objects.hash({% for propName, prop in schema.properties() %}{{propName | camelCase}}{% if prop.type() === 'array' %}Array{% endif %}{% if not loop.last %}, {% endif %}{% endfor %});
    }{% endif %}

    @Override
    public String toString() {
        return "class {{schemaName | camelCase | upperFirst}} {\n" +
        {% for propName, prop in schema.properties() %}{% set varName = propName | camelCase %}{% if prop.type() === 'array' %}{% set varName = propName | camelCase + 'Array' %}{% endif %}
                "    {{varName}}: " + toIndentedString({{varName}}) + "\n" +{% endfor %}
                "}";
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
           return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}