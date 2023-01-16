package com.jss.osiris.libs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
public class CacheController {
	@Autowired
	private CacheManager cacheManager;

	private static final String inputEntryPoint = "/cache";

	@GetMapping(inputEntryPoint + "/clearAll")
	@SuppressWarnings({ "all" })
	public void clearCache() {
		for (String name : cacheManager.getCacheNames()) {
			if (cacheManager.getCache(name) != null) {
				cacheManager.getCache(name).clear();
			}
		}
	}
}
