const _ = require('lodash');

const filter = module.exports;

const { 
  JavaGenerator,
  TypeHelpers,
  ModelKind,
  FormatHelpers,
  CommonModel,

  JAVA_COMMON_PRESET,
  JAVA_CONSTRAINTS_PRESET,
  JAVA_JACKSON_PRESET,
  JAVA_DESCRIPTION_PRESET,
} = require('@asyncapi/generator-model-sdk');

/**
 * Get from given CommonModel original `oneOf`, `anyOf` or `allOf` schema.
 */
function getCombinedSchema({ model, inputModel, type = 'oneOf' }) {
  let combined = undefined;

  const originalSchema = model.originalSchema;
  if (originalSchema && (originalSchema[type])) {
    combined = originalSchema[type];
  }

  const ref = model.$ref;
  const refModel = ref && inputModel && inputModel.models[ref];
  if (refModel) {
    const originalSchema = refModel.originalSchema;
    if (originalSchema && (originalSchema[type])) {
      combined = originalSchema[type];
    }
  }

  return combined;
}

/**
 * Get type for combined schema (`oneOf`, `anyOf` or `allOf`) by concatenating name of each element to single string.
 */
function getTypeFromCombinedSchema({ combinedSchema, type = 'OneOf' }) {
  for (const schema of combinedSchema) {
    // render as Object type
    if (schema.type !== "object") {
      type = "Object";
      break;
    }
    type += _.upperFirst(_.camelCase(schema['x-parser-schema-id']));
  }
  return type
}

/**
 * Custom naming convention for types in Java class.
 * It concatenates name of each element to single string from combined schema (`oneOf`, `anyOf` or `allOf`).
 * For enum type it adds `Enum` suffix.
 */
function customTypeNaming(name, { model, inputModel }) {
  name = name || model.originalSchema['x-modelgen-inferred-name'];
  if (ModelKind.ENUM === TypeHelpers.extractKind(model)) {
    return FormatHelpers.toPascalCase(`${name}Enum`);
  }

  const anyOfSchema = 
    getCombinedSchema({ model, inputModel, type: 'oneOf' }) ||
    getCombinedSchema({ model, inputModel, type: 'anyOf' });
  if (anyOfSchema) {
    const type = getTypeFromCombinedSchema({ combinedSchema: anyOfSchema, type: "OneOf" });
    return type;
  }

  const allOfSchema = getCombinedSchema({ model, inputModel, type: 'allOf' });
  if (allOfSchema) {
    const type = getTypeFromCombinedSchema({ combinedSchema: allOfSchema, type: "AllOf" });
    return type;
  }

  return _.upperFirst(_.camelCase(name));
}

/**
 * Preset which renders enum type as local class.
 */
const INLINE_ENUM_PRESET = {
  class: {
    async property({ renderer, property, inputModel, content }) {
      if (ModelKind.ENUM !== TypeHelpers.extractKind(property)) {
        return content;
      }
      const enumModel = await renderer.generator.renderEnum(property, inputModel);
      return renderer.renderBlock([enumModel, content]);
    },
  }
}

/**
 * Preset which renders `anyOf` or `oneOf` combined schema as local interface.
 */
const INLINE_ANY_OF = {
  class: {
    async property({ renderer, property, content, inputModel }) {
      const combinedSchema = 
        getCombinedSchema({ model: property, inputModel, type: 'oneOf' }) ||
        getCombinedSchema({ model: property, inputModel, type: 'anyOf' });
      if (!combinedSchema) {
        return content;
      }

      const type = getTypeFromCombinedSchema({ combinedSchema, type: "OneOf" })
      if (type === "Object") {
        return content;
      }

      const interfaceModel = `public interface ${type} {}`;
      return renderer.renderBlock([interfaceModel, content]);
    },
  }
}

/**
 * Preset which renders `allOf` combined schema as local class.
 */
const INLINE_ALL_OF = {
  class: {
    async property({ renderer, property, content, inputModel }) {
      const combinedSchema = getCombinedSchema({ model: property, inputModel, type: 'allOf' });
      if (!combinedSchema) {
        return content;
      }

      const type = getTypeFromCombinedSchema({ combinedSchema, type: "AllOf" });
      if (type === "Object") {
        return content;
      }

      const commonModel = new CommonModel();
      commonModel.$id = type;
      commonModel.properties = {};
      for (const schema of combinedSchema) {
        const cm = new CommonModel();
        const schemaId = schema['x-parser-schema-id'];
        const type = _.camelCase(schemaId);
        cm.$ref = _.upperFirst(type); // pascalCase
        commonModel.properties[type] = cm;
      }
      const classModel = await renderer.generator.renderClass(commonModel, inputModel);
      return renderer.renderBlock([classModel, content]);
    },
  }
}

/**
 * Preset which adds for each property the `@Valid` annotation.
 */
const VALID_ANNOTATION_PRESET = {
  class: {
    property({ renderer, content }) {
      const annotation = renderer.renderAnnotation('Valid');
      return renderer.renderBlock([annotation, content]);
    },
  },
}

/**
 * Render Java's class based on schema and schemaName using `@asyncapi/generator-model-sdk`.
 * 
 * For more info checks docs:
 * - generator: https://github.com/asyncapi/generator-model-sdk/blob/master/docs/generators.md
 * - customisation (presets): https://github.com/asyncapi/generator-model-sdk/blob/master/docs/customisation.md
 * 
 * @param schema to render
 * @param schemaName to render
 * @param params passed to template
 * @returns {string}
 */
async function renderJavaModel(schema, schemaName, params, callback) {
  javaGenerator = new JavaGenerator({ 
    namingConvention: {
      type: customTypeNaming,
    },
    presets: [
      VALID_ANNOTATION_PRESET,
      JAVA_CONSTRAINTS_PRESET,
      JAVA_JACKSON_PRESET,
      JAVA_DESCRIPTION_PRESET,
      {
        preset: JAVA_COMMON_PRESET,
        options: {
          equal: !(params.disableEqualsHashCode === 'true'),
          hashCode: !(params.disableEqualsHashCode === 'true'),
        }
      },
      INLINE_ANY_OF,
      INLINE_ALL_OF,
      INLINE_ENUM_PRESET,
    ] 
  });

  // copy schema
  const newSchema = Object.assign({}, schema)._json;
  newSchema.title = newSchema.title || schemaName;

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
