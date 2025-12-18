package com.jss.osiris.modules.osiris.crm.service.kpi;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Component
public class KpiTimeWaitingInvoicePayment implements IKpiThread {

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

    @Autowired
    ConstantService constantService;

    @Override
    public String getCode() {
        return "TIME_WAITING_INVOICE_PAYMENT";
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
        return "tablerReceiptEuro";
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
                            id_responsable as idResponsable,
                            count(distinct id)::int as weight,
                            resultDate::date,
                            ceil(extract(EPOCH from avg(interval))/ 3600 / 24 * 10)/ 10 as value
                        from
                            (
                            select
                                i.id,
                                i.id_responsable,
                                date_trunc('day', coalesce(ar.lettering_date_time , car.lettering_date_time )) as resultDate,
                                max(coalesce(ar.lettering_date_time , car.lettering_date_time ))-i.created_date  as interval
                            from
                                invoice i
                            left join accounting_record ar on
                                ar.id_invoice = i.id
                            left join closed_accounting_record car on
                                car.id_invoice = i.id
                            join accounting_account aa on
                                aa.id = coalesce(ar.id_accounting_account, car.id_accounting_account)
                            where
                                i.id_invoice_status = :invoiceStatusId and i.id_responsable is not null
                                and aa.id_principal_accounting_account = :principalAccountingAccountTiers
                                and coalesce(ar.lettering_date_time , car.lettering_date_time ) is not null
                            group by
                                i.id,
                                i.id_responsable,
                                date_trunc('day', coalesce(ar.lettering_date_time , car.lettering_date_time )),
                                i.created_date
                        ) m
                        group by
                            id_responsable ,
                            resultDate
                                        """,
                WaitingAttachmentResult.class);
        q2.setParameter("invoiceStatusId", constantService.getInvoiceStatusPayed().getId());
        q2.setParameter("principalAccountingAccountTiers",
                constantService.getPrincipalAccountingAccountCustomer().getId());

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
        return KpiCrmCategory.ACCOUNTING;
    }

    @Override
    public Integer getDisplayOrder() {
        return null;
    }
}
