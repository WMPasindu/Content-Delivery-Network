package com.pasindu.dev.assignment.node_one;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class NodeOneApplication {

	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}
	
	public static void main(String[] args) {
		SpringApplication.run(NodeOneApplication.class, args);
	}
	
	@Bean
	public String test() {
		return "Hello @@@@@ ";
	}

}
