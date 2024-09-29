package com.jss.osiris.modules.quotation.repository.infoGreffe;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.infoGreffe.FormaliteInfogreffe;

public interface FormaliteInfogreffeRepository extends QueryCacheCrudRepository<FormaliteInfogreffe, String> {
    public FormaliteInfogreffe findByIdentifiantFormalite_FormaliteNumero(Integer numeroFormalite);

    @Query(nativeQuery = true, value = "select * from formalite_infogreffe where upper(reference_client) like concat('%',trim(upper(:reference)),'%')"
            +
            " or upper(reference_technique) like concat('%',trim(upper(:reference)),'%') " +
            " or cast(id_identifiant_formalite as text) like concat('%',trim(:reference),'%')")
    List<FormaliteInfogreffe> findByReference(@Param("reference") String reference);

}