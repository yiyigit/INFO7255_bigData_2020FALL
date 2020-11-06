package com.example.beans;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class ElasticSearchConnect {
	//private static final String indexerURI = "http://localhost:9200/planindex";
	
	//index the data
	public void runTask(String id, JSONObject jsonObject) {
		try {
			System.out.println("Adding to elasticsearch " + id);
			String objectType = jsonObject.getString("objectType");
			String indexer = "/plan"+"/"+"_doc"+"/"+ id;
			URL url = new URL("http", "localhost", 9200, indexer);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost postRequest = new HttpPost(url.toURI());
			StringEntity entity = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
			postRequest.setEntity(entity);
			CloseableHttpResponse httpResponse = httpClient.execute(postRequest);		
			System.out.println(httpResponse.getEntity());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
	}
	
	//delete the index
	public boolean deleteTask(String objectId) {
		
		try {
			System.out.println("Deleting from elasticsearch " + objectId);
			String indexer = "/plan"+"/"+"_doc"+"/"+ objectId;
			URL url = new URL("http", "localhost", 9200, indexer);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpDelete deleteRequest = new HttpDelete(url.toURI());
			CloseableHttpResponse httpResponse = httpClient.execute(deleteRequest);
			System.out.println(httpResponse.getEntity());
			return true;
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
		 
	}
	
	//search the index
	public String searchTask(JSONObject jsonObject) {		
		try {
			String indexer = "/plan"+"/"+"_search"+"/";
			URL url = new URL("http", "localhost", 9200, indexer);
			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpPost postRequest = new HttpPost(url.toURI());
			StringEntity entity = new StringEntity(jsonObject.toString(), ContentType.APPLICATION_JSON);
			postRequest.setEntity(entity);
			CloseableHttpResponse httpResponse = httpClient.execute(postRequest);
			
			HttpEntity searchEntity = httpResponse.getEntity();
            String content = EntityUtils.toString(searchEntity);
			return content;			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";	 
	}

}
