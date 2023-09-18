const fs = require('fs');
const path = require('path');

module.exports = {
    'generate:after': generator => {
        if (generator.templateParams &&
            (generator.templateParams['maven'] === true || generator.templateParams['maven'] === 'true')) {
            fs.unlinkSync(path.resolve(generator.targetDir, 'build.gradle'));
            fs.unlinkSync(path.resolve(generator.targetDir, 'gradle.properties'));
            fs.unlinkSync(path.resolve(generator.targetDir, 'gradlew'));
            fs.unlinkSync(path.resolve(generator.targetDir, 'gradlew.bat'));
            fs.rmdirSync(path.resolve(generator.targetDir, 'gradle'), {recursive: true});
        } else {
            fs.unlinkSync(path.resolve(generator.targetDir, 'pom.xml'));
        }
    }
};