package com.jss.osiris.modules.osiris.beneficialOwner.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.beneficialOwner.model.BeneficialOwner;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;

public interface BeneficialOwnerRepository extends QueryCacheCrudRepository<BeneficialOwner, Integer> {
    @Query("select b from BeneficialOwner b where b.affaire =:affaire")
    List<BeneficialOwner> findBeneficialOwnersByAffaire(@Param("affaire") Affaire affaire);
}
