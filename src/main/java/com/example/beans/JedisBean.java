package com.example.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
// import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.util.DigestUtils;

import io.lettuce.core.RedisException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

public class JedisBean {

	private static final String redisHost = "localhost";
	private static final Integer redisPort = 6379;
	//the jedis connection pool..
	private static JedisPool pool = null;
	private static final String SEP = "____";
	
	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	public JedisBean() {
		pool = new JedisPool(redisHost, redisPort);
	}
	
	
	public String getFromDB(String objectId) {		
		
		// System.out.printf("Getting object {} from database.\n", objectId);
		LOG.info("Getting object {} from database.", objectId);
		
		JSONObject jsonObject = getHelper("plan" + SEP + objectId);
		if(jsonObject != null) {
			return jsonObject.toString();
		} else {
			return null;
		}
	}
	

	private JSONObject getHelper(String uuid) {
		try {
			Jedis jedis = pool.getResource();
			JSONObject jsonObj = new JSONObject();
			System.out.println("Reading keys from pattern");
			Set<String> keys = jedis.keys(uuid + SEP + "*");
		
			// object members
			for(String key : keys) {
				Set<String> jsonKeySet = jedis.smembers(key);				
				if(jsonKeySet.size() > 1) {					
					JSONArray jsonArr = new JSONArray();
					Iterator<String> jsonKeySetIterator = jsonKeySet.iterator();
					while(jsonKeySetIterator.hasNext()) {
						jsonArr.put(getHelper(jsonKeySetIterator.next()));
					}
					jsonObj.put(key.substring(key.lastIndexOf(SEP) + 4), jsonArr);
				} else {				
					Iterator<String> jsonKeySetIterator = jsonKeySet.iterator();
					JSONObject embdObject = null;
					while(jsonKeySetIterator.hasNext()) {
						embdObject = getHelper(jsonKeySetIterator.next());
					}
					jsonObj.put(key.substring(key.lastIndexOf(SEP) + 4), embdObject);
				}
			}
			
			// simple members
			Map<String,String> simpleMap = jedis.hgetAll(uuid);
			for(String simpleKey : simpleMap.keySet()) {
				jsonObj.put(simpleKey, simpleMap.get(simpleKey));
			}
//			System.out.println("jsonObj: " + jsonObj);

			jedis.close();
			return jsonObj;
		} catch(RedisException e) {
			e.printStackTrace();
            return null;
		}
	}
	
	
	public String add(JSONObject jsonObject) {
		try {
			String idOne = jsonObject.getString("objectType") + SEP + jsonObject.getString("objectId");
			if(!doesKeyExist(idOne) && addHelper(jsonObject, idOne.toString()))
				return idOne;
			else
				return null;
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean addHelper(JSONObject jsonObject, String uuid) {		
		try {
			Jedis jedis = pool.getResource();
			Map<String,String> simpleMap = new HashMap<String,String>();
			
			for(Object key : jsonObject.keySet()) {
				String attributeKey = String.valueOf(key);
				Object attributeVal = jsonObject.get(String.valueOf(key));
				String edge = attributeKey;
				if(attributeVal instanceof JSONObject) {
					
					JSONObject embdObject = (JSONObject) attributeVal;
					String setKey = uuid + SEP + edge;
					String embd_uuid = embdObject.get("objectType") + SEP + embdObject.getString("objectId");
					jedis.sadd(setKey, embd_uuid);
					addHelper(embdObject, embd_uuid);
					
				} else if (attributeVal instanceof JSONArray) {
					
					JSONArray jsonArray = (JSONArray) attributeVal;
					Iterator<Object> jsonIterator = jsonArray.iterator();
					String setKey = uuid + SEP + edge;
					
					while(jsonIterator.hasNext()) {
						JSONObject embdObject = (JSONObject) jsonIterator.next();
						String embd_uuid = embdObject.get("objectType") + SEP + embdObject.getString("objectId");
						jedis.sadd(setKey, embd_uuid);
						addHelper(embdObject, embd_uuid);
					}
					
				} else {
					simpleMap.put(attributeKey, String.valueOf(attributeVal));
				}
			}
			jedis.hmset(uuid, simpleMap);
			jedis.close();
		}
		catch(JedisException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	
	// delete plan
	public boolean delete(String objectId) {
		return deleteHelper("plan" + SEP + objectId);
	}
	
	public boolean deleteHelper(String uuid) {
		try {
			Jedis jedis = pool.getResource();
			
			// recursively deleting all embedded json objects
			Set<String> keys = jedis.keys(uuid + SEP + "*");
			for(String key : keys) {
				Set<String> jsonKeySet = jedis.smembers(key);
				for(String embd_uuid : jsonKeySet) {
					deleteHelper(embd_uuid);
				}
				jedis.del(key);
			}
			
			// deleting simple fields
			jedis.del(uuid);
			jedis.close();
			return true;
		} catch(JedisException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public boolean update(JSONObject jsonObject) {
		try {
			Jedis jedis = pool.getResource();
			String uuid = jsonObject.getString("objectType") + SEP + jsonObject.getString("objectId");
			
			Map<String,String> simpleMap = jedis.hgetAll(uuid);
			if(simpleMap.isEmpty()) {
				simpleMap = new HashMap<String,String>();
			}
			
			for(Object key : jsonObject.keySet()) {
				String attributeKey = String.valueOf(key);
				Object attributeVal = jsonObject.get(String.valueOf(key));
				String edge = attributeKey;
				
				if(attributeVal instanceof JSONObject) {
					
					JSONObject embdObject = (JSONObject) attributeVal;
					String setKey = uuid + SEP + edge;
					String embd_uuid = embdObject.get("objectType") + SEP + embdObject.getString("objectId");
					jedis.sadd(setKey, embd_uuid);
					update(embdObject);
					
				} else if (attributeVal instanceof JSONArray) {
					
					JSONArray jsonArray = (JSONArray) attributeVal;
					Iterator<Object> jsonIterator = jsonArray.iterator();
					String setKey = uuid + SEP + edge;
					
					while(jsonIterator.hasNext()) {
						JSONObject embdObject = (JSONObject) jsonIterator.next();
						String embd_uuid = embdObject.get("objectType") + SEP + embdObject.getString("objectId");
						jedis.sadd(setKey, embd_uuid);
						update(embdObject);
					}
					
				} else {
					simpleMap.put(attributeKey, String.valueOf(attributeVal));
				}
			}
			jedis.hmset(uuid, simpleMap);
			jedis.close();
			return true;
			
		} catch(JedisException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	public boolean doesKeyExist(String objectId) {		
		try {
			Jedis jedis = pool.getResource();
			if(jedis.exists("plan" + SEP + objectId) && !jedis.keys("plan" + SEP + objectId).isEmpty()) {
				jedis.close();
				return true;
			} else {
				jedis.close();
				return false;
			}
		} catch(JedisException e) {
			e.printStackTrace();
			return false;
		}
	}
	
}	

