package com.jss.osiris;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OsirisApplication {

	public static void main(String[] args) {
		SpringApplication.run(OsirisApplication.class, args);
	}

	@Autowired
	void configureObjectMapper(final ObjectMapper mapper) {
		// mapper.registerModule(new Hibernate5Module());
	}
}
