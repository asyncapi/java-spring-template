package com.asyncapi.model;

{% if message.description() or message.examples()%}/**
 * {{message.description() | safe}}{% if message.examples() %}
 * Examples: {{message.examples()}}{% endif %}
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
}