package com.jss.osiris;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.ClasspathXmlConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import com.jss.osiris.libs.JsonSerializingCacheManager;
import com.jss.osiris.libs.jackson.JacksonPageSerializer;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EnableMethodSecurity
public class OsirisApplication {

	private static ConfigurableApplicationContext context;

	@Value("${spring.jpa.properties.hibernate.cache.hazelcast.native_client_cluster_name:dev}")
	private String clusterName;

	@Value("${spring.jpa.properties.hibernate.cache.hazelcast.native_client_address:127.0.0.1}")
	private String clusterAdresse;

	@Value("${dev.mode}")
	private Boolean devMode;

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

	@Bean
	public HazelcastInstance hazelcastInstance() {
		if (devMode) {
			Config config = new ClasspathXmlConfig("hazelcast.xml");
			HazelcastInstance instance = Hazelcast.newHazelcastInstance(config);
			instance.getCluster().getMembers();
			return instance;
		}

		ClientConfig clientConfig = new ClientConfig();
		clientConfig.getNetworkConfig().addAddress(clusterAdresse.split("\\s*,\\s*"));
		clientConfig.setClusterName(clusterName);
		clientConfig.setInstanceName("hzInstance1");
		return HazelcastClient.newHazelcastClient(clientConfig);
	}

	@Bean
	public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(HazelcastInstance hazelcastInstance) {
		if (devMode)
			return props -> {
			};

		return props -> {
			props.put("hazelcast.instance", hazelcastInstance);
		};
	}

	@Bean
	public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
		HazelcastCacheManager hazelcastCacheManager = new HazelcastCacheManager(hazelcastInstance);
		return new JsonSerializingCacheManager(hazelcastCacheManager);
	}

}
