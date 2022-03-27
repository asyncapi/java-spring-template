const fs = require('fs');

function checkForAnonymousSchemaObjects(generator) {
    const modelPath = generator.targetDir + '/src/main/java/com/asyncapi/model';
    var anonymousPresent = false;
    fs.readdirSync(modelPath).forEach(file => {
        if (file.startsWith('AnonymousSchema')) {
            anonymousPresent = true;
        }
    });
    if (anonymousPresent) {
        console.log('The AnonymousSchemaN classes were generated in DTO classes.');
        console.log('This may be a result of explicit schema object definition e.g.');
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
            'If changing of data model is not possible, you may use "$id" or "x-parser-schema-id" to set name e.g.');
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