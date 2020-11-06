package com.example.springboot;

import javax.servlet.Filter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.example")
@EnableCaching
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	
	@Bean
    public Filter filter(){
        ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
        return filter;
    }
	
	@Bean
	public FilterRegistrationBean<ShallowEtagHeaderFilter> shallowEtagHeaderFilter() {
	    FilterRegistrationBean<ShallowEtagHeaderFilter> filterRegistrationBean
	      = new FilterRegistrationBean<>( new ShallowEtagHeaderFilter());
	    filterRegistrationBean.addUrlPatterns("/*");
	    filterRegistrationBean.setName("etagFilter");
	    return filterRegistrationBean;
	}

}
