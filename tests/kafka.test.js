const path = require('path');
const Generator = require('@asyncapi/generator');
const { readFile } = require('fs').promises;

const MAIN_TEST_RESULT_PATH = path.join('tests', 'temp', 'integrationTestResult');

const generateFolderName = () => {
    // you always want to generate to new directory to make sure test runs in clear environment
    return path.resolve(MAIN_TEST_RESULT_PATH, Date.now().toString());
};

describe('template integration tests for generated files using the generator and kafka example', () => {

    jest.setTimeout(30000);

    it('should generate proper config, services and DTOs files for provided kafka', async() => {
        const outputDir = generateFolderName();
        const params = {};
        const kafkaExamplePath = './mocks/kafka.yml';

        const generator = new Generator(path.normalize('./'), outputDir, { forceWrite: true, templateParams: params });
        await generator.generateFromFile(path.resolve('tests', kafkaExamplePath));

        const expectedFiles = [
            '/src/main/java/com/asyncapi/infrastructure/Config.java',
            '/src/main/java/com/asyncapi/service/PublisherService.java',
            '/src/main/java/com/asyncapi/service/MessageHandlerService.java',
            '/src/main/java/com/asyncapi/model/LightMeasuredPayload.java',
            '/src/main/java/com/asyncapi/model/LightMeasured.java',
            '/src/test/java/com/asyncapi/TestcontainerKafkaTest.java',
            '/build.gradle',
            '/gradle.properties'
        ];
        for (const index in expectedFiles) {
            const file = await readFile(path.join(outputDir, expectedFiles[index]), 'utf8');
            const fileWithAnyDate = file.replace(/date="\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d\.\d+([+-][0-2]\d:[0-5]\d|Z)"/, 'date="AnyDate"');
            expect(fileWithAnyDate).toMatchSnapshot();
        }
    });
});

describe('template integration tests for generated files using the generator and kafka with parameters example', () => {

    jest.setTimeout(30000);

    it('should generate proper config and message handler for provided kafka', async() => {
        const outputDir = generateFolderName();
        const params = {};
        const kafkaExamplePath = './mocks/kafka-with-parameters.yml';

        const generator = new Generator(path.normalize('./'), outputDir, { forceWrite: true, templateParams: params });
        await generator.generateFromFile(path.resolve('tests', kafkaExamplePath));

        const expectedFiles = [
            '/src/main/java/com/asyncapi/infrastructure/Config.java',
            '/src/main/java/com/asyncapi/service/MessageHandlerService.java'
        ];
        for (const index in expectedFiles) {
            const file = await readFile(path.join(outputDir, expectedFiles[index]), 'utf8');
            const fileWithAnyDate = file.replace(/date="\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d\.\d+([+-][0-2]\d:[0-5]\d|Z)"/, 'date="AnyDate"');
            expect(fileWithAnyDate).toMatchSnapshot();
        }
    });
});