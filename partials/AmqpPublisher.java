{% macro amqpPublisher(asyncapi, params) %}

{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
        {%- for message in channel.subscribe().messages() %}
import {{params['userJavaPackage']}}.model.{{message.payload().uid() | camelCase | upperFirst}};
        {%- endfor -%}
    {% endif -%}
{% endfor %}
import javax.annotation.processing.Generated;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
public interface PublisherService {

    {% for channelName, channel in asyncapi.channels() %}
    {% if channel.hasSubscribe() %}
        {%- if channel.subscribe().hasMultipleMessages() %}
            {%- set varName = "object" %}
        {%- else %}
            {%- set varName = channel.subscribe().message().payload().uid() | camelCase %}
        {%- endif %}
    {% if channel.description() or channel.subscribe().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.subscribe().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    void {{channel.subscribe().id() | camelCase}}({{varName | upperFirst}} payload);
    {%- if channel.hasParameters() %}
    void {{channel.subscribe().id() | camelCase}}({%- for parameterName, parameter in channel.parameters() %}String {{parameterName}}, {% endfor %}{{varName | upperFirst}} payload);
    {% endif %}
    {% endif %}
    {% endfor %}

}

{% endmacro %}