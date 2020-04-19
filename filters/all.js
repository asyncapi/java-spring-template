const _ = require('lodash');

module.exports = ({ Nunjucks }) => {
  Nunjucks.addFilter('camelCase', (str) => {
    return _.camelCase(str);
  });

  Nunjucks.addFilter('upperFirst', (str) => {
    return _.upperFirst(str);
  });

  Nunjucks.addFilter('toJavaType', (str) => {
    switch(str) {
      case 'integer':
      case 'int32':
        return 'int';
      case 'long':
      case 'int64':
        return 'long';
      case 'boolean':
        return 'boolean';
      case 'date':
        return 'java.time.LocalDate';
      case 'dateTime':
      case 'date-time':
        return 'java.time.LocalDateTime';
      case 'string':
      case 'password':
      case 'byte':
        return 'String';
      case 'float':
        return 'float';
      case 'double':
        return 'double';
      case 'binary':
        return 'byte[]';
      default:
        return 'Object';
    }
  });

  Nunjucks.addFilter('isProtocol', (api, protocol) => {
    return JSON.stringify(api.json()).includes('"protocol":"' + protocol + '"');
  });

  Nunjucks.addFilter('print', (str) => {
    console.error(str);
  });

  Nunjucks.addFilter('splitByLines', (str) => {
    if (str) {
      return str.split(/\r?\n/);
    } else {
      return "";
    }
  });

  Nunjucks.addFilter('isRequired', (name, list) => {
    return list && list.includes(name);
  });

  Nunjucks.addFilter('schemeExists', (collection, scheme) => {
    return _.some(collection, {'scheme': scheme});
  });
};
