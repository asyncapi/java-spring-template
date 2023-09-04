const path = require('path');
const Generator = require('@asyncapi/generator');
const {existsSync} = require("fs");
const { readFile } = require('fs').promises;

const MAIN_TEST_RESULT_PATH = path.join('tests', 'temp', 'integrationTestResult');

const generateFolderName = () => {
    // you always want to generate to new directory to make sure test runs in clear environment
    return path.resolve(MAIN_TEST_RESULT_PATH, Date.now().toString());
};

describe('integration tests for generated files under different template parameters', () => {

    jest.setTimeout(30000);

    it('should generate spring 2 code with parameter', async() => {
        const outputDir = generateFolderName();
        const params = {"springBoot2": true};
        const kafkaExamplePath = './mocks/kafka.yml';

        const generator = new Generator(path.normalize('./'), outputDir, { forceWrite: true, templateParams: params });
        await generator.generateFromFile(path.resolve('tests', kafkaExamplePath));

        const expectedFiles = [
            '/src/main/java/com/asyncapi/service/MessageHandlerService.java',
            '/src/main/java/com/asyncapi/model/LightMeasuredPayload.java',
            '/src/main/java/com/asyncapi/model/LightMeasured.java',
            '/build.gradle',
            '/gradle.properties'
        ];
        for (const index in expectedFiles) {
            const file = await readFile(path.join(outputDir, expectedFiles[index]), 'utf8');
            expect(file).toMatchSnapshot();
        }
    });

    it('should generate maven build', async() => {
        const outputDir = generateFolderName();
        const params = {"maven": true};
        const kafkaExamplePath = './mocks/kafka.yml';

        const generator = new Generator(path.normalize('./'), outputDir, { forceWrite: true, templateParams: params });
        await generator.generateFromFile(path.resolve('tests', kafkaExamplePath));

        const expectedFiles = [
            '/pom.xml'
        ];
        const notExpectedFiles = [
            '/build.gradle',
            '/gradle.properties'
        ];
        for (const index in expectedFiles) {
            const file = await readFile(path.join(outputDir, expectedFiles[index]), 'utf8');
            expect(file).toMatchSnapshot();
        }
        for (const index in notExpectedFiles) {
            expect(existsSync(path.join(outputDir, notExpectedFiles[index]))).toBeFalsy();
        }
    });


    it('should generate gradle build', async() => {
        const outputDir = generateFolderName();
        const params = {"maven": false};
        const kafkaExamplePath = './mocks/kafka.yml';

        const generator = new Generator(path.normalize('./'), outputDir, { forceWrite: true, templateParams: params });
        await generator.generateFromFile(path.resolve('tests', kafkaExamplePath));

        const notExpectedFiles = [
            '/pom.xml'
        ];
        const expectedFiles = [
            '/build.gradle',
            '/gradle.properties'
        ];
        for (const index in expectedFiles) {
            const file = await readFile(path.join(outputDir, expectedFiles[index]), 'utf8');
            expect(file).toMatchSnapshot();
        }
        for (const index in notExpectedFiles) {
            expect(existsSync(path.join(outputDir, notExpectedFiles[index]))).toBeFalsy();
        }
    });
});
