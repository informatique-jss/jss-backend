package com.jss.osiris.modules.osiris.crm.service.kpi.turnover;

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
import com.jss.osiris.modules.osiris.crm.model.KpiCrmCategory;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.crm.service.kpi.model.WorkingTableTurnoverRatio;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Component
public class KpiTurnoverRatio implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    EmployeeService employeeService;

    private HashMap<Integer, Employee> employeeCache = new HashMap<Integer, Employee>();

    @Override
    public String getCode() {
        return "TURNOVER_RATIO";
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
        return KpiCrm.AGGREGATE_TYPE_WEIGHTED_AVERAGE;
    }

    @Override
    public String getLabelType() {
        return ReportingWidget.LABEL_TYPE_DATETIME;
    }

    @Override
    public String getUnit() {
        return "%";
    }

    @Override
    public String getIcon() {
        return "tablerPercentage20";
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
        TypedQuery<WorkingTableTurnoverRatio> q2 = (TypedQuery<WorkingTableTurnoverRatio>) entityManager
                .createNativeQuery(
                        """
                                select
                                     idResponsable ,
                                     createdDate ,
                                     turnover /(sum(turnover) over (partition by id_tiers)) as turnover,
                                     sum(turnover) over (partition by id_tiers) as tiersTurnover,
                                     id_commercial as idCommercial
                                 from
                                     (
                                     select
                                         id_responsable as idResponsable ,
                                         cast (date_trunc('day' , created_date) as date) as createdDate,
                                         sum(turnover_without_tax_without_debour) as turnover,
                                         rt.id_tiers, rt.id_commercial
                                     from
                                         reporting_turnover rt
                                     join responsable r on
                                         r.id = rt.id_responsable
                                     where
                                         created_date >=:startDate
                                         and created_date < :endDate
                                     group by
                                         id_responsable ,rt.id_commercial,
                                         cast (date_trunc('day' , created_date) as date),
                                         rt.id_tiers)
                                         where turnover >0
                                     """,
                        WorkingTableTurnoverRatio.class);
        q2.setParameter("startDate", lastDate);
        q2.setParameter("endDate", today);

        List<WorkingTableTurnoverRatio> turnoverValues = q2.getResultList();

        if (turnoverValues != null) {
            List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();
            HashMap<Integer, Responsable> responsableCache = new HashMap<Integer, Responsable>();
            for (WorkingTableTurnoverRatio turnoverValue : turnoverValues) {
                if (turnoverValue.getIdResponsable() != null && turnoverValue.getTiersTurnover() != null) {
                    if (responsableCache.get(turnoverValue.getIdResponsable()) == null) {
                        responsableCache.put(turnoverValue.getIdResponsable(),
                                responsableService.getResponsable(turnoverValue.getIdResponsable()));
                    }
                    KpiCrmValue value = new KpiCrmValue();
                    value.setKpiCrm(kpiCrm);
                    value.setResponsable(responsableCache.get(turnoverValue.getIdResponsable()));
                    value.setOverridedSalesEmployee(getEmployee(turnoverValue.getIdCommercial()));
                    value.setValue(turnoverValue.getTurnover().multiply(new BigDecimal(100)));
                    value.setWeight(turnoverValue.getTiersTurnover().intValue());
                    value.setValueDate(turnoverValue.getCreatedDate().toLocalDate());
                    newValues.add(value);
                }
            }

            kpiCrmService.saveValuesForKpiAndDay(kpiCrm, newValues);
        }
    }

    private Employee getEmployee(Integer idEmployee) {
        if (idEmployee == null) {
            return null;
        }
        if (employeeCache.get(idEmployee) == null) {
            employeeCache.put(idEmployee, employeeService.getEmployee(idEmployee));
        }
        return employeeCache.get(idEmployee);
    }

    @Override
    public String getKpiCrmCategoryCode() {
        return KpiCrmCategory.TURNOVER;
    }

    @Override
    public Integer getDisplayOrder() {
        return null;
    }
}
