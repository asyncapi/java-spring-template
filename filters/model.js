const filter = module.exports;

const { JavaGenerator, FormatHelpers } = require('@asyncapi/generator-model-sdk');

function renderSelf({ content }) {
  return content;
}

function renderProperty({ renderer, content }) {
  const annotation = renderer.renderAnnotation('Valid');
  return renderer.renderBlock([annotation, content]);
}

function renderGetter({ renderer, propertyName, property, content, model }) {
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
}

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
    self: renderSelf,
    property: renderProperty,
    getter: renderGetter,
    additionalContent: renderAdditionalContent,
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
  // copy schema
  const newSchema = Object.assign({}, schema)._json;
  newSchema.title = schemaName;

  const javaGenerator = new JavaGenerator({ presets: [
    {
      preset: TEMPLATE_PRESET,
      options: { params },
    },
    DESCRIPTION_PRESET,
  ] });
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
