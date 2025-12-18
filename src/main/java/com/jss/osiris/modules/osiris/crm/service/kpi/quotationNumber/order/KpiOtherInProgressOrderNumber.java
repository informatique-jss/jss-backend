package com.jss.osiris.modules.osiris.crm.service.kpi.quotationNumber.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmCategory;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.crm.service.kpi.QuotationReportingGroupHelper;
import com.jss.osiris.modules.osiris.crm.service.kpi.ReportingGroup;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class KpiOtherInProgressOrderNumber implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationReportingGroupHelper quotationReportingGroupHelper;

    @PersistenceContext
    EntityManager em;

    @Autowired
    AuditService auditService;

    @Override
    public String getCode() {
        return "OTHER_IN_PROGRESS_ORDER_NUMBER";
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
        return null;
    }

    @Override
    public String getIcon() {
        return "tablerWriting";
    }

    @Override
    public Boolean getIsPositiveEvolutionGood() {
        return true;
    }

    @Override
    public String getGraphType() {
        return KpiCrm.GRAPH_TYPE_BAR;
    }

    @Override
    public void computeKpiCrmValues() throws OsirisException {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());
        if (kpiCrm == null)
            throw new OsirisException("KpiCrm not defined for code " + getCode());

        LocalDate lastDate = kpiCrmValueService.getLastKpiCrmValueDate(kpiCrm);

        while (lastDate.isBefore(LocalDate.now())) {
            List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();

            List<CustomerOrder> orders = customerOrderService.getByCreatedDateBetweenAndStatus(lastDate.atStartOfDay(),
                    lastDate.atTime(23, 59, 59), null, lastDate.atStartOfDay(), lastDate.atTime(23, 59, 59));

            if (orders != null) {
                orders.sort(new Comparator<CustomerOrder>() {
                    @Override
                    public int compare(CustomerOrder firstOrder, CustomerOrder secondOrder) {
                        if (firstOrder.getResponsable() != null && secondOrder.getResponsable() == null)
                            return 1;
                        if (firstOrder.getResponsable() == null && secondOrder.getResponsable() != null)
                            return -1;
                        if (firstOrder.getResponsable() == null && secondOrder.getResponsable() == null)
                            return 0;
                        if (firstOrder.getResponsable().getId() > secondOrder.getResponsable().getId())
                            return 1;
                        if (firstOrder.getResponsable().getId() < secondOrder.getResponsable().getId())
                            return -1;
                        return 0;
                    }
                });

                Responsable currentResponsable = null;
                Integer nbrQuotation = 0;

                for (CustomerOrder order : orders) {
                    if (quotationReportingGroupHelper.getQuotationType(order)
                            .equals(ReportingGroup.FOURNITURES)
                            || quotationReportingGroupHelper.getQuotationType(order)
                                    .equals(ReportingGroup.DOMICILIATION)
                            || quotationReportingGroupHelper.getQuotationType(order)
                                    .equals(ReportingGroup.JSS_FR)) {
                        if (order.getResponsable() != null
                                && isStatusModifiedThisDay(order, lastDate,
                                        CustomerOrderStatus.BEING_PROCESSED)) {
                            if (currentResponsable == null
                                    || !order.getResponsable().getId().equals(currentResponsable.getId())) {
                                if (!(new BigDecimal(nbrQuotation)).equals(getDefaultValue())) {
                                    KpiCrmValue value = new KpiCrmValue();
                                    value.setKpiCrm(kpiCrm);
                                    value.setResponsable(currentResponsable);
                                    value.setValue(new BigDecimal(nbrQuotation));
                                    value.setValueDate(lastDate);
                                    newValues.add(value);
                                }
                                currentResponsable = order.getResponsable();
                                nbrQuotation = 0;
                            }
                            nbrQuotation++;
                        }
                    }
                }

                if (!(new BigDecimal(nbrQuotation)).equals(getDefaultValue())) {
                    KpiCrmValue value = new KpiCrmValue();
                    value.setKpiCrm(kpiCrm);
                    value.setResponsable(currentResponsable);
                    value.setValue(new BigDecimal(nbrQuotation));
                    value.setValueDate(lastDate);
                    newValues.add(value);
                }

                if (newValues != null && newValues.size() > 0) {
                    kpiCrmService.saveValuesForKpiAndDay(kpiCrm, newValues);
                    em.flush();
                    em.clear();
                }
            }

            lastDate = lastDate.plusDays(1);
        }
    }

    private boolean isStatusModifiedThisDay(CustomerOrder order, LocalDate lastDate, String targetCode) {
        if (order != null) {
            List<Audit> audits = auditService.getAuditForEntityAndFieldName(CustomerOrder.class.getSimpleName(),
                    order.getId(), targetCode, "customerOrderStatus");
            if (audits != null) {
                for (Audit audit : audits) {
                    LocalDate auditDate = audit.getDatetime().toLocalDate();
                    if (auditDate.equals(lastDate) && audit.getNewValue().equals(targetCode))
                        return true;
                }
            }
        }
        return false;
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
