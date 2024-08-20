package com.jss.osiris.modules.quotation.repository.infoGreffe;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.quotation.model.infoGreffe.FormaliteInfogreffe;

public interface FormaliteInfogreffeRepository extends QueryCacheCrudRepository<FormaliteInfogreffe, String> {
    public FormaliteInfogreffe findByIdentifiantFormalite_FormaliteNumero(Integer numeroFormalite);
}