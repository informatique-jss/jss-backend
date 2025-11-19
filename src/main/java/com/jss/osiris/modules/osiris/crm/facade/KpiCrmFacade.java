package com.jss.osiris.modules.osiris.crm.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValuePayload;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
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
    public BigDecimal getAggregateValuesForTiersList(String kpiCrmKey, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Integer> tiersIds, boolean isAllTiers) {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
        List<Tiers> outTiers = new ArrayList<Tiers>();
        if (tiersIds != null)
            for (Integer tiersId : tiersIds) {
                outTiers.add(tiersService.getTiers(tiersId));
            }

        return kpiCrmValueService.getAggregateValuesForTiersList(kpiCrm, startDate, endDate, salesEmployeeId, outTiers,
                isAllTiers);
    }

    @Transactional(rollbackFor = Exception.class)
    public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByTiersAndDate(String kpiCrmKey, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Integer> tiersIds, boolean isAllTiers,
            boolean aggregateResponsable) {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
        List<Tiers> outTiers = new ArrayList<Tiers>();
        if (tiersIds != null)
            for (Integer tiersId : tiersIds) {
                outTiers.add(tiersService.getTiers(tiersId));
            }

        return kpiCrmValueService.getKpiCrmValuePayloadAggregatedByTiersAndDate(kpiCrm, startDate, endDate,
                salesEmployeeId, outTiers,
                isAllTiers, aggregateResponsable);
    }

    @Transactional(rollbackFor = Exception.class)
    public BigDecimal getAggregateValuesForResponsableList(String kpiCrmKey, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Integer> responsableIds, boolean isAllResponsable) {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
        List<Responsable> outResponsables = new ArrayList<Responsable>();
        if (responsableIds != null)
            for (Integer responsableId : responsableIds) {
                outResponsables.add(responsableService.getResponsable(responsableId));
            }

        return kpiCrmValueService.getAggregateValuesForResponsableList(kpiCrm, startDate, endDate, salesEmployeeId,
                outResponsables,
                isAllResponsable);
    }

    @Transactional(rollbackFor = Exception.class)
    public KpiCrmValuePayload getKpiCrmValuePayloadAggregatedByResponsableAndDate(String kpiCrmKey, LocalDate startDate,
            LocalDate endDate, Integer salesEmployeeId, List<Integer> responsableIds, boolean isAllResponsable,
            boolean aggregateResponsable) {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(kpiCrmKey);
        List<Responsable> outResponsable = new ArrayList<Responsable>();
        if (responsableIds != null)
            for (Integer responsableId : responsableIds) {
                outResponsable.add(responsableService.getResponsable(responsableId));
            }

        return kpiCrmValueService.getKpiCrmValuePayloadAggregatedByResponsableAndDate(kpiCrm, startDate, endDate,
                salesEmployeeId,
                outResponsable,
                isAllResponsable, aggregateResponsable);
    }

}
