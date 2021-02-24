const filter = module.exports;

const { 
  JavaGenerator,
  TypeHelpers,
  ModelKind,
  FormatHelpers,
} = require('@asyncapi/generator-model-sdk');

let javaGenerator = undefined;

function renderEqualsHashCode({ renderer, upperCasedName, properties, options }) {
  if (options.params.disableEqualsHashCode === 'true') return "";

  const hashProperties = Object.keys(properties).map(prop => FormatHelpers.toCamelCase(prop)).join(', ');
  const equalProperties = Object.keys(properties).map(prop => {
    const camelCasedProp = FormatHelpers.toCamelCase(prop);
    return `Objects.equals(this.${camelCasedProp}, self.${camelCasedProp})`;
  }).join(' &&\n');

  return `@Override
public boolean equals(Object o) {
  if (this == o) {
    return true;
  }
  if (o == null || getClass() != o.getClass()) {
    return false;
  }
  ${upperCasedName} self = (${upperCasedName}) o;
    return 
${renderer.indent(equalProperties, 6)};
}
    
@Override
public int hashCode() {
  return Objects.hash(${hashProperties});
}
`;
}

function renderAdditionalContent({ renderer, model, content = "", options }) {
  const camelCasedName = FormatHelpers.toCamelCase(model.$id);
  const upperCasedName = FormatHelpers.upperFirst(camelCasedName);
  const properties = model.properties || {};
  const toStringProperties = Object.keys(properties).map(prop => `"    ${prop}: " + toIndentedString(${FormatHelpers.toCamelCase(prop)}) + "\\n" +`);

  const equalHashCode = renderEqualsHashCode({ renderer, upperCasedName, properties, options });

  return content + equalHashCode + `
@Override
public String toString() {
  return "class ${upperCasedName} {\\n" +   
${renderer.indent(renderer.renderBlock(toStringProperties), 4)}
    "}";
}

/**
 * Convert the given object to string with each line indented by 4 spaces (except the first line).
 */
private String toIndentedString(Object o) {
  if (o == null) {
    return "null";
  }
  return o.toString().replace("\\n", "\\n    ");
}`;
}

const TEMPLATE_PRESET = {
  class: {
    async property({ renderer, content }) {
      const annotation = renderer.renderAnnotation('Valid');
      return renderer.renderBlock([annotation, content]);
    },
    additionalContent: renderAdditionalContent,
  },
}

const INLINE_ENUM_PRESET = {
  class: {
    async property({ renderer, propertyName, property, content }) {
      if (ModelKind.ENUM !== TypeHelpers.extractKind(property)) {
        return content;
      }
      const enumName = FormatHelpers.toPascalCase(`${propertyName}Enum`);
    
      propertyName = FormatHelpers.toCamelCase(propertyName);
      const renderedProperty = `private ${enumName} ${propertyName};`;
    
      const newSchema = Object.assign({}, property.originalSchema);
      newSchema.$id = enumName;
      const enumModel = await javaGenerator.render(newSchema);
    
      const annotation = renderer.renderAnnotation('Valid');
      return renderer.renderBlock([enumModel, annotation, renderedProperty]);
    },
    getter({ renderer, propertyName, property, content }) {
      if (ModelKind.ENUM !== TypeHelpers.extractKind(property)) {
        return content;
      }

      propertyName = FormatHelpers.toCamelCase(propertyName);
      const getterName = FormatHelpers.toPascalCase(propertyName);
      const type = FormatHelpers.toPascalCase(`${propertyName}Enum`);
      return `public ${type} get${getterName}() { return this.${propertyName}; }`;
    },
    setter({ renderer, propertyName, property, content }) {
      if (ModelKind.ENUM !== TypeHelpers.extractKind(property)) {
        return content;
      }

      propertyName = FormatHelpers.toCamelCase(propertyName);
      const setterName = FormatHelpers.toPascalCase(propertyName);
      const type = FormatHelpers.toPascalCase(`${propertyName}Enum`);
      return `public void set${setterName}(${type} ${propertyName}) { this.${propertyName} = ${propertyName}; }`;
    },
  }
}

const INLINE_ONE_ANY_OF = {
  class: {
    async property({ renderer, propertyName, property, content }) {
      const originalSchema = property.originalSchema;
      console.log(originalSchema)
      if (originalSchema && (originalSchema.oneOf || originalSchema.anyOf)) {
        console.log(originalSchema)
      }

      return content;
    },
    getter({ renderer, propertyName, property, content }) {
      const originalSchema = property.originalSchema;
      if (originalSchema && (originalSchema.oneOf || originalSchema.anyOf)) {
        console.log(originalSchema)
      }

      return content;
    },
    setter({ renderer, propertyName, property, content }) {
      const originalSchema = property.originalSchema;
      if (originalSchema && (originalSchema.oneOf || originalSchema.anyOf)) {
        console.log(originalSchema)
      }

      return content;
    },
  }
}

const JACKSON_ANNOTATION_PRESET = {
  class: {
    getter({ renderer, propertyName, property, content, model }) {
      const annotations = [];
      annotations.push(renderer.renderAnnotation('JsonProperty', `"${propertyName}"`));
      
      const isRequired = model.isRequired(propertyName);
      if (isRequired) {
        annotations.push(renderer.renderAnnotation('NotNull') );
      }
    
      const pattern = property.getFromSchema('pattern');
      if (pattern) {
        annotations.push(renderer.renderAnnotation('Pattern', { regexp: `"${pattern}"` }));
      }
    
      const minimum = property.getFromSchema('minimum');
      if (minimum) {
        annotations.push(renderer.renderAnnotation('Min', minimum));
      }
    
      const exclusiveMinimum = property.getFromSchema('exclusiveMinimum');
      if (exclusiveMinimum) {
        annotations.push(renderer.renderAnnotation('Min', exclusiveMinimum + 1));
      }
    
      const maximum = property.getFromSchema('maximum');
      if (maximum) {
        annotations.push(renderer.renderAnnotation('Max', maximum));
      }
    
      const exclusiveMaximum = property.getFromSchema('exclusiveMaximum');
      if (exclusiveMaximum) {
        annotations.push(renderer.renderAnnotation('Min', exclusiveMaximum - 1));
      }
    
      const minItems = property.getFromSchema('minItems');
      const maxItems = property.getFromSchema('maxItems');
      if (minItems !== undefined || maxItems !== undefined) {
        annotations.push(renderer.renderAnnotation('Size', { min: minItems, max: maxItems }));
      }
    
      return renderer.renderBlock([...annotations, content]);
    },
  }
}

function renderDescription({ renderer, content, item }) {
  let desc = item.getFromSchema('description');
  
  const examples = item.getFromSchema('examples');
  if (examples) {
    let renderedExamples = "";
    examples.forEach(example => {
      if (renderedExamples !== "") {renderedExamples += ", "}
      if (typeof example == "object") {
        try {
          renderedExamples += JSON.stringify(example);
        } catch (ignore) {
          renderedExamples += example;
        }
      } else {
        renderedExamples += example;
      }
    });
    const exampleDesc = `Example: ${renderedExamples}`;
    desc = desc ? desc + `\n${exampleDesc}` : exampleDesc;
  }

  if (desc) {
    const renderedDesc = renderer.renderComments(desc);
    return `${renderedDesc}\n${content}`;
  }
  return content;
}

const DESCRIPTION_PRESET = {
  class: {
    self({ renderer, model, content }) {
      return renderDescription({ renderer, content, item: model });
    },
    getter({ renderer, property, content }) {
      return renderDescription({ renderer, content, item: property });
    }
  }
}

async function renderJavaModel(schema, schemaName, params, callback) {
  javaGenerator = new JavaGenerator({ presets: [
    {
      preset: TEMPLATE_PRESET,
      options: { params },
    },
    INLINE_ONE_ANY_OF,
    INLINE_ENUM_PRESET,
    JACKSON_ANNOTATION_PRESET,
    DESCRIPTION_PRESET,
  ] });

  // copy schema
  const newSchema = Object.assign({}, schema)._json;
  newSchema.title = schemaName;

  try {
    const models = await javaGenerator.generate(newSchema);
    const result = models[0].result;
 
    callback(null, result);
  } catch (error) {
    console.error(error);
    callback(error);
  }
}
filter.renderJavaModel = renderJavaModel;
