package validator;

import java.io.IOException;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import connection.RedisConnection;
import redis.clients.jedis.Jedis;

public class Validator {
	
	public static Boolean isJSONValid(String jsonFile) throws IOException, ProcessingException {

		Jedis jedis = RedisConnection.getConnection();

		String planSchema = jedis.get("schemaKey");

		if (ValidationUtils.isJsonValid(planSchema, jsonFile)) {
			return true;
		} else {
			return false;
		}
	}
}
