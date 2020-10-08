const path = require('path');
const _ = require('lodash');
const replace = require('replace-in-file');

module.exports = {
    'generate:after': generator => {
        const asyncapi = generator.asyncapi;
        const messages = asyncapi.allMessages();
        const schemas = asyncapi.allSchemas();

        let objectsRegistry = new Set();
        let interfaces = new Map();

        let objectProcessing = (className, parameters) => {
            if (parameters && parameters.every(obj => obj.type() === 'object')) {
                let interfaceName = _.upperFirst(_.camelCase(className)) + '.OneOf';
                parameters.forEach(obj => {
                    objectsRegistry.add(obj.uid());
                    interfaceName += _.upperFirst(_.camelCase(obj.uid()));
                });
                parameters.forEach(obj => {
                    if (interfaces.has(obj.uid())) {
                        if (!interfaces.get(obj.uid()).includes(interfaceName)) {
                            interfaces.set(obj.uid(), interfaces.get(obj.uid()) + ', ' + interfaceName);
                        }
                    } else {
                        interfaces.set(obj.uid(), interfaceName);
                    }
                });
            }
        }
        for (let [key, value] of messages) {
            objectProcessing(value.payload().uid(), [].concat(value.payload().anyOf(), value.payload().oneOf()).filter(obj => obj != null));
        }
        for (let [key, value] of schemas) {
            objectProcessing(value.uid(), [].concat(value.anyOf(), value.oneOf()).filter(obj => obj != null));
        }

        for (let [key, value] of schemas) {
            if (objectsRegistry.has(value.uid()) && value.type() === 'object') {
                const className = _.upperFirst(_.camelCase(value.uid()));
                const reg = new RegExp('public class ' + className, 'g')
                const options = {
                    files: path.resolve(generator.targetDir, `src/main/java/com/asyncapi/model/${key}.java`),
                    from: reg,
                    to: 'public class ' + className + ' implements ' + interfaces.get(value.uid()),
                };
                replace.sync(options);
            }
        }
    }
};