![AsyncAPI Java Spring Template](assets/github-repobanner-javaspringtemp.png)

Java Spring template for the [AsyncAPI Generator](https://github.com/asyncapi/generator).

---
[![License](https://img.shields.io/github/license/asyncapi/java-spring-template)](https://github.com/asyncapi/java-spring-template/blob/master/LICENSE)
[![npm](https://img.shields.io/npm/v/@asyncapi/java-spring-template?style=flat-square)](https://www.npmjs.com/package/@asyncapi/java-spring-template)<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section -->
[![All Contributors](https://img.shields.io/badge/all_contributors-3-orange.svg?style=flat-square)](#contributors-)<!-- ALL-CONTRIBUTORS-BADGE:END -->
![downloads](https://img.shields.io/npm/dm/@asyncapi/java-spring-template?style=flat-square)
---

<!-- toc is generated with GitHub Actions do not remove toc markers -->

<!-- toc -->

- [Attention, AsyncAPI v3 is not currently supported by this template](#attention-asyncapi-v3-is-not-currently-supported-by-this-template)
- [Usage](#usage)
  * [AsyncAPI definitions](#asyncapi-definitions)
  * [Supported parameters](#supported-parameters)
  * [Examples](#examples)
- [Run it](#run-it)
- [Development](#development)
  * [Missing features](#missing-features)
- [Contributors ✨](#contributors-%E2%9C%A8)

<!-- tocstop -->

----
## Attention, AsyncAPI v3 is not currently supported by this template


----

## Usage

Install AsyncAPI CLI, for details follow the [guide](https://www.asyncapi.com/tools/cli).

```bash
npm install -g @asyncapi/cli
```

Generate using CLI.

```bash
asyncapi generate fromTemplate <asyncapi.yaml> @asyncapi/java-spring-template
```

You can replace `<asyncapi.yaml>` with local path or URL pointing to [any AsyncAPI document](https://raw.githubusercontent.com/asyncapi/java-spring-template/master/tests/mocks/kafka.yml).

### AsyncAPI definitions
To have correctly generated code, your AsyncAPI file MUST define `operationId` for every operation.

In order for the generator to know what names to use for some parameters [AsyncAPI specification bindings](https://www.asyncapi.com/docs/reference/specification/v2.0.0#operationBindingsObject) SHOULD be used.

It is RECOMMENDED to not use anonymous objects in payload and components definition, if changing of data model is not possible, you MAY use `$id` to set name of element.

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

### Supported parameters

|Name|Description| Required | Default                  |
|---|---|----------|--------------------------|
|disableEqualsHashCode|Disable generation of equals and hashCode methods for model classes.| No       | `false`                  |
|inverseOperations|Generate an application that will publish messages to `publish` operation of channels and read messages from `subscribe` operation of channels. Literally this flag will simply swap `publish` and `subscribe` operations in the channels. <br> This flag will be useful when you want to generate a code of mock for your main application. Be aware, generation could be incomplete and manual changes will be required e.g. if bindings are defined only for case of main application.| No       | `false`                  |
|javaPackage|The Java package of the generated classes. Alternatively you can set the specification extension `info.x-java-package`. If both extension and parameter are used, parameter has more priority.| No       | `com.asyncapi`           |
|springBoot2|Generate template files for the Spring Boot version 2. For kafka protocol it will also force to use spring-kafka 2.9.9| No       | `false`                    |
|maven|Generate pom.xml Maven build file instead of Gradle build.|No       | `false`                  |
|listenerPollTimeout|Only for Kafka. Timeout in ms to use when polling the consumer.| No       | `3000`                   |
|listenerConcurrency|Only for Kafka. Number of threads to run in the listener containers.| No       | `3`                      |
|addTypeInfoHeader|Only for Kafka. Add type information to message header.| No       | `true`                   |
|connectionTimeout|Only for MQTT. This value, measured in seconds, defines the maximum time interval the client will wait for the network connection to the MQTT server to be established. The default timeout is 30 seconds. A value of 0 disables timeout processing meaning the client will wait until the network connection is made successfully or fails.| No       | `30`                     |
|disconnectionTimeout|Only for MQTT. The completion timeout in milliseconds when disconnecting. The default disconnect completion timeout is 5000 milliseconds.| No       | `5000`                   |
|completionTimeout|Only for MQTT. The completion timeout in milliseconds for operations. The default completion timeout is 30000 milliseconds.| No       | `30000`                  |
|mqttClientId| Only for MQTT. Provides the client identifier for the MQTT server. This parameter overrides the value of the clientId if it's set in the AsyncAPI file.If both aren't provided, a default value is set.| No       |                          |
|asyncapiFileDir| Path where original AsyncAPI file will be stored.| No       | `src/main/resources/api/` |
### Examples

The shortest possible syntax:
```bash
asyncapi generate fromTemplate asyncapi.yaml @asyncapi/java-spring-template
```

Specify where to put the result with `-o` option and define parameter of poll timeout with `-p` option:
```bash
asyncapi generate fromTemplate asyncapi.yaml @asyncapi/java-spring-template -o ./src -p listenerPollTimeout=5000
```
## Run it

Go to the root folder of the generated code and run this command (you need the JDK 17):
```bash
./gradlew bootRun
```

## Development

1. Clone the repository:
   ```sh
   git clone https://github.com/asyncapi/java-spring-template
   cd java-spring-template
   ```
1. Download all template dependencies:
   ```sh
   npm install
   ```
1. Make required changes in the template.
2. Run snapshot tests:
   ```sh
   npm test
   ```
   If there falling tests examine diff report and make an appropriate changes in template files or snapshots.
1. Check output generation project. Install AsyncAPI Generator:
   ```
   npm install -g @asyncapi/cli
   ```
1. Run generation (assuming you are in template folder):

   ```bash
   # for MQTT protocol test with below
   asyncapi generate fromTemplate tests/mocks/mqtt.yml ./ -o output
   # for Kafka protocol test with below
   asyncapi generate fromTemplate tests/mocks/kafka.yml ./ -o output
   ```
1. Explore generated files in `output` directory. Generated project shouldn't contain syntax or compilation errors. 
Preferably generated tests should pass.

> For local development, you need different variations of this command. First of all, you need to know about three important CLI flags:
- `--debug` enables the debug mode. 
- `--watch-template` enables a watcher of changes that you make in the template. It regenerates your template whenever it detects a change.
- `--install` enforces reinstallation of the template.

### Missing features

See the list of features that are still missing in the component:

- [ ] support of Kafka is done based on clear "spring-kafka" library without integration like for mqtt or amqp
- [x] generated code for protocol `amqp` could be out of date. Please have a look to [application.yaml](template/src/main/resources/application.yml) and [AmqpConfig.java](partials/AmqpConfig.java) 
- [ ] tests for protocol `amqp` are not provided
- [x] [`parameters`](https://github.com/asyncapi/spec/blob/2.0.0/versions/2.0.0/asyncapi.md#parametersObject) for topics are not supported
- [ ] [`server variables`](https://github.com/asyncapi/spec/blob/2.0.0/versions/2.0.0/asyncapi.md#serverVariableObject) are not entirely supported 
- [ ] [`security schemas`](https://github.com/asyncapi/spec/blob/2.0.0/versions/2.0.0/asyncapi.md#securitySchemeObject) are not supported
- [x] [`traits`](https://github.com/asyncapi/spec/blob/2.0.0/versions/2.0.0/asyncapi.md#operationTraitObject) are not supported
- [ ] Json serializer/deserializer is used always, without taking into account real [`content type`](https://github.com/asyncapi/spec/blob/2.0.0/versions/2.0.0/asyncapi.md#default-content-type)
- [ ] template generation of docker-compose depending on protocol of server, now the rabbitmq is hardcoded

If you want to help us develop them, feel free to contribute.

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://github.com/Tenischev"><img src="https://avatars1.githubusercontent.com/u/4137916?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Semen</b></sub></a><br /><a href="#maintenance-Tenischev" title="Maintenance">🚧</a><a href="https://github.com/asyncapi/java-spring-template/commits?author=Tenischev" title="Documentation">📖</a> <a href="https://github.com/asyncapi/java-spring-template/commits?author=Tenischev" title="Code">💻</a><a href="https://github.com/asyncapi/java-spring-template/issues?q=author%3ATenischev" title="Bug reports">🐛</a><a href="https://github.com/asyncapi/java-spring-template/pulls?q=is%3Apr+reviewed-by%3ATenischev" title="Reviewed Pull Requests">👀</a><a href="https://github.com/asyncapi/java-spring-template/commits?author=Tenischev" title="Tests">⚠️</a></td>
    <td align="center"><a href="https://www.linkedin.com/in/francesconobilia/"><img src="https://avatars1.githubusercontent.com/u/10063590?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Francesco Nobilia</b></sub></a><br /><a href="https://github.com/asyncapi/java-spring-template/pulls?q=is%3Apr+reviewed-by%3Afnobilia" title="Reviewed Pull Requests">👀</a></td>
    <td align="center"><a href="https://www.linkedin.com/in/derberg/"><img src="https://avatars.githubusercontent.com/u/6995927?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Lukasz Gornicki</b></sub></a><br /><a href="https://github.com/asyncapi/java-spring-template/pulls?q=is%3Apr+reviewed-by%3Aderberg" title="Reviewed Pull Requests">👀</a></td>
    <td align="center"><a href="http://www.amrutprabhu.com"><img src="https://avatars.githubusercontent.com/u/8725949?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Amrut Prabhu</b></sub></a><br /><a href="https://github.com/asyncapi/java-spring-template/commits?author=amrutprabhu" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/VaishnaviNandakumar"><img src="https://avatars.githubusercontent.com/u/41518119?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Vaishnavi Nandakumar</b></sub></a><br /><a href="https://github.com/asyncapi/java-spring-template/commits?author=VaishnaviNandakumar" title="Code">💻</a></td>
    <td align="center"><a href="https://github.com/taotao100"><img src="https://avatars.githubusercontent.com/u/7056867?v=4?s=100" width="100px;" alt=""/><br /><sub><b>taotao100</b></sub></a><br /><a href="https://github.com/asyncapi/java-spring-template/issues?q=is%3Aissue+author%3Ataotao100" title="Bug reports">🐛</a>
    <td align="center"><a href="https://github.com/jbiscella"><img src="https://avatars.githubusercontent.com/u/7963565?v=4?s=100" width="100px;" alt=""/><br /><sub><b>Jacopo Biscella</b></sub></a><br /><a href="https://github.com/asyncapi/java-spring-template/issues?q=is%3Aissue+author%3Ajbiscella" title="Bug reports">🐛</a>
  </tr>
</table>

<!-- markdownlint-restore -->
<!-- prettier-ignore-end -->

<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!
