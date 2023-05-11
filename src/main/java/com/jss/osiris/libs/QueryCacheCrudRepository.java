package com.jss.osiris.libs;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface QueryCacheCrudRepository<T, ID> extends CrudRepository<T, ID> {
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Iterable<T> findAll();

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Iterable<T> findAllById(Iterable<ID> ids);
}