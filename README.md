# Java Spring generator
_Use your AsyncAPI definition to generate java code to subscribe and publish messages_


## Usage

### AsyncAPI definitions
In order for the generator to know what names to use for some methods it's necessary to make use of [AsyncAPI specification bindings](https://www.asyncapi.com/docs/specifications/2.0.0/#operationBindingsObject). 
here is an example of how to use it:
```yml
channels:
  event.lighting.measured:
    publish:
      bindings:
        kafka:
          groupId: my-group
      message:
         $ref: '#/components/messages/lightMeasured'
    subscribe:
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
      bindings:
        kafka:
          groupId: my-group
      message:
        $ref: '#/components/messages/lightMeasured'
    subscribe:
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


Generated source contains RabbitMQ docker-compose. So you could use it to test amqp with:
```bash
docker-compose -f src/main/docker/rabbitmq.yml up -d
```

### Missing features

See the list of features that are still missing in the component:

- [ ] support of Kafka is done based on clear "spring-kafka" library without integration like for mqtt or amqp
- [ ] generated code for protocols mqtt and amqp could be out of date. Please have a look to [application.yaml](template/src/main/resources/application.yml) and [AmqpConfig.java](partials/AmqpConfig.java), [MqttConfig.java](partials/MqttConfig.java) 
- [ ] tests are not provided
- [x] add annotation to the [model generation](template/src/main/java/com/asyncapi/model). Consider "@Valid", "@JsonProperty", "@Size", "@NotNull" e.t.c.
- [ ] [`parameters`](https://github.com/asyncapi/asyncapi/blob/master/versions/2.0.0/asyncapi.md#parametersObject) for topics are not supported
- [ ] [`server variables`](https://github.com/asyncapi/asyncapi/blob/master/versions/2.0.0/asyncapi.md#serverVariableObject) are not entirely supported 
- [ ] [`security schemas`](https://github.com/asyncapi/asyncapi/blob/master/versions/2.0.0/asyncapi.md#securitySchemeObject) are not supported
- [ ] [`traits`](https://github.com/asyncapi/asyncapi/blob/master/versions/2.0.0/asyncapi.md#operationTraitObject) are not supported
- [ ] Json serializer/desirializer is used always, without taking into account real [`content type`](https://github.com/asyncapi/asyncapi/blob/master/versions/2.0.0/asyncapi.md#default-content-type)
- [ ] client side generation mode (in general just flip subscribe and publish channels)
- [ ] template generation of docker-compose depending on protocol of server, now the rabbitmq is hardcoded

If you want to help us develop them, feel free to contribute.
