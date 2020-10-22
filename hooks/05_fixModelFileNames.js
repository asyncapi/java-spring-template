const _ = require('lodash');

module.exports = {
	'setFileTemplateName': (generator, hookArguments) => {
		const currentFilename = hookArguments.originalFilename ;
		return _.upperFirst(_.camelCase(currentFilename));
	}
};