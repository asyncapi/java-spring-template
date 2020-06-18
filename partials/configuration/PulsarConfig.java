{% macro pulsarConfig(asyncapi, params) %}

import {{ params['userJavaPackage'] }}.constants.Topic;

import java.util.concurrent.TimeUnit;

import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan
public class Config {

	{%- for servername, server in asyncapi.servers() %}{% if server.protocol() == 'pulsar' %}
	@Value("${asyncapi.server:{{server.url()}}}")
	{% endif %}{% endfor %}
	private String serverUrl;

	@Value("${asyncapi.ioThreads:10}")
	private int ioThreads;

	@Value("${asyncapi.listenerThreads:10}")
	private int listenerThreads;

	@Value("${asyncapi.enableTcpNoDelay:false}")
	private boolean enableTcpNoDelay;

	@Value("${asyncapi.keepAliveInterval:20}")
	private int keepAliveInterval;

	@Value("${asyncapi.connectionTimeout:10}")
	private int connectionTimeout;

	@Value("${asyncapi.operationTimeout:15}")
	private int operationTimeout;

	@Value("${asyncapi.startingBackoffInterval:100}")
	private int startingBackoffInterval;

	@Value("${asyncapi.maxBackoffInterval:10}")
	private int maxBackoffInterval;

	@Bean
	public PulsarClient pulsarClient() throws PulsarClientException {
		return PulsarClient.builder()
						.serviceUrl(serverUrl)
						.ioThreads(ioThreads)
						.listenerThreads(listenerThreads)
						.enableTcpNoDelay(enableTcpNoDelay)
						.keepAliveInterval(keepAliveInterval, TimeUnit.SECONDS)
						.connectionTimeout(connectionTimeout, TimeUnit.SECONDS)
						.operationTimeout(operationTimeout, TimeUnit.SECONDS)
						.startingBackoffInterval(startingBackoffInterval, TimeUnit.MILLISECONDS)
						.maxBackoffInterval(maxBackoffInterval, TimeUnit.SECONDS)
						.build();
	}

	{% for channelName, channel in asyncapi.channels() %}{% if channel.hasPublish() %}
	@Bean
	public Topic {{channelName | camelCase}}() {
		return Topic.{{channelName | camelCase | upperFirst}};
	}
	{% endif %}{% endfor %}
}
{% endmacro %}
