const fs = require('fs-extra');

module.exports = {
    /**
     * Move directory with source code to specified.
     *
     * @param generator
     */
    'generate:after': generator => {
        const sourcePath = generator.targetDir + '/src/main/java/';
        const testPath = generator.targetDir + '/src/test/java/';
        let javaPackage = generator.templateParams['userJavaPackage'];

        javaPackage = javaPackage.replace(/\./g, '/');

        if (javaPackage !== 'com/asyncapi') {
            fs.moveSync(sourcePath + 'com/asyncapi', sourcePath + javaPackage);
            fs.moveSync(testPath + 'com/asyncapi', testPath + javaPackage);
            fs.removeSync(sourcePath + 'com');
            fs.removeSync(testPath + 'com');
        }
    },

    /**
     * If parameters wasn't pass, but extension is used, then set extension to param value.
     * Since template params are not modifiable, another param used to store updated value.
     *
     * @param generator
     */
    'generate:before': generator => {
        const extensions = generator.asyncapi.info().extensions();
        let javaPackage = generator.templateParams['javaPackage'];

        if (javaPackage === 'com.asyncapi' && extensions && extensions['x-java-package']) {
            javaPackage = extensions['x-java-package'];
        }

        Object.defineProperty(generator.templateParams, 'userJavaPackage', {
            enumerable: true,
            get() {
                return javaPackage;
            }
        });
    }
};