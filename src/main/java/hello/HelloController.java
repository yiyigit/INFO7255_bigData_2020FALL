package hello;

import org.springframework.web.bind.annotation.RestController;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import connection.RedisConnection;

import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import validator.Validator;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RestController
public class HelloController{
	
	Jedis jedis = RedisConnection.getConnection();
    
	@RequestMapping("/")
    public String index() {
        return "Greetings from Yiyi!";
    }
    
    @RequestMapping(value="/plan", method=RequestMethod.POST, consumes = "application/json")
    public String addPlan(@RequestBody String data, HttpServletResponse response) throws IOException, ProcessingException
    {
		
		JSONObject jsonObject = new JSONObject(data);
    	Boolean jsonValidity = Validator.isJSONValid(jsonObject);
    	if(jsonValidity == true) {
			String uuid = UUID.randomUUID().toString();
			String key = "key"+uuid;
			jedis.set(key,data);
			return key + " inserted successfully";
    	}else {
    		response.setStatus(400);
			return "JSON Schema not valid!";
    	}
    }
    
    @RequestMapping(value="/plan/{id}", method=RequestMethod.GET)
    public String getPlan(@PathVariable String id, HttpServletResponse response)
    {
    	jedis.connect();
    	String key = id;
    	String planKey =  jedis.get(key);
    	return planKey;
    }
    
    @RequestMapping(value="/plan/{id}", method=RequestMethod.DELETE)
    public String deletePlan(@PathVariable String id, HttpServletResponse response)
    {
    	jedis.connect();
    	String key = id;
    	jedis.del(key);
    	return key + " deleted successfully!";
    }
    
    @RequestMapping(value="/plan/{id}", method=RequestMethod.PUT)
    public String updatePlan(@RequestBody String data, @PathVariable String id, HttpServletResponse response) throws IOException, ProcessingException
    {
    	jedis.connect();
    	String key = id;
		JSONObject jsonObject = new JSONObject(data);
    	Boolean jsonValidity = Validator.isJSONValid(jsonObject);
    	if(jsonValidity == true) {
    		jedis.set(key, data);
    		return key + " updated successfully!";
    	}
    	else {
    		return "JSON Schema not valid!";
    	}
    }
}
