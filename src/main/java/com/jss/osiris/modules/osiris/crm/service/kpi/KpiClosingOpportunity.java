package com.jss.osiris.modules.osiris.crm.service.kpi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class KpiClosingOpportunity implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    QuotationService quotationService;

    @PersistenceContext
    EntityManager em;

    @Autowired
    AuditService auditService;

    @Override
    public String getCode() {
        return "CLOSING_OPPORTUNITY";
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
        return "tablerHandLoveYou";
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

        LocalDate lastDate = kpiCrmValueService.getLastKpiCrmValueDate(kpiCrm);

        List<Quotation> quotations = quotationService.getByCreatedDateBetweenAndStatus(
                LocalDateTime.now().minusYears(100), LocalDateTime.now().plusYears(100), null, lastDate.atTime(0, 0, 0),
                LocalDate.now().minusDays(1).atTime(23, 59, 59));

        if (quotations != null) {
            quotations.sort(new Comparator<Quotation>() {
                @Override
                public int compare(Quotation firstQuotation, Quotation secondQuotation) {
                    if (firstQuotation.getResponsable() != null && secondQuotation.getResponsable() == null)
                        return 1;
                    if (firstQuotation.getResponsable() == null && secondQuotation.getResponsable() != null)
                        return -1;
                    if (firstQuotation.getResponsable() == null && secondQuotation.getResponsable() == null)
                        return 0;
                    if (firstQuotation.getResponsable().getId() > secondQuotation.getResponsable().getId())
                        return 1;
                    if (firstQuotation.getResponsable().getId() < secondQuotation.getResponsable().getId())
                        return -1;
                    return 0;
                }
            });

            List<Integer> idDone = new ArrayList<Integer>();

            while (lastDate.isBefore(LocalDate.now())) {
                List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();

                Responsable currentResponsable = null;
                Integer nbrQuotation = 0;
                long totalTimeInSeconds = 0;

                for (Quotation quotation : quotations) {
                    if (!idDone.contains(quotation.getId()))
                        if (quotation.getCreatedDate() != null
                                && (quotation.getCreatedDate().toLocalDate().equals(lastDate)
                                        || quotation.getCreatedDate().toLocalDate().isBefore(lastDate))) {
                            Audit endAudit = getAuditWhereStatusModifiedThisDay(quotation, lastDate,
                                    QuotationStatus.VALIDATED_BY_CUSTOMER, false);
                            if (quotation.getResponsable() != null && endAudit != null) {
                                if (currentResponsable == null
                                        || !quotation.getResponsable().getId().equals(currentResponsable.getId())) {
                                    if (!(new BigDecimal(nbrQuotation)).equals(getDefaultValue())) {
                                        KpiCrmValue value = new KpiCrmValue();
                                        value.setKpiCrm(kpiCrm);
                                        value.setResponsable(currentResponsable);
                                        value.setValue(
                                                new BigDecimal(totalTimeInSeconds * 1.0 / 3600 / 8 / nbrQuotation)
                                                        .setScale(1, RoundingMode.HALF_EVEN));
                                        value.setWeight(nbrQuotation);
                                        value.setValueDate(lastDate);
                                        newValues.add(value);
                                    }
                                    currentResponsable = quotation.getResponsable();
                                    nbrQuotation = 0;
                                    totalTimeInSeconds = 0;
                                }

                                Audit startAudit = getAuditWhereStatusModifiedThisDay(quotation, null,
                                        QuotationStatus.SENT_TO_CUSTOMER, true);
                                if (startAudit != null) {
                                    totalTimeInSeconds += endAudit.getDatetime().toEpochSecond(ZoneOffset.UTC)
                                            - startAudit.getDatetime().toEpochSecond(ZoneOffset.UTC);
                                    idDone.add(quotation.getId());
                                    nbrQuotation++;
                                }
                            }
                        }
                }

                if (!(new BigDecimal(nbrQuotation)).equals(getDefaultValue())) {
                    KpiCrmValue value = new KpiCrmValue();
                    value.setKpiCrm(kpiCrm);
                    value.setResponsable(currentResponsable);
                    value.setValue(new BigDecimal(totalTimeInSeconds * 1.0 / 3600 / 8 / nbrQuotation)
                            .setScale(1, RoundingMode.HALF_EVEN));
                    value.setWeight(nbrQuotation);
                    value.setValueDate(lastDate);
                    newValues.add(value);
                }

                if (newValues != null && newValues.size() > 0) {
                    kpiCrmService.saveValuesForKpiAndDay(kpiCrm, newValues);
                    em.flush();
                    em.clear();
                }

                lastDate = lastDate.plusDays(1);
            }

        }
    }

    private Audit getAuditWhereStatusModifiedThisDay(Quotation quotation, LocalDate lastDate, String targetCode,
            boolean isFirst) {
        if (quotation != null) {
            List<Audit> audits = auditService.getAuditForEntityAndFieldName(Quotation.class.getSimpleName(),
                    quotation.getId(), targetCode, "quotationStatus");
            if (audits != null) {
                audits.sort(new Comparator<Audit>() {
                    @Override
                    public int compare(Audit audit1, Audit audit2) {
                        if (isFirst) {
                            return audit1.getDatetime().compareTo(audit2.getDatetime());
                        } else {
                            return audit2.getDatetime().compareTo(audit1.getDatetime());
                        }
                    }
                });
                for (Audit audit : audits) {
                    LocalDate auditDate = audit.getDatetime().toLocalDate();
                    if ((lastDate == null || auditDate.equals(lastDate)) && audit.getNewValue().equals(targetCode))
                        return audit;
                }
            }
        }
        return null;
    }
}
