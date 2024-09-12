package com.jss.osiris.modules.quotation.repository.guichetUnique;

import org.springframework.data.jpa.repository.QueryHints;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;

import jakarta.persistence.QueryHint;

public interface FormeJuridiqueRepository extends QueryCacheCrudRepository<FormeJuridique, String> {

    @QueryHints({ @QueryHint(name = "org.hibernate.cacheable", value = "true") })
    FormeJuridique findByCode(String code);

}
