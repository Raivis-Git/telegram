package com.alphabot.telegram.message;

import io.github.cdimascio.dotenv.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MessageApplication {

	public static void main(String[] args) {
		loadEnvVariables();
		SpringApplication.run(MessageApplication.class, args);
	}

	private static void loadEnvVariables() {
		Dotenv.configure()
				.directory(".")
				.filename("application.env")
				.ignoreIfMissing()
				.load()
				.entries()
				.forEach(e -> System.setProperty(e.getKey(), e.getValue()));
	}

}
