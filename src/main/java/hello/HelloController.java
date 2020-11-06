package hello;

import org.springframework.web.bind.annotation.RestController;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

import connection.RedisConnection;
// import io.undertow.util.ETagUtils;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import validator.Validator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import java.io.IOException;
import java.util.UUID;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.PostMapping;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.*;
import com.nimbusds.jose.jwk.*;
import com.nimbusds.jose.jwk.gen.*;
import com.nimbusds.jwt.*;
@RestController
public class HelloController{
	
	Jedis jedis = RedisConnection.getConnection();
	private RSAKey rsaPublicJWK;

	@RequestMapping("/")
    public String index() {
        return "Greetings from Yiyi!";
    }
    
    @RequestMapping(value="/plan", method=RequestMethod.POST)
    public ResponseEntity<String> addPlan(@RequestBody String data, @RequestHeader HttpHeaders requestHeaders,  HttpServletResponse response) throws JOSEException, ParseException,  IOException, ProcessingException
    {
		
		JSONObject jsonObject = new JSONObject(data);
		Boolean jsonValidity = Validator.isJSONValid(jsonObject);
		if (!ifAuthorized(requestHeaders)) {
			return new ResponseEntity<String>("Unauthorized request", HttpStatus.UNAUTHORIZED);
		}
    	if(jsonValidity == true) {
			String uuid = UUID.randomUUID().toString();
			String key = "key"+uuid;
			jedis.set(key,data);
			return new ResponseEntity<String>(key + " inserted successfully", HttpStatus.OK);
			// return ResponseEntity.ok().body(key + " inserted successfully");
    	}else {
			response.setStatus(400);
			return new ResponseEntity<String>("JSON Schema not valid!", HttpStatus.BAD_REQUEST);

			// return ResponseEntity.badRequest().body("JSON Schema not valid!");
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
	@RequestMapping(value="/token", method=RequestMethod.GET)
	public ResponseEntity getToken() throws JOSEException {
		// RSA signatures require a public and private RSA key pair, the public key 
		// must be made known to the JWS recipient in order to verify the signatures
		RSAKey rsaJWK = new RSAKeyGenerator(2048).keyID("123").generate();
		rsaPublicJWK = rsaJWK.toPublicJWK();
		// verifier = new RSASSAVerifier(rsaPublicJWK);

		// Create RSA-signer with the private key
		JWSSigner signer = new RSASSASigner(rsaJWK);

		// Prepare JWT with claims set
		int expireTime = 30000; // seconds
		
		JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
		    .expirationTime(new Date(new Date().getTime() + expireTime * 1000)) // milliseconds
		    .build();

		SignedJWT signedJWT = new SignedJWT(
		    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(rsaJWK.getKeyID()).build(),
		    claimsSet);

		// Compute the RSA signature
		signedJWT.sign(signer);
		
		// To serialize to compact form, produces something like
		// eyJhbGciOiJSUzI1NiJ9.SW4gUlNBIHdlIHRydXN0IQ.IRMQENi4nJyp4er2L
		// mZq3ivwoAjqa1uUkSBKFIX7ATndFF5ivnt-m8uApHO4kfIFOrW7w2Ezmlg3Qd
		// maXlS9DhN0nUk_hGI3amEjkKd0BWYCB8vfUbUv0XGjQip78AI4z1PrFRNidm7
		// -jPDm5Iq0SZnjKjCNS5Q15fokXZc8u0A
		String token = signedJWT.serialize();
		
		return ResponseEntity.ok().body(token);
	
	}
    @RequestMapping(value="/plan/{id}", method=RequestMethod.DELETE)
    public String deletePlan(@PathVariable String id, HttpServletResponse response)
    {
    	jedis.connect();
    	String key = id;
    	jedis.del(key);
    	return key + " deleted successfully!";
    }
    
    @RequestMapping(value="/plan/{id}", method=RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity updatePlan(@RequestBody String data, @PathVariable String id, HttpServletResponse response) throws IOException, ProcessingException
    {
    	jedis.connect();
    	String key = id;
		JSONObject jsonObject = new JSONObject(data);
		Boolean jsonValidity = Validator.isJSONValid(jsonObject);
		// String etag = EtagUtil.generateEtag(data);
    	if(jsonValidity == true) {
    		jedis.set(key, data);
    		return ResponseEntity.ok().eTag("o").body(key + " updated successfully!");
    	}
    	else {
    		return ResponseEntity.badRequest().body("JSON Schema not valid!");
    	}
	}
	
	@RequestMapping(value="/plan/{id}", method=RequestMethod.PUT)
    public String updatePlan(@RequestBody JSONObject jsonObject, @PathVariable String id, HttpServletResponse response) throws IOException, ProcessingException
    {
    	jedis.connect();
		String key = id;
		String data = jsonObject.toString();
    	Boolean jsonValidity = Validator.isJSONValid(jsonObject);
    	if(jsonValidity == true) {
    		jedis.set(key, data);
    		return key + " updated successfully!";
    	}
    	else {
    		return "JSON Schema not valid!";
		}
		
	}
	
	private boolean ifAuthorized(HttpHeaders requestHeaders) throws ParseException,  JOSEException {
		String token = requestHeaders.getFirst("Authorization");
		if(token==null){
			return false;
		}
		// On the consumer side, parse the JWS and verify its RSA signature
		SignedJWT signedJWT = SignedJWT.parse(token.substring(7));
		if(signedJWT== null || rsaPublicJWK==null){
			return false;
		}
		JWSVerifier verifier = new RSASSAVerifier(rsaPublicJWK);
		// Retrieve / verify the JWT claims according to the app requirements
		if (!signedJWT.verify(verifier)) {
			return false;
		}
		JWTClaimsSet claimset = signedJWT.getJWTClaimsSet();
		Date exp = 	claimset.getExpirationTime();
		
		// System.out.println(exp);		
		// System.out.println(new Date());
		
		return new Date().before(exp);
	}

}
