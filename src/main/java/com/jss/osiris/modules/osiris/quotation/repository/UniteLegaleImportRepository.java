package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.UniteLegaleImport;

public interface UniteLegaleImportRepository extends QueryCacheCrudRepository<UniteLegaleImport, String> {

    @Query(nativeQuery = true, value = "select * from unite_legale_import where is_updated = false limit 300")
    List<UniteLegaleImport> getNextUniteLegale();
}