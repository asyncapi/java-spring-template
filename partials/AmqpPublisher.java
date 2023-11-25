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
    {%- set schemaName = channel.subscribe().message().payload().uid() | camelCase | upperFirst %}
    void {{channel.subscribe().id() | camelCase}}();

    {% endif %}
    {% endfor %}

}

{% endmacro %}