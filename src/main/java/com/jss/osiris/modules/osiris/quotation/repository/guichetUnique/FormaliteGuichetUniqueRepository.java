package com.jss.osiris.modules.osiris.quotation.repository.guichetUnique;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;

public interface FormaliteGuichetUniqueRepository extends QueryCacheCrudRepository<FormaliteGuichetUnique, Integer> {

    @Query(nativeQuery = true, value = "select * from formalite_guichet_unique where reference_mandataire like concat('%',trim(:referenceMandataire),'%') ")
    List<FormaliteGuichetUnique> findByRefenceMandataire(@Param("referenceMandataire") String reference);

    @Query("select f from FormaliteGuichetUnique f where status in (:statusSignaturePending) and formalite is not null and payload is not null")
    List<FormaliteGuichetUnique> findFormaliteToSign(
            @Param("statusSignaturePending") List<FormaliteGuichetUniqueStatus> statusSignaturePending);

    List<FormaliteGuichetUnique> findByLiasseNumber(String value);

}