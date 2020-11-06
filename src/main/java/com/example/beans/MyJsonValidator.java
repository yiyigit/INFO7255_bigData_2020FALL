package com.example.beans;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class MyJsonValidator {
	
	private Schema schema;
	
	// constructor
	public MyJsonValidator() {
	    File file = new File("src/main/resources/schema.json");
	    
	    InputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			inputStream = null;
		}
	    if (inputStream == null) {
	    	schema = null;
	    } else {
		    JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
		    schema = SchemaLoader.load(rawSchema);
	    }
	}
	
	// get schema
	public Schema getSchema() {
		return schema;
	}
	
	// get JSONObject from String
	public JSONObject getJsonObjectFromString(String jsonString) {
		return new JSONObject(jsonString);
	}
	
	// validate jsonObject against schema
	public boolean validate(JSONObject jsonObject) {
		try {
			schema.validate(jsonObject);
		}
		catch (ValidationException e) {
			return false;
		}
		return true;
	}

}
