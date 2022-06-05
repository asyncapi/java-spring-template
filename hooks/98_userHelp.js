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
        const schemaWithAnonymous =
`  schemas:
    NamedObject:
      type: object
        properties:
          field:
            type: array
            items:
              type: object #Anonymous object
              properties:
                field:
                  type: string`;
        const messageWithAnonymous =
`  messages:
    Message:
      payload:
        type: object #Anonymous object
        properties:`;
        console.log(schemaWithAnonymous);
        console.log('OR');
        console.log(messageWithAnonymous);
        console.log('Please move such elements to child of "schemas:" to define proper names. ' +
            'If changing of data model is not possible, you may use "$id" to set name e.g.');
        const objectWithId =
`        properties:
          field:
            type: array
            items:
              $id: ArrayElement #Name of object
              type: object
              properties:
                field:
                  type: string`;
        console.log(objectWithId);
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