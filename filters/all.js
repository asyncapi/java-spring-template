const filter = module.exports;
const _ = require('lodash');

function defineType(prop, propName) {
    if (prop.type() === 'object') {
        return _.upperFirst(_.camelCase(prop.uid()));
    } else if (prop.type() === 'array') {
        if (prop.items().type() === 'object') {
            return 'List<' + _.upperFirst(_.camelCase(prop.items().uid())) + '>';
        } else if (prop.items().format()) {
            return 'List<' + toClass(toJavaType(prop.items().format())) + '>';
        } else {
            return 'List<' + toClass(toJavaType(prop.items().type())) + '>';
        }
    } else if (prop.enum() && (prop.type() === 'string' || prop.type() === 'integer')) {
            return _.upperFirst(_.camelCase(propName)) + 'Enum';
    } else if (prop.anyOf() || prop.oneOf()) {
        let propType = 'OneOf';
        let hasPrimitive = false;
        [].concat(prop.anyOf(), prop.oneOf()).filter(obj => obj != null).forEach(obj => {
            hasPrimitive |= obj.type() !== 'object';
            propType += _.upperFirst(_.camelCase(obj.uid()));
        });
        if (hasPrimitive) {
            propType = 'Object';
        }
        return propType;
    } else if (prop.allOf()) {
        let propType = 'AllOf';
        prop.allOf().forEach(obj => {
            propType += _.upperFirst(_.camelCase(obj.uid()));
        });
        return propType;
    } else {
        if (prop.format()) {
            return toJavaType(prop.format());
        } else {
            return toJavaType(prop.type());
        }
    }
}
filter.defineType = defineType;

function toClass(couldBePrimitive) {
    switch(couldBePrimitive) {
        case 'int':
            return 'Integer';
        case 'long':
            return 'Long';
        case 'boolean':
            return 'Boolean';
        case 'float':
            return 'Float';
        case 'double':
            return 'Double';
        default:
            return couldBePrimitive;
    }
}
filter.toClass = toClass;

function toJavaType(str){
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
    case 'time':
      return 'java.time.OffsetTime';
    case 'dateTime':
    case 'date-time':
      return 'java.time.OffsetDateTime';
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
}
filter.toJavaType = toJavaType;

function isDefined(obj) {
  return typeof obj !== 'undefined'
}
filter.isDefined = isDefined;

function isProtocol(api, protocol){
  return JSON.stringify(api.json()).includes('"protocol":"' + protocol + '"');
};
filter.isProtocol = isProtocol;

function isObjectType(schemas){
  var res = [];
  for (let obj of schemas) {
    if (obj._json['type'] === 'object' && !obj._json['x-parser-schema-id'].startsWith('<')) {
      res.push(obj);
    }
  }
  return res;
};
filter.isObjectType = isObjectType;

function examplesToString(ex){
  let retStr = "";
  ex.forEach(example => {
    if (retStr !== "") {retStr += ", "}
    if (typeof example == "object") {
      try {
        retStr += JSON.stringify(example);
      } catch (ignore) {
        retStr += example;
      }
    } else {
      retStr += example;
    }
  });
  return retStr;
};
filter.examplesToString = examplesToString;

function splitByLines(str){
  if (str) {
    return str.split(/\r?\n|\r/).filter((s) => s !== "");
  } else {
    return "";
  }
};
filter.splitByLines = splitByLines;

function isRequired(name, list){
  return list && list.includes(name);
};
filter.isRequired = isRequired;

function schemeExists(collection, scheme){
  return _.some(collection, {'scheme': scheme});
};
filter.schemeExists = schemeExists;

function createEnum(val){
  let result;
  let withoutNonWordChars = val.replace(/[^A-Z^a-z^0-9]/g, "_");
  if ((new RegExp('^[^A-Z^a-z]', 'i')).test(withoutNonWordChars)) {
    result = '_' + withoutNonWordChars;
  } else {
    result = withoutNonWordChars;
  }
  return result;
};
filter.createEnum = createEnum;

function addBackSlashToPattern(val) {  
  let result = val.replace(/\\/g, "\\\\");
  return result;
}
filter.addBackSlashToPattern = addBackSlashToPattern;
