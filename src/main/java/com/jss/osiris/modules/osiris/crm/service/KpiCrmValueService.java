package com.jss.osiris.modules.osiris.crm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmSearchModel;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValueAggregatedByResponsable;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValueAggregatedByTiers;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValuePayload;

public interface KpiCrmValueService {

        public KpiCrmValue getKpiCrmValue(Integer id);

        public void addOrUpdateKpiCrmValues(List<KpiCrmValue> kpiCrmValues);

        public KpiCrmValue getLastCrmValue(KpiCrm kpiCrm);

        public List<KpiCrmValueAggregatedByTiers> getAggregateValuesForTiersListByTiers(KpiCrm kpiCrm,
                        KpiCrmSearchModel searchModel);

        public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByTiersAndDate(KpiCrm kpiCrm,
                        KpiCrmSearchModel searchModel,
                        boolean aggregateResponsable);

        public List<KpiCrmValueAggregatedByResponsable> getAggregateValuesForResponsableListByResponsable(KpiCrm kpiCrm,
                        KpiCrmSearchModel searchModel);

        public BigDecimal getAggregateValuesForResponsableList(KpiCrm kpiCrm, KpiCrmSearchModel searchModel);

        public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByResponsableAndDate(KpiCrm kpiCrm,
                        KpiCrmSearchModel searchModel, boolean aggregateResponsable);

        BigDecimal getKpiCrmValueAggregatedForTiersList(KpiCrm kpiCrm, KpiCrmSearchModel searchModel);

        public LocalDate getLastKpiCrmValueDate(KpiCrm kpiCrm);

        void deleteKpiCrmValuesForKpiCrm(KpiCrm kpiCrm);

}
