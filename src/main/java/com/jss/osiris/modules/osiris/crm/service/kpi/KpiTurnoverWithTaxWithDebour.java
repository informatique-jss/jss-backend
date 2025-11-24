package com.jss.osiris.modules.osiris.crm.service.kpi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.crm.service.kpi.model.WorkingTableTurnover;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Component
public class KpiTurnoverWithTaxWithDebour implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ResponsableService responsableService;

    @Override
    public String getCode() {
        return "TURNOVER_WITH_TAX_WITH_DEBOUR";
    }

    @Override
    public BigDecimal getDefaultValue() {
        return new BigDecimal(0);
    }

    @Override
    public String getAggregateTypeForResponsable() {
        return KpiCrm.AGGREGATE_TYPE_SUM;
    }

    @Override
    public String getAggregateTypeForTimePeriod() {
        return KpiCrm.AGGREGATE_TYPE_SUM;
    }

    @Override
    public String getLabelType() {
        return ReportingWidget.LABEL_TYPE_DATETIME;
    }

    @Override
    public String getUnit() {
        return "€";
    }

    @Override
    public String getIcon() {
        return "tablerCoinEuro";
    }

    @Override
    public Boolean getIsPositiveEvolutionGood() {
        return true;
    }

    @Override
    public String getGraphType() {
        return KpiCrm.GRAPH_TYPE_LINE;
    }

    @Override
    public void computeKpiCrmValues() throws OsirisException {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());
        if (kpiCrm == null)
            throw new OsirisException("KpiCrm not defined for code " + getCode());

        // Check working table exists
        @SuppressWarnings("unchecked")
        TypedQuery<Integer> q = (TypedQuery<Integer>) entityManager
                .createNativeQuery("select count(*) as nbr from reporting_turnover ", Integer.class);

        Integer nbr = (Integer) q.getSingleResult();

        if (nbr <= 0)
            return;

        LocalDate lastDate = kpiCrmValueService.getLastKpiCrmValueDate(kpiCrm);
        LocalDate today = LocalDate.now();

        @SuppressWarnings("unchecked")
        TypedQuery<WorkingTableTurnover> q2 = (TypedQuery<WorkingTableTurnover>) entityManager
                .createNativeQuery(
                        """
                                select id_responsable as idResponsable , cast (date_trunc('day' ,created_date) as date) as createdDate, sum(turnover_with_tax_with_debour) as turnover
                                from reporting_turnover rt
                                where created_date >=:startDate and created_date < :endDate
                                group by id_responsable , cast (date_trunc('day' ,created_date) as date)
                                """,
                        WorkingTableTurnover.class);
        q2.setParameter("startDate", lastDate);
        q2.setParameter("endDate", today);

        List<WorkingTableTurnover> turnoverValues = q2.getResultList();

        if (turnoverValues != null) {
            List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();
            HashMap<Integer, Responsable> responsableCache = new HashMap<Integer, Responsable>();
            for (WorkingTableTurnover turnoverValue : turnoverValues) {
                if (turnoverValue.getIdResponsable() != null) {
                    if (responsableCache.get(turnoverValue.getIdResponsable()) == null) {
                        responsableCache.put(turnoverValue.getIdResponsable(),
                                responsableService.getResponsable(turnoverValue.getIdResponsable()));
                    }
                    KpiCrmValue value = new KpiCrmValue();
                    value.setKpiCrm(kpiCrm);
                    value.setResponsable(responsableCache.get(turnoverValue.getIdResponsable()));
                    value.setValue(turnoverValue.getTurnover());
                    value.setValueDate(turnoverValue.getCreatedDate().toLocalDate());
                    newValues.add(value);
                }
            }

            kpiCrmService.saveValuesForKpiAndDay(kpiCrm, newValues);
        }
    }
}
