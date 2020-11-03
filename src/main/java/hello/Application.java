package hello;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.json.simple.parser.ParseException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import connection.RedisConnection;
import redis.clients.jedis.Jedis;

@SpringBootApplication
public class Application {
    
    public static void main(String[] args) throws ParseException {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        
        String schema = new Application().getFileWithUtil("jsonSchema.json");
        Jedis jedis = RedisConnection.getConnection();
		jedis.set("schemaKey", schema);
        
		System.out.println("Let's inspect the beans provided by Spring Boot:");
        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);
        for (String beanName : beanNames) {
            System.out.println(beanName);
        }
    }
    
    public String getFileWithUtil(String fileName) {

		String result = "";

		ClassLoader classLoader = getClass().getClassLoader();
		try {
			result = IOUtils.toString(classLoader.getResourceAsStream(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

}
