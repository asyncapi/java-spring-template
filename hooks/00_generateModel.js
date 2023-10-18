const fs= require('fs');
const modelina = require('@asyncapi/modelina')
const path = require("path");

module.exports = {
    'generate:before': generator => {
        const javaGenerator = new modelina.JavaFileGenerator();

        javaGenerator.generateToFiles(generator.asyncapi, path.resolve(generator.targetDir, 'src/main/java/com/asyncapi/modelina/'), {
            collectionType: "List",
            presets: [
                {
                    preset: modelina.JAVA_COMMON_PRESET,
                    options: {
                        equal: true,
                        hashCode: true,
                        classToString: true
                    }
                }
            ]
        }, true)
    }
};