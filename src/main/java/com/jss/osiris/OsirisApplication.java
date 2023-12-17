package com.jss.osiris;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableGlobalMethodSecurity(prePostEnabled = true)
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
}
