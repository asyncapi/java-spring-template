{% macro wsPublisherImpl(asyncapi, params) %}

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
        {%- for message in channel.subscribe().messages() %}
import {{params['userJavaPackage']}}.model.{{message.payload().uid() | camelCase | upperFirst}};
        {%- endfor -%}
    {% endif -%}
{% endfor %}
import javax.annotation.processing.Generated;

@Generated(value="com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Service
public class PublisherServiceImpl implements PublisherService {
{% for channelName, channel in asyncapi.channels() %}
    {%- if channel.hasSubscribe() %}
        {%- set hasParameters = channel.hasParameters() %}
        {%- set methodName = channel.subscribe().id() | camelCase %}
        {%- if channel.subscribe().hasMultipleMessages() %}
            {%- set varName = "object" %}
        {%- else %}
            {%- set varName = channel.subscribe().message().payload().uid() | camelCase %}
        {%- endif %}
    {% if channel.description() or channel.subscribe().description() %}/**{% for line in channel.description() | splitByLines %}
     * {{line | safe}}{% endfor %}{% for line in channel.subscribe().description() | splitByLines %}
     * {{line | safe}}{% endfor %}
     */{% endif %}
    public void {{methodName}}(Integer key, {{varName | upperFirst}} {{varName}}{% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}, {% if parameter.schema().type() === 'object'%}{{parameterName | camelCase | upperFirst}}{% else %}{{parameter.schema().type() | toJavaType(false)}}{% endif %} {{parameterName | camelCase}}{% endfor %}{% endif %}) {

    }
}
{% endmacro %}