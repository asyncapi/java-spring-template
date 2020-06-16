const fs = require('fs');
const path = require('path');

module.exports = {
    'generate:after': generator => {
        console.log("Run 03");
        const asyncapi = generator.asyncapi;
        const messages = asyncapi.allMessages();
        const schemas = asyncapi.allSchemas();

        for (let [key, value] of schemas) {
            if (value.type() !== 'object') {
                try {
                    fs.unlinkSync(path.resolve(generator.targetDir, `src/main/java/com/asyncapi/model/${key}.java`));
                } catch (e) {}
            }
        }
    }
};