package com.jss.osiris.modules.osiris.beneficialOwner.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.beneficialOwner.model.BeneficialOwner;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;

public interface BeneficialOwnerRepository extends QueryCacheCrudRepository<BeneficialOwner, Integer> {

    List<BeneficialOwner> findBeneficialOwnersByFormalite(Formalite formalite);
}
