package com.jss.osiris.modules.osiris.crm.service.KpiThreads;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsType;
import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsValue;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;
import com.jss.osiris.modules.osiris.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Domiciliation;
import com.jss.osiris.modules.osiris.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;
import com.jss.osiris.modules.osiris.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvision;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@org.springframework.stereotype.Service
public class KpiOrderCompletionAverageTimeService implements IKpiCrm {
    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    AuditService auditService;

    @Autowired
    KpiCrmService kpiCrmService;

    public String getCode() {
        return KpiCrm.ORDER_COMPLETION_AVERAGE_TIME;
    }

    public String getAggregateType() {
        return KpiCrm.AGGREGATE_TYPE_AVERAGE;
    }

    public LocalDate getDate() {

        return null;
    }

    public Responsable getResponsable() {
        return null;
    }

    public String getLabel() {
        return kpiCrmService.getKpiCrmByCode(getCode()).getLabel();
    }

    @Transactional(rollbackFor = Exception.class)
    public List<KpiCrmValue> getComputeValue(Responsable responsable, LocalDate startDate, LocalDate endDate) {

        List<KpiCrmValue> dailyKpis = new ArrayList<>();

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            List<CustomerOrder> orders = customerOrderService
                    .getCustomerOrderByResponsableAndStatusAndDates(responsable, null, null, startOfDay, endOfDay);
            if (!orders.isEmpty()) {
                BigDecimal kpiTotal = BigDecimal.ZERO;
                int provisionCount = 0;
                for (CustomerOrder order : orders) {
                    if (!order.getAssoAffaireOrders().isEmpty()) {
                        for (AssoAffaireOrder asso : order.getAssoAffaireOrders())
                            if (!asso.getServices().isEmpty()) {
                                for (com.jss.osiris.modules.osiris.quotation.model.Service service : asso.getServices())
                                    if (!service.getProvisions().isEmpty()) {
                                        for (Provision provision : service.getProvisions()) {
                                            if (provision.getFormalite() != null) {
                                                kpiTotal.add(BigDecimal
                                                        .valueOf(sumStatusTime(Formalite.class.getSimpleName(),
                                                                provision.getFormalite().getId(),
                                                                FormaliteStatus.FORMALITE_WAITING_DOCUMENT,
                                                                "formaliteStatus")
                                                                .toMinutes()));
                                                provisionCount++;
                                            }
                                            if (provision.getSimpleProvision() != null) {
                                                kpiTotal.add(BigDecimal
                                                        .valueOf(sumStatusTime(SimpleProvision.class.getSimpleName(),
                                                                provision.getSimpleProvision().getId(),
                                                                SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT,
                                                                "simpleProvisionStatus")
                                                                .toMinutes()));
                                                provisionCount++;
                                            }
                                            if (provision.getDomiciliation() != null) {
                                                kpiTotal.add(BigDecimal
                                                        .valueOf(sumStatusTime(Domiciliation.class.getSimpleName(),
                                                                provision.getDomiciliation().getId(),
                                                                DomiciliationStatus.DOMICILIATION_WAITING_FOR_DOCUMENTS,
                                                                "domiciliationStatus")
                                                                .toMinutes()));
                                                provisionCount++;
                                            }
                                            if (provision.getAnnouncement() != null) {
                                                kpiTotal.add(BigDecimal
                                                        .valueOf(sumStatusTime(Announcement.class.getSimpleName(),
                                                                provision.getAnnouncement().getId(),
                                                                AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT,
                                                                "announcementStatus")
                                                                .toMinutes()));
                                                provisionCount++;
                                            }
                                        }
                                    }
                            }
                    }
                }
                kpiTotal.divide(BigDecimal.valueOf(provisionCount));
                if (provisionCount > 0) {
                    KpiCrmValue kpiCrmValue = new KpiCrmValue();
                    kpiCrmValue.setResponsable(responsable);
                    kpiCrmValue.setValueDate(date);
                    kpiCrmValue.setValue(kpiTotal.divide(BigDecimal.valueOf(provisionCount), RoundingMode.HALF_UP));
                    dailyKpis.add(kpiCrmValue);
                }
            }
        }
        return dailyKpis;
    }

    public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable> responsables, LocalDate startDate,
            LocalDate endDate) {
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);
        AnalyticStatsType analyticStatsType = new AnalyticStatsType();
        AnalyticStatsValue analyticStatsValue = new AnalyticStatsValue();
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());

        List<CustomerOrder> orders = customerOrderService.getOrdersByResponsablesAndDates(responsables, startOfDay,
                endOfDay);

        if (!orders.isEmpty()) {
            BigDecimal kpiTotal = BigDecimal.ZERO;
            int provisionCount = 0;
            for (CustomerOrder order : orders) {
                if (!order.getAssoAffaireOrders().isEmpty()) {
                    for (AssoAffaireOrder asso : order.getAssoAffaireOrders())
                        if (!asso.getServices().isEmpty()) {
                            for (com.jss.osiris.modules.osiris.quotation.model.Service service : asso.getServices())
                                if (!service.getProvisions().isEmpty()) {
                                    for (Provision provision : service.getProvisions()) {
                                        if (provision.getFormalite() != null) {
                                            kpiTotal = kpiTotal.add(BigDecimal
                                                    .valueOf(sumStatusTime(Formalite.class.getSimpleName(),
                                                            provision.getFormalite().getId(),
                                                            FormaliteStatus.FORMALITE_WAITING_DOCUMENT,
                                                            "formaliteStatus")
                                                            .toMinutes()));
                                            provisionCount++;
                                        }
                                        if (provision.getSimpleProvision() != null) {
                                            kpiTotal = kpiTotal.add(BigDecimal
                                                    .valueOf(sumStatusTime(SimpleProvision.class.getSimpleName(),
                                                            provision.getSimpleProvision().getId(),
                                                            SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT,
                                                            "simpleProvisionStatus")
                                                            .toMinutes()));
                                            provisionCount++;
                                        }
                                        if (provision.getDomiciliation() != null) {
                                            kpiTotal = kpiTotal.add(BigDecimal
                                                    .valueOf(sumStatusTime(Domiciliation.class.getSimpleName(),
                                                            provision.getDomiciliation().getId(),
                                                            DomiciliationStatus.DOMICILIATION_WAITING_FOR_DOCUMENTS,
                                                            "domiciliationStatus")
                                                            .toMinutes()));
                                            provisionCount++;
                                        }
                                        if (provision.getAnnouncement() != null) {
                                            kpiTotal = kpiTotal.add(BigDecimal
                                                    .valueOf(sumStatusTime(Announcement.class.getSimpleName(),
                                                            provision.getAnnouncement().getId(),
                                                            AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT,
                                                            "announcementStatus")
                                                            .toMinutes()));
                                            provisionCount++;
                                        }
                                    }
                                }
                        }
                }
            }
            analyticStatsValue.setValue(kpiTotal.divide(BigDecimal.valueOf(provisionCount)));
            analyticStatsType.setAnalyticStatsValue(analyticStatsValue);
            analyticStatsType.setValueDate(endDate);
            analyticStatsType.setId(kpiCrm.getId());
            analyticStatsType.setTitle(kpiCrm.getLabel());

        }
        return analyticStatsType;

    }

    // TODO corriger : si tjs en attente de doc, on ne le compte pas encore
    private Duration sumStatusTime(String entityType, Integer entityId, String entityStatus, String fieldName) {
        List<Audit> audits = new ArrayList<>();
        LocalDateTime waitingSince = null;
        Duration total = Duration.ZERO;
        audits = auditService.getAuditForEntityAndFieldName(
                entityType,
                entityId,
                entityStatus,
                fieldName);
        audits.sort(Comparator.comparing(Audit::getDatetime));

        for (Audit audit : audits) {
            if (entityType.equals(SimpleProvision.class.getSimpleName())) {
                if (audit.getNewValue()
                        .equals(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT)) {
                    waitingSince = audit.getDatetime();
                }
                if (audit.getOldValue()
                        .equals(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT)
                        && waitingSince != null) {
                    total = total
                            .plus(Duration.between(waitingSince,
                                    audit.getDatetime()));
                    waitingSince = null;
                }
            }
            if (entityType.equals(Formalite.class.getSimpleName())) {
                if (audit.getNewValue()
                        .equals(FormaliteStatus.FORMALITE_WAITING_DOCUMENT)) {
                    waitingSince = audit.getDatetime();
                }
                if (audit.getOldValue()
                        .equals(FormaliteStatus.FORMALITE_WAITING_DOCUMENT)
                        && waitingSince != null) {
                    total = total
                            .plus(Duration.between(waitingSince,
                                    audit.getDatetime()));
                    waitingSince = null;
                }
            }
            if (entityType.equals(Announcement.class.getSimpleName())) {
                if (audit.getNewValue()
                        .equals(AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT)) {
                    waitingSince = audit.getDatetime();
                }
                if (audit.getOldValue()
                        .equals(AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT)
                        && waitingSince != null) {
                    total = total
                            .plus(Duration.between(waitingSince,
                                    audit.getDatetime()));
                    waitingSince = null;
                }
            }
        }
        // if still waiting document, we put end date to sysdate for calculation
        if (waitingSince != null) {
            // total = total
            // .plus(Duration.between(waitingSince, LocalDateTime.now()));
        }
        return total;
    }

}
