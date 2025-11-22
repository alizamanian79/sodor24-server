package com.app.sodor24_server;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Sodor24ServerApplicationTests {

	@BeforeAll
	static void setUp() {
		Dotenv dotenv = Dotenv.load();
		System.setProperty("spring.profiles.active", dotenv.get("SPRING_PROFILES_ACTIVE"));
	}

	@Test
	void contextLoads() {
	}
}
