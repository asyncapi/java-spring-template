package {{ params['userJavaPackage'] }}.consumer;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

{%- if asyncapi | isProtocol('pulsar') -%}
import org.apache.pulsar.client.api.Consumer;
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

public class ConsumerCollector implements BeanPostProcessor {

	private static final Logger log = LoggerFactory.getLogger(ConsumerCollector.class);

	{%- if asyncapi | isProtocol('pulsar') -%}
	private final PulsarClient pulsarClient;
	{%- endif -%}

	private List<Consumer<?>> consumers = new LinkedList<Consumer<?>>();

	{%- if asyncapi | isProtocol('pulsar') -%}
	public ConsumerCollector(PulsarClient pulsarClient) {
		this.pulsarClient = pulsarClient;
	}
	{%- endif -%}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		final Class<?> beanClass = bean.getClass();
		for (Method method : beanClass.getDeclaredMethods()) {
			if (method.isAnnotationPresent({{ params['userJavaPackage'] }}.annotations.Consumer.class)) {
				ConsumerWrapper wrapper = new ConsumerWrapper(
						method.getAnnotation({{ params['userJavaPackage'] }}.annotations.Consumer.class).topic(),
						beanClass.getName() + "#" + method.getName(),
						method,
						bean);
				consumers.add(this.subscribe(wrapper));
			}
		}
		return bean;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		return bean;
	}

	private Consumer<?> subscribe(ConsumerWrapper consumerWrapper) {
		{%- if asyncapi | isProtocol('pulsar') -%}
		try {
			return pulsarClient
					.newConsumer(Schema.JSON(consumerWrapper.getTopic().getMessageType()))
					.consumerName("consumer-" + consumerWrapper.getName())
					.subscriptionName("subscription-" + consumerWrapper.getName())
					.topic(consumerWrapper.getTopic().getChannelName())
					.messageListener((consumer, msg) -> {
						try {
							final Method method = consumerWrapper.getHandler();

							method.setAccessible(true);
							method.invoke(consumerWrapper.getBean(), msg.getValue());

							consumer.acknowledge(msg);
						} catch (Exception e) {
							consumer.negativeAcknowledge(msg);
							log.error("Exception during message handling", e);
						}
					}).subscribe();
		} catch (PulsarClientException e) {
			log.error("Exception during consumer creation", e);
			return null;
		}
		{%- endif -%}
	}

	public List<Consumer<?>> getConsumers() {
		return consumers;
	}

}
