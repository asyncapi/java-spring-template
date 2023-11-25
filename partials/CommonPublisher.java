{% macro commonPublisher(asyncapi, params) %}

{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
        {%- for message in channel.subscribe().messages() %}
import {{params['userJavaPackage']}}.model.{{message.payload().uid() | camelCase | upperFirst}};
        {%- endfor -%}
    {% endif -%}
    {%- if channel.hasParameters() %}
        {%- for parameterName, parameter in channel.parameters() %}
            {%- if parameter.schema().type() === 'object' %}
import {{params['userJavaPackage']}}.model.{{parameterName | camelCase | upperFirst}};
            {%- endif %}
        {%- endfor -%}
    {% endif -%}
{% endfor %}
import javax.annotation.processing.Generated;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
public interface PublisherService {

    {% for channelName, channel in asyncapi.channels() %}
        {%- if channel.hasSubscribe() %}
            {%- if channel.subscribe().hasMultipleMessages() %}
                {%- set varName = "object" %}
            {%- else %}
                {%- set varName = channel.subscribe().message().payload().uid() | camelCase %}
            {%- endif %}
    {% if channel.description() or channel.subscribe().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.subscribe().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}{% set hasParameters = channel.hasParameters() %}
    void {{channel.subscribe().id() | camelCase}}({{varName | upperFirst}} {{varName}}{% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}, {% if parameter.schema().type() === 'object'%}{{parameterName | camelCase | upperFirst}}{% else %}{{parameter.schema().type() | toJavaType(false)}}{% endif %} {{parameterName | camelCase}}{% endfor %}{% endif %});
        {% endif %}
    {% endfor %}
}
{% endmacro %}