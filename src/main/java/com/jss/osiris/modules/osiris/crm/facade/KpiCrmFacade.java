package com.jss.osiris.modules.osiris.crm.facade;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmSearchModel;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValuePayload;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@Service
public class KpiCrmFacade {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    TiersService tiersService;

    @Transactional(rollbackFor = Exception.class)
    public BigDecimal getAggregateValuesForTiersList(String kpiCrmKey, KpiCrmSearchModel searchModel) {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
        return kpiCrmValueService.getKpiCrmValueAggregatedForTiersList(kpiCrm, searchModel);
    }

    @Transactional(rollbackFor = Exception.class)
    public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByTiersAndDate(String kpiCrmKey,
            KpiCrmSearchModel searchModel,
            boolean aggregateResponsable) {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
        return kpiCrmValueService.getKpiCrmValuePayloadAggregatedByTiersAndDate(kpiCrm, searchModel,
                aggregateResponsable);
    }

    @Transactional(rollbackFor = Exception.class)
    public BigDecimal getAggregateValuesForResponsableList(String kpiCrmKey, KpiCrmSearchModel searchModel) {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
        return kpiCrmValueService.getAggregateValuesForResponsableList(kpiCrm, searchModel);
    }

    @Transactional(rollbackFor = Exception.class)
    public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByResponsableAndDate(String kpiCrmKey,
            KpiCrmSearchModel searchModel,
            boolean aggregateResponsable) {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
        return kpiCrmValueService.getKpiCrmValuePayloadAggregatedByResponsableAndDate(kpiCrm, searchModel,
                aggregateResponsable);
    }

}
