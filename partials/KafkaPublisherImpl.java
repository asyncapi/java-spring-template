import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.processing.Generated;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Generated(value = "com.asyncapi.generator.template.spring", date="{{''|currentTime }}")
@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    private KafkaTemplate<Integer, Object> kafkaTemplate;
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
        Message<{{varName | upperFirst}}> message = MessageBuilder.withPayload({{varName}})
                .setHeader(KafkaHeaders.TOPIC, get{{methodName | upperFirst-}}Topic({% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}{{parameterName | camelCase}}{% if not loop.last %}, {% endif %}{% endfor %}{% endif %}))
                .setHeader(KafkaHeaders.{%- if params.springBoot2 %}MESSAGE_KEY{% else %}KEY{% endif -%}, key)

    // Example method for publishing messages
    public void publishMessage(Integer key, YourMessageType messagePayload) {
        Message<YourMessageType> message = MessageBuilder.withPayload(messagePayload)
                .setHeader(KafkaHeaders.TOPIC, "yourTopicName")
                .setHeader(KafkaHeaders.KEY, key)
                .build();

    private String get{{methodName | upperFirst-}}Topic({% if hasParameters %}{%for parameterName, parameter in channel.parameters() %}{% if parameter.schema().type() === 'object'%}{{parameterName | camelCase | upperFirst}}{% else %}{{parameter.schema().type() | toJavaType(false)}}{% endif %} {{parameterName | camelCase}}{% if not loop.last %}, {% endif %}{% endfor %}{% endif %}) {
        Map<String, String> parameters = {% if hasParameters %}new HashMap<>(){% else %}null{% endif %};
        {%- if hasParameters %}
            {%- for parameterName, parameter in channel.parameters() %}
        parameters.put("{{parameterName}}", {{parameterName | camelCase}}{% if parameter.schema().type() !== 'string'%}.toString(){% endif %});
            {%- endfor %}
        {%- endif %}
        return replaceParameters("{{channelName}}", parameters);
    }
    {%- endif %}
{%- endfor %}
             
    private String replaceParameters(String topic, Map<String, String> parameters) {
        if (parameters != null) {
            String compiledTopic = topic;
            for (String key : parameters.keySet()) {
                compiledTopic = compiledTopic.replace("{" + key + "}", parameters.get(key));
        // Asynchronous send with callback
        ListenableFuture<SendResult<Integer, Object>> future = kafkaTemplate.send(message);
        future.addCallback(new ListenableFutureCallback<SendResult<Integer, Object>>() {
            @Override
            public void onSuccess(SendResult<Integer, Object> result) {
                // Handle success scenario
                System.out.println("Message sent successfully: " + result.getProducerRecord().value());
            }

            @Override
            public void onFailure(Throwable ex) {
                // Handle failure scenario
                System.err.println("Error sending message: " + ex.getMessage());
                // Implement retry logic or other error handling mechanisms as needed
            }
        });

        // Alternatively, for synchronous send with exception handling
        try {
            kafkaTemplate.send(message).get(10, TimeUnit.SECONDS);
            // Handle success scenario
            System.out.println("Message sent successfully: " + message.getPayload());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // Handle thread interruption
            System.err.println("Thread interrupted while sending message: " + e.getMessage());
        } catch (ExecutionException e) {
            // Handle execution exceptions
            System.err.println("Execution exception during message send: " + e.getMessage());
            // Implement retry logic or other error handling mechanisms as needed
        } catch (TimeoutException e) {
            // Handle timeout exceptions
            System.err.println("Timeout while sending message: " + e.getMessage());
            // Implement retry logic or other error handling mechanisms as needed
        }
    }

}
