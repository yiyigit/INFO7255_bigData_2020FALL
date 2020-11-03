package validator;

import java.io.IOException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;

public class ValidationUtils {
	public static final String JSON_V4_SCHEMA_IDENTIFIER = "http://json-schema.org/draft-04/schema#";
	public static final String JSON_SCHEMA_IDENTIFIER_ELEMENT = "$schema";
	public static boolean isJsonValid(String schemaText, String jsonText) throws ProcessingException, IOException {
		final JsonSchema schemaNode = getSchemaNode(schemaText);
		final JsonNode jsonNode = getJsonNode(jsonText);
		return isJsonValid(schemaNode, jsonNode);
	} 
	public static boolean isJsonValid(JsonSchema jsonSchemaNode, JsonNode jsonNode) throws ProcessingException {
		ProcessingReport report = jsonSchemaNode.validate(jsonNode);
		return report.isSuccess();
	}
	public static JsonSchema getSchemaNode(String schemaText) throws IOException, ProcessingException {
		final JsonNode schemaNode = getJsonNode(schemaText);
		return _getSchemaNode(schemaNode);
	}
	public static JsonNode getJsonNode(String jsonText) throws IOException {
		return JsonLoader.fromString(jsonText);
	}
	private static JsonSchema _getSchemaNode(JsonNode jsonNode) throws ProcessingException {
		final JsonNode schemaIdentifier = jsonNode.get(JSON_SCHEMA_IDENTIFIER_ELEMENT);
		if (null == schemaIdentifier) {
			((ObjectNode) jsonNode).put(JSON_SCHEMA_IDENTIFIER_ELEMENT, JSON_V4_SCHEMA_IDENTIFIER);
		}

		final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
		return factory.getJsonSchema(jsonNode);
	}
}
