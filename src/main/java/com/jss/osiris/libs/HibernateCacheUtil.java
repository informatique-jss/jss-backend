package com.jss.osiris.libs;

import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.stat.CacheRegionStatistics;
import org.hibernate.stat.Statistics;
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
}