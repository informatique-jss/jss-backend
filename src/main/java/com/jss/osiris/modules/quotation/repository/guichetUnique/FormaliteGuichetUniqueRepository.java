package com.jss.osiris.modules.quotation.repository.guichetUnique;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;

public interface FormaliteGuichetUniqueRepository extends QueryCacheCrudRepository<FormaliteGuichetUnique, Integer> {

    @Query(nativeQuery = true, value = "select * from formalite_guichet_unique where reference_mandataire like concat('%',trim(:referenceMandataire),'%') ")
    List<FormaliteGuichetUnique> findByRefenceMandataire(@Param("referenceMandataire") String reference);

}