package com.pasindu.dev.assignment.node_registry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

@SpringBootApplication
@Component
public class NodeRegistryApplication {

	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private ApplicationContext applicationContext;
	@Bean
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(NodeRegistryApplication.class, args);
	}

	private Boolean debug = true;

	@PostConstruct
	public void atStartup() {
		FileWatcherService fileWatcherService = applicationContext.getBean(FileWatcherService.class);
		taskExecutor.execute(fileWatcherService);
	}
}
