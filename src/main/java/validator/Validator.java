package validator;

import java.io.IOException;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.InputStream;
import connection.RedisConnection;
import redis.clients.jedis.Jedis;

public class Validator {
	private final static Schema schema = loadSchema();

    private static Schema loadSchema() {
        InputStream inputStream = Validator.class.getResourceAsStream("/jsonSchema.json");
        System.out.println(inputStream.toString());
        JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
        Schema schema = SchemaLoader.load(rawSchema);
        return schema;
    }
	
	public static Boolean isJSONValid(JSONObject object) throws IOException, ProcessingException {
		try {
			schema.validate(object);
			return true;
		}
		catch (ValidationException e) {
			e.printStackTrace();
			return false;
		}
		
	}
}
