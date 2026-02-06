package com.jss.osiris.modules.osiris.accounting.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.accounting.model.PaySlipLineType;

public interface PaySlipLineTypeRepository extends QueryCacheCrudRepository<PaySlipLineType, Integer> {

    PaySlipLineType findByLabel(String label);
}