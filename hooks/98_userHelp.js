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
  console.log(`Following AnonymousSchema classes were generated in DTO classes:
${anonymousFileNames.toString()}\n
This may be a result of explicit (composition, inheritance, array items) Schema Object definition e.g.\n
    schemas:
      NamedObject:
      type: object
        properties:
          field:
            type: array
            items:
              type: object #Anonymous object
              properties:
                field:
                  type: string
\nOR\n
    messages:
      Message:
        payload:
          type: object #Anonymous object
          properties:\n
Please move such elements to child of "schemas:" to define proper names.
If changing of data model is not possible, you may use "$id" to set name e.g.\n
    properties:
      field:
        type: array
        items:
          $id: ArrayElement #Name of object
          type: object
          properties:
            field:
              type: string`);
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