package {{ params['userJavaPackage'] }}.model;

import java.util.Objects;

{% if message.description() or message.examples()%}/**{% for line in message.description() | splitByLines %}
 * {{ line | safe}}{% endfor %}{% if message.examples() %}
 * Examples: {{message.examples() | examplesToString | safe}}{% endif %}
 */{% endif %}
public class {{messageName | camelCase | upperFirst}} {
    {% set payloadName = message.payload().uid() | camelCase | upperFirst %}
    private {{payloadName}} payload;

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