package com.jss.osiris.modules.quotation.repository.guichetUnique;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;

public interface FormeJuridiqueRepository extends QueryCacheCrudRepository<FormeJuridique, String> {

    FormeJuridique findByCode(String code);

}
