const fs = require('fs');
const path = require('path');

module.exports = {
    'generate:after': generator => {
        let hasMqtt, hasAmqp, hasKafka, hasPulsar;
        const asyncapi = generator.asyncapi;
        let javaPackage = generator.templateParams['userJavaPackage'];
        javaPackage = javaPackage.replace(/\./g, '/');
        for (let server of Object.values(asyncapi.servers())) {
            hasAmqp = hasAmqp || server.protocol() === 'amqp';
            hasMqtt = hasMqtt || server.protocol() === 'mqtt';
            hasKafka = hasKafka || server.protocol() === 'kafka';
            hasPulsar = hasPulsar || server.protocol() === 'pulsar';
        }
        if (!hasKafka) {
            // remove filers from template related only to Kafka
            fs.unlinkSync(path.resolve(generator.targetDir, 'src/test/java/' + javaPackage + '/SimpleKafkaTest.java'));
            fs.unlinkSync(path.resolve(generator.targetDir, 'src/test/java/' + javaPackage + '/TestcontainerKafkaTest.java'));
        }
        if (!hasAmqp) {
            // remove filers from template related only to amqp
        }
        if (!hasMqtt) {
            // remove filers from template related only to mqtt
            fs.unlinkSync(path.resolve(generator.targetDir, 'src/test/java/com/asyncapi/TestcontainerMqttTest.java'));
        }
        if (!hasPulsar) {
            // remove filers from template related only to pulsar
        }
    }
};
