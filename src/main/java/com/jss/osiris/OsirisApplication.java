package com.jss.osiris;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class OsirisApplication {

	public static void main(String[] args) {
		SpringApplication.run(OsirisApplication.class, args);
	}
}
