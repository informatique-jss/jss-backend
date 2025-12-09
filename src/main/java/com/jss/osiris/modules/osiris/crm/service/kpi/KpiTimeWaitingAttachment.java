package com.jss.osiris.modules.osiris.crm.service.kpi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmCategory;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.crm.service.kpi.model.WaitingAttachmentResult;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Component
public class KpiTimeWaitingAttachment implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @PersistenceContext
    EntityManager em;

    @Autowired
    AuditService auditService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Override
    public String getCode() {
        return "TIME_WAITING_ATTACHMENT";
    }

    @Override
    public BigDecimal getDefaultValue() {
        return new BigDecimal(0);
    }

    @Override
    public String getAggregateTypeForResponsable() {
        return KpiCrm.AGGREGATE_TYPE_WEIGHTED_AVERAGE;
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
        return "jours";
    }

    @Override
    public String getIcon() {
        return "tablerFileUnknown";
    }

    @Override
    public Boolean getIsPositiveEvolutionGood() {
        return false;
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

        kpiCrmValueService.deleteKpiCrmValuesForKpiCrm(kpiCrm);

        @SuppressWarnings("unchecked")
        TypedQuery<WaitingAttachmentResult> q2 = (TypedQuery<WaitingAttachmentResult>) em.createNativeQuery(
                """
                        select
                            co.id_responsable as idResponsable,
                            count(distinct m.id)::int as weight,
                            resultDate::date,
                            ceil(EXTRACT(EPOCH from avg(interval))/3600/24*10)/10 as value
                        from
                            (
                            select
                                maq.id,
                                maq.id_service,
                                date_trunc('day', min(a.datetime)) as resultDate,
                                min(a.datetime) -maq.created_date_time as interval
                            from
                                missing_attachment_query maq
                            join provision p on
                                p.id_service = maq.id_service
                            join audit a on
                                a.entity in ('Formalite', 'Announcement', 'SimpleProvision', 'Domiciliation')
                                and field_name like '%Status'
                                and entity_id = coalesce(p.id_announcement, p.id_formalite, p.id_domiciliation, p.id_simple_provision)
                                and a.old_value like '%_WAITING_DOCUMENT'
                                and datetime>maq.created_date_time
                            group by
                                maq.id,
                                maq.id_service,
                                maq.created_date_time
                        ) m
                        join service s on
                            s.id = m.id_service
                        join asso_affaire_order aao on
                            aao.id = s.id_asso_affaire_order
                        join customer_order co on
                            co.id = aao.id_customer_order
                        where
                            co.id_customer_order_status not in (:customerOrderStatusToExclude)
                        group by
                            co.id_responsable ,
                            resultDate
                                        """,
                WaitingAttachmentResult.class);
        q2.setParameter("customerOrderStatusToExclude",
                Arrays.asList(
                        customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED).getId(),
                        customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.DRAFT).getId()));

        List<WaitingAttachmentResult> waintingValues = q2.getResultList();

        if (waintingValues != null) {
            List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();
            HashMap<Integer, Responsable> responsableCache = new HashMap<Integer, Responsable>();
            for (WaitingAttachmentResult waitingValue : waintingValues) {
                if (waitingValue.getIdResponsable() != null) {
                    if (responsableCache.get(waitingValue.getIdResponsable()) == null) {
                        responsableCache.put(waitingValue.getIdResponsable(),
                                responsableService.getResponsable(waitingValue.getIdResponsable()));
                    }
                    KpiCrmValue value = new KpiCrmValue();
                    value.setKpiCrm(kpiCrm);
                    value.setResponsable(responsableCache.get(waitingValue.getIdResponsable()));
                    value.setValue(waitingValue.getValue());
                    value.setWeight(waitingValue.getWeight());
                    value.setValueDate(waitingValue.getResultDate().toLocalDate());
                    newValues.add(value);
                }
            }

            kpiCrmService.saveValuesForKpiAndDay(kpiCrm, newValues);
        }
    }

    @Override
    public String getKpiCrmCategoryCode() {
        return KpiCrmCategory.CUSTOMER_ORDER;
    }

    @Override
    public Integer getDisplayOrder() {
        return null;
    }
}
