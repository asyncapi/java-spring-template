{% macro mqttConfig(asyncapi, params) %}

import {{params['userJavaPackage']}}.service.MessageHandlerService;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.StringUtils;

import javax.annotation.processing.Generated;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Configuration
public class Config {

    @Value("${mqtt.broker.address}")
    private String address;

    @Value("${mqtt.broker.timeout.connection}")
    private int connectionTimeout;

    @Value("${mqtt.broker.timeout.disconnection}")
    private long disconnectionTimeout;

    @Value("${mqtt.broker.timeout.completion}")
    private long completionTimeout;

    @Value("${mqtt.broker.clientId}")
    private String clientId;

    @Value("${mqtt.broker.username}")
    private String username;

    @Value("${mqtt.broker.password}")
    private String password;

    {% for serverName, server in asyncapi.servers() %}{% if server.protocol() == 'mqtt' and server.binding('mqtt') %}
    {% if server.binding('mqtt').cleanSession | isDefined %}
    @Value("${mqtt.broker.cleanSession}")
    private boolean cleanSession;
    {% endif %}{% if server.binding('mqtt').keepAlive | isDefined %}
    @Value("${mqtt.broker.timeout.keepAlive}")
    private int keepAliveInterval;
    {% endif %}{% if server.binding('mqtt').lastWill %}
    @Value("${mqtt.broker.lastWill.topic}")
    private String lastWillTopic;

    @Value("${mqtt.broker.lastWill.message}")
    private String lastWillMessage;

    @Value("${mqtt.broker.lastWill.qos}")
    private int lastWillQos;

    @Value("${mqtt.broker.lastWill.retain}")
    private boolean lastWillRetain;
    {% endif %}{% endif %}{% endfor %}

    {% for channelName, channel in asyncapi.channels() %}{% if channel.hasPublish() %}
    @Value("${mqtt.topic.{{-channel.publish().id() | camelCase-}}}")
    private String {{channel.publish().id() | camelCase-}}Topic;
    {% elif channel.hasSubscribe() %}
    @Value("${mqtt.topic.{{-channel.subscribe().id() | camelCase-}}}")
    private String {{channel.subscribe().id() | camelCase-}}Topic;
    {% endif %}{% endfor %}

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        {% for serverName, server in asyncapi.servers() %}
        {% if server.protocol() == 'mqtt' and server.binding('mqtt').lastWill %}options.setWill(lastWillTopic, lastWillMessage.getBytes(), lastWillQos, lastWillRetain);{% endif %}
        {% if server.protocol() == 'mqtt' and server.binding('mqtt').cleanSession | isDefined %}options.setCleanSession(cleanSession);{% endif %}
        {% if server.protocol() == 'mqtt' and server.binding('mqtt').keepAlive | isDefined %}options.setKeepAliveInterval(keepAliveInterval);{% endif %}{% endfor %}
        options.setServerURIs(new String[] { address });
        if (!StringUtils.isEmpty(username)) {
            options.setUserName(username);
        }
        if (!StringUtils.isEmpty(password)) {
            options.setPassword(password.toCharArray());
        }
        options.setConnectionTimeout(connectionTimeout);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Autowired
    MessageHandlerService messageHandlerService;

    {% for channelName, channel in asyncapi.channels() %}{% if channel.hasPublish() %}
    @Bean
    public IntegrationFlow {{channel.publish().id() | camelCase}}Flow() {
        return IntegrationFlow.from({{channel.publish().id() | camelCase}}Inbound())
                .handle(messageHandlerService::handle{{channel.publish().id() | camelCase | upperFirst}})
                .get();
    }

    @Bean
    public MessageProducerSupport {{channel.publish().id() | camelCase}}Inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId,
                mqttClientFactory(), {{channel.publish().id() | camelCase}}Topic);
        adapter.setCompletionTimeout(connectionTimeout);
        adapter.setDisconnectCompletionTimeout(disconnectionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        return adapter;
    }
    {% endif %}{% endfor %}

    {% for channelName, channel in asyncapi.channels() %}{% if channel.hasSubscribe() %}
    @Bean
    public MessageChannel {{channel.subscribe().id() | camelCase}}OutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(outputChannel = "{{channel.subscribe().id() | camelCase}}OutboundChannel")
    public MessageHandler {{channel.subscribe().id() | camelCase}}Outbound() {
        MqttPahoMessageHandler pahoMessageHandler = new MqttPahoMessageHandler(clientId, mqttClientFactory());
        pahoMessageHandler.setAsync(true);
        pahoMessageHandler.setCompletionTimeout(completionTimeout);
        pahoMessageHandler.setDisconnectCompletionTimeout(disconnectionTimeout);
        pahoMessageHandler.setDefaultTopic({{channel.subscribe().id() | camelCase}}Topic);
        {% if channel.subscribe().binding('mqtt') and channel.subscribe().binding('mqtt').retain | isDefined %}pahoMessageHandler.setDefaultRetained({{channel.subscribe().binding('mqtt').retain}});{% endif %}
        {% if channel.subscribe().binding('mqtt') and channel.subscribe().binding('mqtt').qos | isDefined %}pahoMessageHandler.setDefaultQos({{channel.subscribe().binding('mqtt').qos}});{% endif %}
        return pahoMessageHandler;
    }
    {% endif %}{% endfor %}

}
{% endmacro %}
