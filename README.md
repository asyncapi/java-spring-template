#Java Spring generator
_Use your AsyncAPI definition to generate java code to subscribe and publish messages_


# ATTENTION: This template is not complete. Please help us update it.
Find more information in [Contribution](docs/contribution.md).
## Usage

### AsyncAPI definitions
In order for the generator to know what names to use for some methods it's necessary to make use of [AsyncAPI specification bindings](https://www.asyncapi.com/docs/specifications/2.0.0/#operationBindingsObject). 
here is an example of how to use it:
```yml
channels:
  event.lighting.measured:
    publish:
      message:
         $ref: '#/components/messages/lightMeasured'
    subscribe:
      bindings:
        kafka:
          groupId: my-group
      message:
        $ref: '#/components/messages/lightMeasured'
```
here is a complete example
```yml
asyncapi: '2.0.0'
info:
  title: Streetlights API
  version: '1.0.0'
  description: |
    The Smartylighting Streetlights API allows you
    to remotely manage the city lights.
  license:
    name: Apache 2.0
    url: 'https://www.apache.org/licenses/LICENSE-2.0'

servers:
  production:
    url: api.streetlights.smartylighting.com:{port}
    protocol: mqtt
    variables:
      port:
        default: '1883'
        enum:
          - '1883'
          - '8883'

channels:
  event.lighting.measured:
    publish:
      message:
        $ref: '#/components/messages/lightMeasured'
    subscribe:
      bindings:
        kafka:
          groupId: my-group
      message:
        $ref: '#/components/messages/lightMeasured'
components:
  messages:
    lightMeasured:
      summary: Inform about environmental lighting conditions for a particular streetlight.
      payload:
        $ref: "#/components/schemas/lightMeasuredPayload"
  schemas:
    lightMeasuredPayload:
      type: object
      properties:
        lumens:
          type: integer
          minimum: 0
          description: Light intensity measured in lumens.
        sentAt:
          $ref: "#/components/schemas/sentAt"
    sentAt:
      type: string
      format: date-time
      description: Date and time when the message was sent.
```
### From the command-line interface (CLI)

```bash
  Usage: ag [options] <asyncapi> @asyncapi/java-spring-template

  Options:

    -V, --version                 output the version number
    -t, --templates <templateDir> directory where templates are located (defaults to internal templates directory)
    -h, --help                    output usage information
```

#### Examples

The shortest possible syntax:
```bash
ag asyncapi.yaml @asyncapi/java-spring-template
```

Specify where to put the result:
```bash
ag -o ./src asyncapi.yaml @asyncapi/java-spring-template
```

### Run it

Go to the root folder of the generated code and run this command (you need the JDK1.8):
```bash
./gradlew bootRun
```

_Don't tested:_

{% if asyncapi.servers() | schemeExists('amqp') %}
Start your RabbitMQ with:
```bash
docker-compose -f src/main/docker/rabbitmq.yml up -d
```
{% endif %}
