# What to do?
* Support of Kafka is done based on clear "spring-kafka" library without integration.
* Definition for protocols mqtt and amqp could be out of date. 
Please have a look to application.yaml and AmqpConfig.java, MqttConfig.java.
* Tests are not provided.
* Add annotation to the model generation. 
Consider "@Valid", "@ApiModelProperty", "@JsonProperty", "@Size", "@NotNull" e.t.c.
* Parameters for topics are not supported.
* Security schemas are not supported.
* Traits are not supported.
* Json serializer/desirializer is used always, without taking into account real content type.