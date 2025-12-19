package com.jss.osiris.modules.osiris.quotation.repository.infoGreffe;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.infoGreffe.KbisRequest;

public interface KbisRequestRepository extends QueryCacheCrudRepository<KbisRequest, Integer> {

    List<KbisRequest> findBySiret(String siret);

}
