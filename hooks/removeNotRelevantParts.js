const fs = require('fs');
const path = require('path');

module.exports = register => {
    register('generate:after', generator => {
        let hasMqtt = false;
        let hasAmqp = false;
        let hasKafka = false;
        const asyncapi = generator.asyncapi;
        for (let server of Object.values(asyncapi.servers())) {
            hasAmqp = hasAmqp || server.protocol() === 'amqp';
            hasMqtt = hasMqtt || server.protocol() === 'mqtt';
            hasKafka = hasKafka || server.protocol() === 'kafka';
        }
        if (!hasKafka) {
            // remove filers from template related only to Kafka
            fs.unlinkSync(path.resolve(generator.targetDir, 'src/test/java/com/asyncapi/SimpleKafkaTest.java'));
            fs.unlinkSync(path.resolve(generator.targetDir, 'src/test/java/com/asyncapi/TestcontainerKafkaTest.java'));
        }
        if (!hasAmqp) {
            // remove filers from template related only to amqp
        }
        if (!hasMqtt) {
            // remove filers from template related only to mqtt
        }
    });
};