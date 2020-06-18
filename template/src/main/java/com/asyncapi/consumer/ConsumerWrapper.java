package {{ params['userJavaPackage'] }}.consumer;

import java.lang.reflect.Method;

import {{ params['userJavaPackage'] }}.constants.Topic;

/**
 * Wrapper class to bundle everything related to one consumer instance.
 */
public class ConsumerWrapper {

	/**
	 * The topic to the messages that are being consumed
	 */
	private final Topic topic;
	/**
	 * The name of the consumer
	 */
	private final String name;
	/**
	 * The method handling the consumption of messages
	 */
	private final Method handler;
	/**
	 * The bean which the handler method belongs to
	 */
	private final Object bean;

	ConsumerWrapper(Topic topic, String name, Method handler, Object bean) {
		this.topic = topic;
		this.name = name;
		this.handler = handler;
		this.bean = bean;
	}

	public Topic getTopic(){
		return topic;
	}

	public String getName(){
		return name;
	}

	public Method getHandler(){
		return handler;
	}

	public Object getBean(){
		return bean;
	}
}
