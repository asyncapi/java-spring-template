const fs = require('fs');
const path = require('path');
const _ = require('lodash');

module.exports = {
    'generate:after': generator => {
        const asyncapi = generator.asyncapi;
        const messages = asyncapi.allMessages();
        const schemas = asyncapi.allSchemas();

        for (let [key, value] of messages) {
            if (_.upperFirst(key) !== key) {
                fs.renameSync(path.resolve(generator.targetDir, `src/main/java/com/asyncapi/model/${key}.java`),
                    path.resolve(generator.targetDir, `src/main/java/com/asyncapi/model/${_.upperFirst(key)}.java`));
            }
        }
        for (let [key, value] of schemas) {
            if (_.upperFirst(key) !== key) {
                fs.renameSync(path.resolve(generator.targetDir, `src/main/java/com/asyncapi/model/${key}.java`),
                    path.resolve(generator.targetDir, `src/main/java/com/asyncapi/model/${_.upperFirst(key)}.java`));
            }
        }
    }
};