package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.beans.ElasticSearchConnect;
import com.example.beans.JedisBean;
import com.example.beans.MyJsonValidator;
import com.example.beans.EtagBean;

@Configuration
public class RedisConfig {

	@Bean("validator")
	public MyJsonValidator myJsonValidator() {
		return new MyJsonValidator() ;
	}
	
	@Bean("jedisBean")
	public JedisBean jedisBean() {
		return new JedisBean() ;
	}
	
	@Bean("elasticSearchConnect")
	public ElasticSearchConnect elasticSearchConnect() {
		return new ElasticSearchConnect() ;
	}
	@Bean("etagBean")
	public EtagBean etagBean() {
		return new EtagBean() ;
	}
	
}
