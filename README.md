# Java Spring generator
<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-3-orange.svg?style=flat-square)](#contributors-)
<!-- ALL-CONTRIBUTORS-BADGE:END -->
_Use your AsyncAPI definition to generate java code to subscribe and publish messages_


## Usage

### AsyncAPI definitions
To have correctly generated code, your AsyncAPI file MUST define `operationId` for every operation.

In order for the generator to know what names to use for some parameters it's necessary to make use of [AsyncAPI specification bindings](https://www.asyncapi.com/docs/specifications/2.0.0/#operationBindingsObject). 


- Complete example for Kafka is [here](tests/mocks/kafka.yml). Notice information about binding.
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
- Complete example for MQTT is [here](tests/mocks/mqtt.yml).


### From the command-line interface (CLI)

```bash
  Usage: ag [options] <asyncapi> @asyncapi/java-spring-template

  Options:

    -V, --version                 output the version number
    -o, --output <outputDir>       directory where to put the generated files (defaults to current directory)
    -p, --param <name=value>       additional param to pass to templates
    -h, --help                    output usage information
```

#### Supported parameters

|Name|Description| Required | Default                  |
|---|---|----------|--------------------------|
|disableEqualsHashCode|Disable generation of equals and hashCode methods for model classes.| No       | `false`                  |
|inverseOperations|Generate an application that will publish messages to `publish` operation of channels and read messages from `subscribe` operation of channels. Literally this flag will simply swap `publish` and `subscribe` operations in the channels. <br> This flag will be useful when you want to generate a code of mock for your main application. Be aware, generation could be incomplete and manual changes will be required e.g. if bindings are defined only for case of main application.| No       | `false`                  |
|javaPackage|The Java package of the generated classes. Alternatively you can set the specification extension `info.x-java-package`. If both extension and parameter are used, parameter has more priority.| No       | `com.asyncapi`           |
|springBoot2|Generate template files for the Spring Boot version 2. For kafka protocol it will also force to use spring-kafka 2.9.9| No       | `false`                    |
|listenerPollTimeout|Only for Kafka. Timeout in ms to use when polling the consumer.| No       | `3000`                   |
|listenerConcurrency|Only for Kafka. Number of threads to run in the listener containers.| No       | `3`                      |
|addTypeInfoHeader|Only for Kafka. Add type information to message header.| No       | `true`                   |
|connectionTimeout|Only for MQTT. This value, measured in seconds, defines the maximum time interval the client will wait for the network connection to the MQTT server to be established. The default timeout is 30 seconds. A value of 0 disables timeout processing meaning the client will wait until the network connection is made successfully or fails.| No       | `30`                     |
|disconnectionTimeout|Only for MQTT. The completion timeout in milliseconds when disconnecting. The default disconnect completion timeout is 5000 milliseconds.| No       | `5000`                   |
|completionTimeout|Only for MQTT. The completion timeout in milliseconds for operations. The default completion timeout is 30000 milliseconds.| No       | `30000`                  |
|mqttClientId| Only for MQTT. Provides the client identifier for the MQTT server. This parameter overrides the value of the clientId if it's set in the AsyncAPI file.If both aren't provided, a default value is set.| No       |                          |
|asyncapiFileDir| Path where original AsyncAPI file will be stored.| No       | `src/main/resources/api/` |
#### Examples

The shortest possible syntax:
```bash
ag asyncapi.yaml @asyncapi/java-spring-template
```

Specify where to put the result and define poll timeout:
```bash
ag -o ./src asyncapi.yaml -p listenerPollTimeout=5000 @asyncapi/java-spring-template
```

If you don't have the AsyncAPI Generator installed, you can install it like this:

```
npm install -g @asyncapi/generator
```

## Development

1. Clone the repository:
   ```
   git clone https://github.com/asyncapi/java-spring-template
   cd java-spring-template
   ```
1. Make sure template has all the dependencies:
   ```
   npm install
   ```
1. Install AsyncAPI Generator:
   ```
   npm install -g @asyncapi/generator
   ```
1. Run generation:

   ```bash
   # for MQTT protocol test with below
   ag tests/mocks/mqtt.yml ./ --output output
   # for Kafka protocol test with below
   ag tests/mocks/kafka.yml ./ --output output
   ```
1. Explore generated files in `output` directory

> For local development, you need different variations of this command. First of all, you need to know about three important CLI flags:
- `--debug` enables the debug mode. 
- `--watch-template` enables a watcher of changes that you make in the template. It regenerates your template whenever it detects a change.
- `--install` enforces reinstallation of the template.

## Run it

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
- [ ] generated code for protocol `amqp` could be out of date. Please have a look to [application.yaml](template/src/main/resources/application.yml) and [AmqpConfig.java](partials/AmqpConfig.java) 
- [ ] tests for protocol `amqp` are not provided
- [x] add annotation to the [model generation](template/src/main/java/com/asyncapi/model). Consider "@Valid", "@JsonProperty", "@Size", "@NotNull" e.t.c.
- [ ] [`parameters`](https://github.com/asyncapi/spec/blob/2.0.0/versions/2.0.0/asyncapi.md#parametersObject) for topics are not supported
- [ ] [`server variables`](https://github.com/asyncapi/spec/blob/2.0.0/versions/2.0.0/asyncapi.md#serverVariableObject) are not entirely supported 
- [ ] [`security schemas`](https://github.com/asyncapi/spec/blob/2.0.0/versions/2.0.0/asyncapi.md#securitySchemeObject) are not supported
- [ ] [`traits`](https://github.com/asyncapi/spec/blob/2.0.0/versions/2.0.0/asyncapi.md#operationTraitObject) are not supported
- [ ] Json serializer/deserializer is used always, without taking into account real [`content type`](https://github.com/asyncapi/spec/blob/2.0.0/versions/2.0.0/asyncapi.md#default-content-type)
- [x] client side generation mode (in general just flip subscribe and publish channels)
- [ ] template generation of docker-compose depending on protocol of server, now the rabbitmq is hardcoded

If you want to help us develop them, feel free to contribute.

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/Tenischev"><img src="https://avatars1.githubusercontent.com/u/4137916?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Semen</b></sub></a><br /><a href="https://github.com/asyncapi/java-spring-template/commits?author=Tenischev" title="Documentation">📖</a> <a href="https://github.com/asyncapi/java-spring-template/commits?author=Tenischev" title="Code">💻</a></td>
    <td align="center"><a href="https://www.linkedin.com/in/francesconobilia/"><img src="https://avatars1.githubusercontent.com/u/10063590?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Francesco Nobilia</b></sub></a><br /><a href="https://github.com/asyncapi/java-spring-template/pulls?q=is%3Apr+reviewed-by%3Afnobilia" title="Reviewed Pull Requests">👀</a></td>
    <td align="center"><a href="http://www.amrutprabhu.com"><img src="https://avatars.githubusercontent.com/u/8725949?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Amrut Prabhu</b></sub></a><br /><a href="https://github.com/asyncapi/java-spring-template/commits?author=amrutprabhu" title="Code">💻</a></td>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
