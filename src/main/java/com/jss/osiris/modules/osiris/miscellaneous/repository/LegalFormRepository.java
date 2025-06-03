package com.jss.osiris.modules.osiris.miscellaneous.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.LegalForm;

public interface LegalFormRepository extends QueryCacheCrudRepository<LegalForm, Integer> {

    Page<LegalForm> findByLabelContainingIgnoreCase(String label, Pageable pageable);
}