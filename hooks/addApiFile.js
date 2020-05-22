const fs = require('fs');
const path = require('path');

module.exports = {
    'generate:after': generator => {
        const asyncapi = generator.originalAsyncAPI;
        let extension;

        try {
            JSON.parse(asyncapi);
            extension = 'json';
        } catch (e) {
            extension = 'yaml';
        }

        fs.writeFileSync(path.resolve(generator.targetDir, `src/main/resources/api/asyncAPI.${extension}`), asyncapi);
    }
};