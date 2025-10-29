package com.jss.osiris.libs.batch.service.threads.kpi;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.osiris.quotation.model.Domiciliation;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvision;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;

@org.springframework.stereotype.Service
public class KpiOrderCompletionAverageTimeThread implements IKpiCrm {

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    AuditService auditService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Override
    public String getLabelType() {
        return ReportingWidget.LABEL_TYPE_DATETIME;
    }

    private static final Map<String, String> WAITING_STATUS_BY_ENTITY = Map.of(
            SimpleProvision.class.getSimpleName(), SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT,
            Formalite.class.getSimpleName(), FormaliteStatus.FORMALITE_WAITING_DOCUMENT,
            Announcement.class.getSimpleName(), AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT,
            Domiciliation.class.getSimpleName(), DomiciliationStatus.DOMICILIATION_WAITING_FOR_DOCUMENTS);

    @Override
    public String getCode() {
        return KpiCrm.ORDER_COMPLETION_AVERAGE_TIME;
    }

    @Override
    public String getAggregateType() {
        return KpiCrm.AGGREGATE_TYPE_AVERAGE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<KpiCrmValue> computeKpiCrmValues() {
        List<KpiCrmValue> dailyKpis = new ArrayList<>();

        // for (LocalDate date = startDate; !date.isAfter(endDate); date =
        // date.plusDays(1)) {
        // LocalDateTime startOfDay = date.atStartOfDay();
        // LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // List<CustomerOrder> orders = customerOrderService
        // .getCustomerOrderByResponsableAndStatusAndDates(responsable, null, null,
        // startOfDay, endOfDay);

        // if (!orders.isEmpty()) {
        // BigDecimal kpiTotal = BigDecimal.ZERO;
        // int provisionCount = 0;

        // for (CustomerOrder order : orders) {
        // for (AssoAffaireOrder asso : order.getAssoAffaireOrders()) {
        // for (com.jss.osiris.modules.osiris.quotation.model.Service service :
        // asso.getServices()) {
        // for (Provision provision : service.getProvisions()) {
        // BigDecimal time = computeProvisionWaitingTime(provision);
        // if (time.compareTo(BigDecimal.ZERO) > 0) {
        // kpiTotal = kpiTotal.add(time);
        // provisionCount++;
        // }
        // }
        // }
        // }
        // }

        // if (provisionCount > 0) {
        // KpiCrmValue kpiCrmValue = new KpiCrmValue();
        // kpiCrmValue.setResponsable(responsable);
        // kpiCrmValue.setValueDate(date);
        // kpiCrmValue.setValue(kpiTotal.divide(BigDecimal.valueOf(provisionCount),
        // RoundingMode.HALF_UP));
        // dailyKpis.add(kpiCrmValue);
        // }
        // }
        // }

        return dailyKpis;
    }

    // @Override
    // public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable>
    // responsables, LocalDate startDate,
    // LocalDate endDate) {
    // LocalDateTime startOfDay = startDate.atStartOfDay();
    // LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
    // AnalyticStatsType analyticStatsType = new AnalyticStatsType();
    // AnalyticStatsValue analyticStatsValue = new AnalyticStatsValue();
    // KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());

    // List<CustomerOrder> orders =
    // customerOrderService.getOrdersByResponsablesAndDates(responsables,
    // startOfDay,
    // endOfDay);

    // if (!orders.isEmpty()) {
    // BigDecimal kpiTotal = BigDecimal.ZERO;
    // int provisionCount = 0;

    // for (CustomerOrder order : orders) {
    // for (AssoAffaireOrder asso : order.getAssoAffaireOrders()) {
    // for (com.jss.osiris.modules.osiris.quotation.model.Service service :
    // asso.getServices()) {
    // for (Provision provision : service.getProvisions()) {
    // BigDecimal time = computeProvisionWaitingTime(provision);
    // if (time.compareTo(BigDecimal.ZERO) > 0) {
    // kpiTotal = kpiTotal.add(time);
    // provisionCount++;
    // }
    // }
    // }
    // }
    // }

    // if (provisionCount > 0) {
    // analyticStatsValue.setValue(kpiTotal.divide(BigDecimal.valueOf(provisionCount),
    // RoundingMode.HALF_UP));
    // analyticStatsValue.setSuffix("Heures");
    // analyticStatsType.setAnalyticStatsValue(analyticStatsValue);
    // analyticStatsType.setValueDate(endDate);
    // analyticStatsType.setId(kpiCrm.getId());
    // analyticStatsType.setTitle(kpiCrm.getLabel());
    // }
    // }
    // return analyticStatsType;
    // }

    private BigDecimal computeProvisionWaitingTime(Provision provision) {
        BigDecimal total = BigDecimal.ZERO;

        if (provision.getFormalite() != null) {
            total = total.add(BigDecimal.valueOf(sumStatusTime(
                    Formalite.class.getSimpleName(),
                    provision.getFormalite().getId(),
                    FormaliteStatus.FORMALITE_WAITING_DOCUMENT,
                    "formaliteStatus").toHours()));
        }

        if (provision.getSimpleProvision() != null) {
            total = total.add(BigDecimal.valueOf(sumStatusTime(
                    SimpleProvision.class.getSimpleName(),
                    provision.getSimpleProvision().getId(),
                    SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT,
                    "simpleProvisionStatus").toHours()));
        }

        if (provision.getDomiciliation() != null) {
            total = total.add(BigDecimal.valueOf(sumStatusTime(
                    Domiciliation.class.getSimpleName(),
                    provision.getDomiciliation().getId(),
                    DomiciliationStatus.DOMICILIATION_WAITING_FOR_DOCUMENTS,
                    "domiciliationStatus").toHours()));
        }

        if (provision.getAnnouncement() != null) {
            total = total.add(BigDecimal.valueOf(sumStatusTime(
                    Announcement.class.getSimpleName(),
                    provision.getAnnouncement().getId(),
                    AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT,
                    "announcementStatus").toHours()));
        }

        return total;
    }

    private Duration sumStatusTime(String entityType, Integer entityId, String entityStatus, String fieldName) {
        String targetStatus = WAITING_STATUS_BY_ENTITY.get(entityType);
        if (targetStatus == null)
            return Duration.ZERO;

        List<Audit> audits = auditService.getAuditForEntityAndFieldName(entityType, entityId, entityStatus, fieldName);
        audits.sort(Comparator.comparing(Audit::getDatetime));

        Duration total = Duration.ZERO;
        LocalDateTime waitingSince = null;

        for (Audit audit : audits) {
            if (targetStatus.equals(audit.getNewValue())) {
                waitingSince = audit.getDatetime();
            } else if (targetStatus.equals(audit.getOldValue()) && waitingSince != null) {
                total = total.plus(Duration.between(waitingSince, audit.getDatetime()));
                waitingSince = null;
            }
        }

        return total;
    }

    @Override
    public BigDecimal getDefaultValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDefaultValue'");
    }
}
