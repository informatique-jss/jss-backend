package com.jss.osiris.libs.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Overridden default db to allow support for datasource routing
 */

public class DynamicDataSource extends AbstractRoutingDataSource {
    @Override
    protected String determineCurrentLookupKey() {
        if (DynamicDataSourceHolder.getDataSource() != null) {
            return DataSourceType.MASTER.getType();
        }
        return DataSourceType.MASTER.getType();

    }
}