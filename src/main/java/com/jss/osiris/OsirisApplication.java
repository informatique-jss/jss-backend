package com.jss.osiris;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.jss.osiris.libs.jackson.JacksonPageSerializer;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableMethodSecurity
public class OsirisApplication {

	private static ConfigurableApplicationContext context;

	public static void main(String[] args) {
		context = SpringApplication.run(OsirisApplication.class, args);
	}

	public static void restart() {
		ApplicationArguments args = context.getBean(ApplicationArguments.class);

		Thread thread = new Thread(() -> {
			context.close();
			context = SpringApplication.run(OsirisApplication.class, args.getSourceArgs());
		});

		thread.setDaemon(false);
		thread.start();
	}

	public static void stop() {
		Thread thread = new Thread(() -> {
			context.close();
		});

		thread.setDaemon(false);
		thread.start();
	}

	@Bean
	public SimpleModule jacksonPageWithJsonViewModule() {
		SimpleModule module = new SimpleModule("jackson-page-with-jsonview", Version.unknownVersion());
		module.addSerializer(PageImpl.class, new JacksonPageSerializer());
		return module;
	}
}
