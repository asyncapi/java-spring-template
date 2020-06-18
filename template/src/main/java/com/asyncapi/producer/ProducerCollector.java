package {{ params['userJavaPackage'] }}.producer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import {{ params['userJavaPackage'] }}.constants.Topic;
{%- if asyncapi | isProtocol('pulsar') -%}
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.Schema;
{%- endif -%}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
{%- if asyncapi | isProtocol('pulsar') -%}
@DependsOn({ "pulsarClient" })
{%- endif -%}
public class ProducerCollector implements BeanPostProcessor {

	private static final Logger log = LoggerFactory.getLogger(ProducerCollector.class);

	{%- if asyncapi | isProtocol('pulsar') -%}
	private final PulsarClient pulsarClient;
	{%- endif -%}

	private final Map<String, Producer<?>> producers = new ConcurrentHashMap<>();

	{%- if asyncapi | isProtocol('pulsar') -%}
	public ProducerCollector(PulsarClient pulsarClient) {
		this.pulsarClient = pulsarClient;
	}
	{%- endif -%}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		final Class<?> beanClass = bean.getClass();
		if (bean instanceof Topic) {
			String channelName = ((Topic) bean).getChannelName();
			Class<?> messageType = ((Topic) bean).getMessageType();
			producers.put(channelName, this.buildProducer(channelName, messageType));
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		return bean;
	}

	private Producer<?> buildProducer(String channelName, Class<?> messageType) {
		{%- if asyncapi | isProtocol('pulsar') -%}
		try {
			return pulsarClient.newProducer(Schema.JSON(messageType))
					.topic(channelName)
					.create();
		} catch (PulsarClientException e) {
			log.error("Exception during producer creation", e);
		}
		{%- endif -%}
		return null;
	}

	Map<String, Producer<?>> getProducers() {
		return producers;
	}
}
