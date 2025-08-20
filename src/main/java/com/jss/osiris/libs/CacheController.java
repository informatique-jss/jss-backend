package com.jss.osiris.libs;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {

	@Autowired
	private CacheManager cacheManager;

	@Autowired
	SessionFactory sessionFactory;

	private static final String inputEntryPoint = "/cache";

	@GetMapping(inputEntryPoint + "/clearAll")
	@SuppressWarnings({ "all" })
	@PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
	public void clearCache() {
		cacheManager.getCacheNames().forEach(name -> {
			var cache = cacheManager.getCache(name);
			if (cache != null)
				cache.clear();
		});

		// Clear Hibernate 2nd level cache
		sessionFactory.getCache().evictAllRegions();
	}
}
