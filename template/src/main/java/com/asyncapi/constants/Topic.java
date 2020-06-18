package {{ params['userJavaPackage'] }}.constants;

{% for channelName, channel in asyncapi.channels() %}
{% if channel.hasPublish() %}
{% set varName = channel.publish().message().payload().uid() | camelCase | upperFirst %}
import {{ params['userJavaPackage'] }}.model.{{varName}};
{% else %}
{%- if channel.hasSubscribe() -%}
{% set varName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %}
import {{ params['userJavaPackage'] }}.model.{{varName}};
{%- endif -%}
{% endif %}
{% endfor %}

/**
 * Enum to match channel names with message types.
 */
public enum Topic {

	{% for channelName, channel in asyncapi.channels() %}
	{% if channel.hasPublish() %}
	{% set varName = channel.publish().message().payload().uid() | camelCase | upperFirst %}
	{{channelName | camelCase | upperFirst}}({{varName}}.class, "{{channelName}}");
	{% else %}
	{%- if channel.hasSubscribe() -%}
	{% set varName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %}
	{{channelName | camelCase | upperFirst}}({{varName}}.class, "{{channelName}}");
	{%- endif -%}
	{% endif %}
	{% endfor %}

	private final Class<?> messageType;

	private final String channelName;

	private Topic(Class<?> messageType, String channelName) {
		this.messageType = messageType;
		this.channelName = channelName;
	}

	public Class<?> getMessageType(){
		return messageType;
	}

	public String getChannelName(){
		return channelName;
	}

}
