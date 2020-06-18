package {{ params['userJavaPackage'] }}.producer;

import {{ params['userJavaPackage'] }}.constants.Topic;
{%- if asyncapi | isProtocol('pulsar') -%}
import org.apache.pulsar.client.api.MessageId;
import org.apache.pulsar.client.api.PulsarClientException;
{%- endif -%}
{% for channelName, channel in asyncapi.channels() %}{% if channel.hasPublish() %}
{% set varName = channel.publish().message().payload().uid() | camelCase | upperFirst %}
import {{ params['userJavaPackage'] }}.model.{{varName}};
{% endif %}{% endfor %}
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Producer {

	private static final Logger log = LoggerFactory.getLogger(Producer.class);

	private final ProducerCollector producerCollector;

	public Producer(ProducerCollector producerCollector) {
		this.producerCollector = producerCollector;
	}

	{% for channelName, channel in asyncapi.channels() %}{% if channel.hasPublish() %}
	{% set varName = channel.publish().message().payload().uid() | camelCase | upperFirst %}
	public MessageId send{{channelName | camelCase | upperFirst}}({{varName}} message) {%- if asyncapi | isProtocol('pulsar') -%}throws PulsarClientException{%- endif -%} {
		String channelName = Topic.{{channelName | camelCase | upperFirst}}.getChannelName();
		{%- if asyncapi | isProtocol('pulsar') -%}
		org.apache.pulsar.client.api.Producer<{{varName}}> producer = (org.apache.pulsar.client.api.Producer<{{varName}}>) producerCollector.getProducers().get(channelName);
		if (producer != null) {
			return producer.send(message);
		} else {
			log.error("No fitting producer found");
		}
		{%- endif -%}
		return null;
	}
	{% endif %}{% endfor %}
}
