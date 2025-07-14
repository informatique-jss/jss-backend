package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.AssoServiceTypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.ServiceType;

public interface AssoServiceTypeDocumentRepository extends QueryCacheCrudRepository<AssoServiceTypeDocument, Integer> {

    List<AssoServiceTypeDocument> findByServiceTypeAndIsMandatory(ServiceType serviceType, boolean b);
}