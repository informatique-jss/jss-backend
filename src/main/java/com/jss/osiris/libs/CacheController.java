package com.jss.osiris.libs;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class CacheController {
	@Autowired
	private CacheManager cacheManager;

	private static final String inputEntryPoint = "/cache";

	@GetMapping(inputEntryPoint + "/clearAll")
	public void clearCache() {
		for (String name : cacheManager.getCacheNames()) {
			cacheManager.getCache(name).clear();
		}
	}
}
