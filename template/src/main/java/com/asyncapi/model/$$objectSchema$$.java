package {{ params['userJavaPackage'] }}.model;

{% if params.springBoot2 -%}
import javax.validation.constraints.*;
import javax.validation.Valid;
{% else %}
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
{%- endif %}

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import javax.annotation.processing.Generated;
import java.util.List;
import java.util.Map;
import java.util.Objects;

{% if schema.description() or schema.examples() %}/**{% for line in schema.description() | splitByLines %}
 * {{ line | safe}}{% endfor %}{% if schema.examples() %}
 * Examples: {{schema.examples() | examplesToString | safe}}{% endif %}
 */{% endif %}
@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
public class {{schemaName | camelCase | upperFirst}} {
    {% for propName, prop in schema.properties() %}
        {%- set isRequired = propName | isRequired(schema.required()) %}
        {%- if prop.additionalProperties() %}
            {%- if prop.additionalProperties() === true %}
    private @Valid Map<String, Object> {{propName | camelCase}};
            {%- elif prop.additionalProperties().type() === 'object' %}
    private @Valid Map<String, {{prop.additionalProperties().uid() | camelCase | upperFirst}}> {{propName | camelCase}};
            {%- elif prop.additionalProperties().format() %}
    private @Valid Map<String, {{prop.additionalProperties().format() | toJavaType | toClass}}> {{propName | camelCase}};
            {%- elif prop.additionalProperties().type() %}
    private @Valid Map<String, {{prop.additionalProperties().type() | toJavaType | toClass}}> {{propName | camelCase}};
            {%- endif %}
        {%- elif prop.type() === 'object' %}
    private @Valid {{prop.uid() | camelCase | upperFirst}} {{propName | camelCase}};
        {%- elif prop.type() === 'array' %}
            {%- if prop.items().type() === 'object' %}
    private @Valid List<{{prop.items().uid() | camelCase | upperFirst}}> {{propName | camelCase}};
            {%- elif prop.items().format() %}
    private @Valid List<{{prop.items().format() | toJavaType | toClass}}> {{propName | camelCase}};
            {%- else %}
    private @Valid List<{{prop.items().type() | toJavaType | toClass}}> {{propName | camelCase}};
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
        {%- elif prop.anyOf() or prop.oneOf() %}
            {%- set propType = 'OneOf' %}{%- set hasPrimitive = false %}
            {%- for obj in prop.anyOf() %}
                {%- set hasPrimitive = hasPrimitive or obj.type() !== 'object' %}
                {%- set propType = propType + obj.uid() | camelCase | upperFirst %}
            {%- endfor %}
            {%- for obj in prop.oneOf() %}
                {%- set hasPrimitive = hasPrimitive or obj.type() !== 'object' %}
                {%- set propType = propType + obj.uid() | camelCase | upperFirst %}
            {%- endfor %}
            {%- if hasPrimitive %}
                {%- set propType = 'Object' %}
            {%- else %}
    public interface {{propType}} {

    }
            {%- endif %}
    private @Valid {{propType}} {{propName | camelCase}};
        {%- elif prop.allOf() %}
            {%- set allName = 'AllOf' %}
            {%- for obj in prop.allOf() %}
                {%- set allName = allName + obj.uid() | camelCase | upperFirst %}
            {%- endfor %}
    public class {{allName}} {
            {%- for obj in prop.allOf() %}
                {%- set varName = obj.uid() | camelCase %}
                {%- set className = obj.uid() | camelCase | upperFirst %}
                {%- set propType = obj | defineType(obj.uid()) | safe %}

        private @Valid {{propType}} {{varName}};

        public {{propType}} get{{className}}() {
            return {{varName}};
        }

        public void set{{className}}({{propType}} {{varName}}) {
            this.{{varName}} = {{varName}};
        }
            {%- endfor %}
    }

    private @Valid {{allName}} {{propName | camelCase}};
        {%- else %}
            {%- if prop.format() %}
    private @Valid {{prop.format() | toJavaType(isRequired)}} {{propName | camelCase}};
            {%- else %}
    private @Valid {{prop.type() | toJavaType(isRequired)}} {{propName | camelCase}};
            {%- endif %}
        {%- endif %}
    {% endfor %}

    {% for propName, prop in schema.properties() %}
        {%- set varName = propName | camelCase %}
        {%- set className = propName | camelCase | upperFirst %}
        {%- set propType = prop | defineType(propName) | safe %}

    {% if prop.description() or prop.examples()%}/**{% for line in prop.description() | splitByLines %}
     * {{ line | safe}}{% endfor %}{% if prop.examples() %}
     * Examples: {{prop.examples() | examplesToString | safe}}{% endif %}
     */{% endif %}
    @JsonProperty("{{propName}}")
    {%- if propName | isRequired(schema.required()) %}@NotNull{% endif %}
    {%- if prop.deprecated() %}@Deprecated{% endif %}
    {%- if prop.minLength() or prop.maxLength() or prop.maxItems() or prop.minItems() %}@Size({% if prop.minLength() or prop.minItems() %}min = {{prop.minLength()}}{{prop.minItems()}}{% endif %}{% if prop.maxLength() or prop.maxItems() %}{% if prop.minLength() or prop.minItems() %},{% endif %}max = {{prop.maxLength()}}{{prop.maxItems()}}{% endif %}){% endif %}    
    {%- if prop.pattern() %}@Pattern(regexp="{{prop.pattern() | addBackSlashToPattern}}"){% endif %}
    {%- if prop.type() == 'number' and (prop.format() == 'float' or prop.format() == 'double') %}
        {%- if prop.minimum() %}@DecimalMin(value = "{{prop.minimum()}}", inclusive = true){% endif %}{%- if prop.exclusiveMinimum() %}@DecimalMin(value = "{{prop.exclusiveMinimum()}}", inclusive = false){% endif %}
        {%- if prop.maximum() %}@DecimalMax(value = "{{prop.maximum()}}", inclusive = true){% endif %}{%- if prop.exclusiveMaximum() %}@DecimalMax(value = "{{prop.exclusiveMaximum()}}", inclusive = false){% endif %}
    {%- else %}
        {%- if prop.minimum() %}@Min({{prop.minimum()}}){% endif %}{% if prop.exclusiveMinimum() %}@Min({{prop.exclusiveMinimum() + 1}}){% endif %}
        {%- if prop.maximum() %}@Max({{prop.maximum()}}){% endif %}{% if prop.exclusiveMaximum() %}@Max({{prop.exclusiveMaximum() + 1}}){% endif %}
    {%- endif %}
    public {{propType}} get{{className}}() {
        return {{varName}};
    }
{% if prop.deprecated() %}
    @Deprecated
{%- endif %}
    public void set{{className}}({{propType}} {{varName}}) {
        this.{{varName}} = {{varName}};
    }
    {% endfor %}
    {% if params.disableEqualsHashCode === 'false' %}@Override{% set hasProps = schema.properties() | length > 0 %}
    public boolean equals(Object o) {
    {%- if not hasProps %}
        return super.equals(o);
    {% else %}
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        {{schemaName | camelCase | upperFirst}} {{schemaName | camelCase}} = ({{schemaName | camelCase | upperFirst}}) o;
        return {% for propName, prop in schema.properties() %}{% set varName = propName | camelCase %}
            Objects.equals(this.{{varName}}, {{schemaName | camelCase}}.{{varName}}){% if not loop.last %} &&{% else %};{% endif %}{% endfor %}
    {% endif -%}
    }

    @Override
    public int hashCode() {
    {%- if not hasProps %}
        return super.hashCode();
    {% else %}
        return Objects.hash({% for propName, prop in schema.properties() %}{{propName | camelCase}}{% if not loop.last %}, {% endif %}{% endfor %});
    {% endif -%}
    }{% endif %}

    @Override
    public String toString() {
        return "class {{schemaName | camelCase | upperFirst}} {\n" +
        {% for propName, prop in schema.properties() %}{% set varName = propName | camelCase %}
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
