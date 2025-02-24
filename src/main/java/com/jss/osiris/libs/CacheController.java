package com.jss.osiris.libs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hazelcast.cache.ICache;
import com.hazelcast.core.DistributedObject;
import com.hazelcast.core.HazelcastInstance;

import jakarta.annotation.PreDestroy;

@RestController
@CrossOrigin
@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
public class CacheController {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	HazelcastInstance hazelcastInstance;

	private static final String inputEntryPoint = "/cache";

	@GetMapping(inputEntryPoint + "/clearAll")
	@SuppressWarnings({ "all" })
	public void clearCache() {
		for (DistributedObject distributedObject : hazelcastInstance.getDistributedObjects()) {
			if (distributedObject instanceof ICache) {
				ICache<?, ?> cache = (ICache<?, ?>) distributedObject;
				String cacheName = cache.getName();
				try {
					cacheManager.getCache(cacheName).invalidate();
					cacheManager.getCache(cacheName).clear();
					System.out.println("Cleared cache: " + cacheName);
				} catch (Exception e) {
					System.err.println("Error clearing cache " + cacheName + ": " + e.getMessage());
				}
			}
		}

		// for (String name : hazelcastInstance.getCacheManager()) {
		// if (cacheManager.getCache(name) != null) {
		// cacheManager.getCache(name).clear();
		// }
		// }
	}

	@PreDestroy
	public void shutDownHazelcast() {
		hazelcastInstance.shutdown();
	}
}
