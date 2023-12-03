const filter = module.exports;
const _ = require('lodash');

function defineType(prop, propName) {
    if (prop.additionalProperties()) {
        if (prop.additionalProperties() === true) {
            return 'Map<String, Object>';
        } else if (prop.additionalProperties().type() === 'object') {
            return 'Map<String, ' + _.upperFirst(_.camelCase(prop.additionalProperties().uid())) + '>';
        } else if (prop.additionalProperties().format()) {
            return 'Map<String, ' + toClass(toJavaType(prop.additionalProperties().format())) + '>';
        } else if (prop.additionalProperties().type()) {
            return 'Map<String, ' + toClass(toJavaType(prop.additionalProperties().type())) + '>';
        }
    } else if (prop.type() === 'object') {
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

function toJavaType(str, isRequired) {
  let resultType;
  switch(str) {
    case 'integer':
    case 'int32':
      resultType = 'int'; break;
    case 'long':
    case 'int64':
      resultType = 'long'; break;
    case 'boolean':
      resultType = 'boolean'; break;
    case 'date':
      resultType = 'java.time.LocalDate'; break;
    case 'time':
      resultType = 'java.time.OffsetTime'; break;
    case 'dateTime':
    case 'date-time':
      resultType = 'java.time.OffsetDateTime'; break;
    case 'string':
    case 'password':
    case 'email':
    case 'uri':
    case 'hostname':
    case 'ipv4':
    case 'ipv6':
    case 'byte':
      resultType = 'String'; break;
    case 'uuid':
      resultType = 'java.util.UUID'; break;
    case 'float':
      resultType = 'float'; break;
    case 'number':
    case 'double':
      resultType = 'double'; break;
    case 'decimal':
      resultType = 'java.math.BigDecimal'; break;
    case 'binary':
      resultType = 'byte[]'; break;
    default:
      resultType = 'Object'; break;
  }
  return isRequired ? resultType : toClass(resultType);
}
filter.toJavaType = toJavaType;

function isDefined(obj) {
  return typeof obj !== 'undefined'
}
filter.isDefined = isDefined;

function isProtocol(api, protocol){
  return api.constructor.stringify(api).includes('"protocol":"' + protocol + '"');
};
filter.isProtocol = isProtocol;

function isObjectType(schemas){
  var res = [];
  for (let obj of schemas) {
    if (obj._json['type'] === 'object' && obj._json['x-parser-schema-id'] && !obj._json['x-parser-schema-id'].startsWith('<')) {
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

filter.currentTime = () => (new Date(Date.now())).toISOString();

function replaceAll(originalStr, replacePattern, replaceString) {
    return originalStr.replaceAll(replacePattern, replaceString)
}
filter.replaceAll = replaceAll;

function toTopicString(channelName, hasParameters, parameters, convertDots, replaceParameterValue, replaceDots = "\.") {
    if (hasParameters) {
        let topicName = channelName
        if (convertDots) {
            topicName = replaceAll(topicName, ".", replaceDots)
        }
        Object.keys(parameters).forEach(value => topicName = topicName.replace("{" + value + "}", replaceParameterValue))
        return topicName
    } else {
        return channelName
    }
}

function toMqttTopicString(channelName, hasParameters, parameters) {
    return toTopicString(channelName, hasParameters, parameters, false, "+")
}

filter.toMqttTopicString = toMqttTopicString

function toAmqpNeutral(channelName, hasParameters, parameters) {
    return toTopicString(_.camelCase(channelName), hasParameters, parameters, true, "", "")
}
filter.toAmqpNeutral = toAmqpNeutral

function toAmqpKey(channelName, hasParameters, parameters) {
    return toTopicString(channelName, hasParameters, parameters, true, "*")
}
filter.toAmqpKey = toAmqpKey