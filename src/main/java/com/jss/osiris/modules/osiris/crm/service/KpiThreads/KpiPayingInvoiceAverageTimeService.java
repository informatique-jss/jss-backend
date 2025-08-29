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
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsType;
import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsValue;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class KpiPayingInvoiceAverageTimeService implements IKpiCrm {
    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @Autowired
    AuditService auditService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Override
    public String getCode() {
        return KpiCrm.PAYING_INVOICE_AVERAGE_TIME;
    }

    @Override
    public String getAggregateType() {
        return null;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public List<KpiCrmValue> getComputeValue(Responsable responsable, LocalDate startDate, LocalDate endDate) {
        List<KpiCrmValue> dailyKpis = new ArrayList<>();

        List<Invoice> invoices = new ArrayList<Invoice>();

        try {
            invoices = invoiceService.searchInvoices(List.of(constantService.getInvoiceStatusSend()),
                    List.of(responsable));
        } catch (OsirisException e) {
            e.printStackTrace();
        }
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            Integer count = 0;
            BigDecimal kpiTotal = BigDecimal.ZERO;
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            invoices.stream().filter(order -> !order.getCreatedDate().isBefore(startOfDay) &&
                    !order.getCreatedDate().isAfter(endOfDay))
                    .toList();

            for (Invoice invoice : invoices) {
                if (!invoice.getPayments().isEmpty()) {
                    invoice.getPayments().sort(new Comparator<Payment>() {
                        @Override
                        public int compare(Payment o1, Payment o2) {
                            if (o1 == null && o2 != null)
                                return 1;
                            else if (o1 != null && o2 == null)
                                return -1;
                            return o2.getPaymentDate().compareTo(o1.getPaymentDate());
                        }
                    });

                    LocalDateTime sentInvoiceDate = null;
                    LocalDateTime receivedPaymentDate = null;
                    List<Audit> audits = null;
                    try {
                        audits = auditService.getAuditForEntityAndFieldName(Invoice.class.getSimpleName(),
                                invoice.getId(), constantService.getInvoiceStatusSend().getId().toString(),
                                "invoiceStatus");
                        if (!audits.isEmpty()) {
                            audits.sort(Comparator.comparing(Audit::getDatetime));
                            sentInvoiceDate = audits.get(0).getDatetime();
                        }

                        Payment payment = invoice.getPayments().get(0);
                        while (payment.getOriginPayment() != null) {
                            payment = payment.getOriginPayment();
                        }
                        receivedPaymentDate = payment.getCreatedDate();
                    } catch (OsirisException e) {
                        e.printStackTrace();
                    }
                    if (receivedPaymentDate != null && sentInvoiceDate != null) {
                        kpiTotal = kpiTotal.add(
                                BigDecimal.valueOf(Duration.between(sentInvoiceDate, receivedPaymentDate).toMinutes()));
                        count++;
                    }
                }
            }
            if (count > 0) {
                KpiCrmValue kpiCrmValue = new KpiCrmValue();
                kpiCrmValue.setResponsable(responsable);
                kpiCrmValue.setValueDate(date);
                kpiCrmValue.setValue(kpiTotal.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP));
                dailyKpis.add(kpiCrmValue);
            }
        }
        return null;
    }

    @Override
    public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable> responsables, LocalDate startDate,
            LocalDate endDate) {

        List<Invoice> invoices = new ArrayList<Invoice>();
        Integer count = 0;
        BigDecimal kpiTotal = BigDecimal.ZERO;
        AnalyticStatsType analyticStatsType = new AnalyticStatsType();
        AnalyticStatsValue analyticStatsValue = new AnalyticStatsValue();
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());

        try {
            invoices = invoiceService.getInvoicesForResponsablesAndDates(
                    List.of(constantService.getInvoiceStatusSend()), responsables, startDate, endDate);

            for (Invoice invoice : invoices) {
                if (!invoice.getPayments().isEmpty()) {
                    invoice.getPayments().sort(new Comparator<Payment>() {
                        @Override
                        public int compare(Payment o1, Payment o2) {
                            if (o1 == null && o2 != null)
                                return 1;
                            else if (o1 != null && o2 == null)
                                return -1;
                            return o2.getPaymentDate().compareTo(o1.getPaymentDate());
                        }
                    });

                    LocalDateTime sentInvoiceDate = null;
                    LocalDateTime receivedPaymentDate = null;
                    List<Audit> audits = null;
                    try {
                        audits = auditService.getAuditForEntityAndFieldName(Invoice.class.getSimpleName(),
                                invoice.getId(), constantService.getInvoiceStatusSend().getId().toString(),
                                "invoiceStatus");
                        if (!audits.isEmpty()) {
                            audits.sort(Comparator.comparing(Audit::getDatetime));
                            sentInvoiceDate = audits.get(0).getDatetime();
                        }

                        Payment payment = invoice.getPayments().get(0);
                        while (payment.getOriginPayment() != null) {
                            payment = payment.getOriginPayment();
                        }
                        receivedPaymentDate = payment.getCreatedDate();
                    } catch (OsirisException e) {
                        e.printStackTrace();
                    }
                    if (receivedPaymentDate != null && sentInvoiceDate != null) {
                        kpiTotal = kpiTotal.add(
                                BigDecimal.valueOf(Duration.between(sentInvoiceDate, receivedPaymentDate).toMinutes()));
                        count++;
                    }
                }
            }

            if (count > 0) {
                analyticStatsValue.setValue(kpiTotal.divide(BigDecimal.valueOf(count), RoundingMode.HALF_UP));
                analyticStatsType.setAnalyticStatsValue(analyticStatsValue);
                analyticStatsType.setValueDate(endDate);
                analyticStatsType.setId(kpiCrm.getId());
                analyticStatsType.setTitle(kpiCrm.getLabel());
            }
        } catch (OsirisException e) {
            e.printStackTrace();
        }
        return analyticStatsType;
    }

}
