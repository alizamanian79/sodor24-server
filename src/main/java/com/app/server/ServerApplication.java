package com.app.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@ComponentScan(basePackages = {"com"})
public class ServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerApplication.class, args);
	}

}
