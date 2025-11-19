package com.jss.osiris.modules.osiris.crm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValueAggregatedByResponsable;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValueAggregatedByTiers;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValuePayload;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

public interface KpiCrmValueService {

        public KpiCrmValue getKpiCrmValue(Integer id);

        public void addOrUpdateKpiCrmValues(List<KpiCrmValue> kpiCrmValues);

        public KpiCrmValue getLastCrmValue(KpiCrm kpiCrm);

        public List<KpiCrmValueAggregatedByTiers> getAggregateValuesForTiersListByTiers(KpiCrm kpiCrm,
                        LocalDate startDate,
                        LocalDate endDate, List<Tiers> tiers);

        public BigDecimal getAggregateValuesForTiersList(KpiCrm kpiCrm, LocalDate startDate,
                        LocalDate endDate, List<Tiers> tiersList, boolean isAllTiers);

        public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByTiersAndDate(KpiCrm kpiCrm, LocalDate startDate,
                        LocalDate endDate, List<Tiers> tiersList, boolean isAllTiers);

        public List<KpiCrmValueAggregatedByResponsable> getAggregateValuesForResponsableListByResponsable(KpiCrm kpiCrm,
                        LocalDate startDate,
                        LocalDate endDate, List<Responsable> responsables);

        public BigDecimal getAggregateValuesForResponsableList(KpiCrm kpiCrm, LocalDate startDate,
                        LocalDate endDate, List<Responsable> responsableList, boolean isAllTiers);

        public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByResponsableAndDate(KpiCrm kpiCrm,
                        LocalDate startDate,
                        LocalDate endDate, List<Responsable> responsableList, boolean isAllTiers);

}
