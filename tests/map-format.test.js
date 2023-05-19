const path = require('path');
const Generator = require('@asyncapi/generator');
const { readFile } = require('fs').promises;

const MAIN_TEST_RESULT_PATH = path.join('tests', 'temp', 'integrationTestResult');

const generateFolderName = () => {
    // you always want to generate to new directory to make sure test runs in clear environment
    return path.resolve(MAIN_TEST_RESULT_PATH, Date.now().toString());
};

describe('template integration tests for map format', () => {

    jest.setTimeout(30000);

    it('should generate DTO file with proper map types', async() => {
        const outputDir = generateFolderName();
        const params = {};
        const mapFormatExamplePath = './mocks/map-format.yml';

        const generator = new Generator(path.normalize('./'), outputDir, { forceWrite: true, templateParams: params });
        await generator.generateFromFile(path.resolve('tests', mapFormatExamplePath));

        const expectedFiles = [
            '/src/main/java/com/asyncapi/model/SongMetaData.java',
        ];
        for (const index in expectedFiles) {
            const file = await readFile(path.join(outputDir, expectedFiles[index]), 'utf8');
            expect(file).toMatchSnapshot();
        }
    });
});
