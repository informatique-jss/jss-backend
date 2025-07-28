package com.jss.osiris.libs;

import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.hibernate.cache.v62.InfinispanRegionFactory;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;

@Component
public class HibernateCacheUtil {

    private final SessionFactory sessionFactory;

    public HibernateCacheUtil(EntityManagerFactory emf) {
        this.sessionFactory = emf.unwrap(SessionFactory.class);
    }

    public RegionFactory getRegionFactory() {
        return sessionFactory.getSessionFactoryOptions()
                .getServiceRegistry()
                .getService(RegionFactory.class);
    }

    // @Scheduled(fixedDelay = 60000) // toutes les minutes
    public void logLowEfficiencyCaches() {
        RegionFactory regionFactory = getRegionFactory();

        if (regionFactory instanceof InfinispanRegionFactory infinispanRegionFactory) {
            Statistics stats = sessionFactory.getStatistics();
            Set<String> cacheRegionNames = infinispanRegionFactory.getCacheManager().getCacheNames();

            for (String region : cacheRegionNames) {
                try {
                    CacheRegionStatistics regionStats = stats.getCacheRegionStatistics(region);
                    if (regionStats == null)
                        continue;

                    long hits = regionStats.getHitCount();
                    long misses = regionStats.getMissCount();
                    long puts = regionStats.getPutCount();
                    long total = hits + misses;
                    double hitRatio = total > 0 ? (double) hits / total : 0;

                    if (puts > 100 && hitRatio < 0.2) {
                        System.out.printf(
                                " Cache inefficace [%s] - Hits: %d, Misses: %d, Hit Ratio: %.2f, Stores: %d%n",
                                region, hits, misses, hitRatio, puts);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'analyse du cache " + region + " : " + e.getMessage());
                }
            }
        }
    }

    public void printCacheStats() {
        RegionFactory regionFactory = getRegionFactory();

        if (regionFactory instanceof InfinispanRegionFactory infinispanRegionFactory) {
            var cacheManager = infinispanRegionFactory.getCacheManager();

            try {
                Configuration conf = cacheManager.getCache("entity").getCacheConfiguration();
                System.out.println("Max entries: " + conf.memory().maxCount());
                System.out.println("Storage type: " + conf.memory().storageType());
            } catch (Exception e) {
            }

            cacheManager.getCacheNames().forEach(name -> {
                var cache = cacheManager.getCache(name, false);
                if (cache != null) {
                    var stats = cache.getAdvancedCache().getStats();
                    var config = cache.getCacheConfiguration();
                    Long maxSize = config.memory().size(); // méthode moderne (Infinispan 10+)
                    System.out.printf("Cache: %s | Hits: %d | Misses: %d | Stores: %d | Size: %d%n | MaxSize: %s%n",
                            name, stats.getHits(), stats.getMisses(), stats.getStores(), cache.size(),
                            maxSize != null ? maxSize : "unbounded");
                }
            });
        } else {
            System.out.println("RegionFactory is not InfinispanRegionFactory");
        }
    }
}