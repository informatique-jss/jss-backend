package com.jss.osiris.libs;

import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import jakarta.persistence.QueryHint;

@NoRepositoryBean
public interface QueryCacheCrudRepository<T, ID> extends CrudRepository<T, ID> {
        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Iterable<T> findAll();

        @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
        Iterable<T> findAllById(Iterable<ID> ids);
}