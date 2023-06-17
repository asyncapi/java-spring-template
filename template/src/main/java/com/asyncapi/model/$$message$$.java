package {{ params['userJavaPackage'] }}.model;

{% if params.springBoot2 -%}
import javax.validation.Valid;
{% else %}
import jakarta.validation.Valid;
{%- endif %}
import java.util.Objects;
import java.util.List;

{% if message.description() or message.examples()%}/**{% for line in message.description() | splitByLines %}
 * {{ line | safe}}{% endfor %}{% if message.examples() %}
 * Examples: {{message.examples() | examplesToString | safe}}{% endif %}
 */{% endif %}
public class {{messageName | camelCase | upperFirst}} {
    {%- if message.payload().anyOf() or message.payload().oneOf() %}
        {%- set payloadName = 'OneOf' %}{%- set hasPrimitive = false %}
        {%- for obj in message.payload().anyOf() %}
            {%- set hasPrimitive = hasPrimitive or obj.type() !== 'object' %}
            {%- set payloadName = payloadName + obj.uid() | camelCase | upperFirst %}
        {%- endfor %}
        {%- for obj in message.payload().oneOf() %}
            {%- set hasPrimitive = hasPrimitive or obj.type() !== 'object' %}
            {%- set payloadName = payloadName + obj.uid() | camelCase | upperFirst %}
        {%- endfor %}
        {%- if hasPrimitive %}
            {%- set payloadName = 'Object' %}
        {%- else %}
    public interface {{payloadName}} {

    }
        {%- endif %}
    {%- elif message.payload().allOf() %}
        {%- set payloadName = 'AllOf' %}
        {%- for obj in message.payload().allOf() %}
            {%- set payloadName = payloadName + obj.uid() | camelCase | upperFirst %}
        {%- endfor %}
    public class {{payloadName}} {
        {%- for obj in message.payload().allOf() %}
            {%- set varName = obj.uid() | camelCase %}
            {%- set className = obj.uid() | camelCase | upperFirst %}
            {%- set propType = obj | defineType(obj.uid()) | safe %}

            {%- if obj.type() === 'array' %}
                {%- set varName = obj.uid() | camelCase + 'List' %}
            {%- endif %}
        private @Valid {{propType}} {{varName}};

        public {{propType}} get{{className}}() {
            return {{varName}};
        }

        public void set{{className}}({{propType}} {{varName}}) {
            this.{{varName}} = {{varName}};
        }
        {%- endfor %}
    }
    {% else %}
        {%- set payloadName = message.payload().uid() | camelCase | upperFirst %}
    {%- endif %}
    private @Valid {{payloadName}} payload;

    public {{payloadName}} getPayload() {
        return payload;
    }

    public void setPayload({{payloadName}} payload) {
        this.payload = payload;
    }

    {% if params.disableEqualsHashCode === 'false' %}@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        {{messageName | camelCase | upperFirst}} event = ({{messageName | camelCase | upperFirst}}) o;
        return Objects.equals(this.payload, event.payload);
    }

    @Override
    public int hashCode() {
        return Objects.hash(payload);
    }{% endif %}

    @Override
    public String toString() {
        return "class {{messageName | camelCase | upperFirst}} {\n" +
                "    payload: " + toIndentedString(payload) + "\n" +
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