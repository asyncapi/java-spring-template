const fs = require('fs');

function checkForAnonymousSchemaObjects(generator) {
    const modelPath = generator.targetDir + '/src/main/java/com/asyncapi/model';
    var anonymousPresent = false;
    var anonymousFileNames = [];
    fs.readdirSync(modelPath).forEach(file => {
        if (file.startsWith('AnonymousSchema')) {
            anonymousPresent = true;
            anonymousFileNames.push(generator.templateParams['userJavaPackage'].replace(/\./g, '/') + '/model/' + file);
        }
    });
    if (anonymousPresent) {
        console.log('Following AnonymousSchemaN classes were generated in DTO classes:');
        anonymousFileNames.forEach(name => console.log(name));
        console.log('This may be a result of explicit (composition, inheritance, array items) Schema Object definition e.g.');
        console.log('  schemas:\n' +
            '    NamedObject:\n' +
            '      type: object\n' +
            '        properties:\n' +
            '          field:\n' +
            '            type: array\n' +
            '            items:\n' +
            '              type: object #Anonymous object\n' +
            '              properties:\n' +
            '                field:\n' +
            '                  type: string');
        console.log('OR');
        console.log('  messages:\n' +
            '    Message:\n' +
            '      payload:\n' +
            '        type: object #Anonymous object\n' +
            '        properties:\n');
        console.log('Please move such elements to child of "schemas:" to define proper names. ' +
            'If changing of data model is not possible, you may use "$id" to set name e.g.');
        console.log(
            '        properties:\n' +
            '          field:\n' +
            '            type: array\n' +
            '            items:\n' +
            '              $id: ArrayElement #Name of object\n' +
            '              type: object\n' +
            '              properties:\n' +
            '                field:\n' +
            '                  type: string');
    }
}

module.exports = {
    /**
     * Print help information for user if problems encountered during generation.
     *
     * @param generator
     */
    'generate:after': generator => {
        checkForAnonymousSchemaObjects(generator);
    }
};