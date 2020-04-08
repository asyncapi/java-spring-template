{% from "partials/AmqpConfig.java" import amqpConfig %}
{% from "partials/MqttConfig.java" import mqttConfig %}

{% if asyncapi.hasServers() %}
    {% for serverName, server in asyncapi.servers() %}
      {% if server.protocol() === 'amqp' %}
{{ amqpConfig(asyncapi) }}
      {% endif %}
      {% if server.protocol() === 'mqtt' %}
{{ mqttConfig(asyncapi) }}
      {% endif %}
    {% endfor %}
{% endif %}