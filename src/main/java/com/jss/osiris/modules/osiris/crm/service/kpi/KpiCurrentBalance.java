package com.jss.osiris.modules.osiris.crm.service.kpi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmCategory;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceStatus;
import com.jss.osiris.modules.osiris.invoicing.model.Payment;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceHelper;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.invoicing.service.PaymentService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

@Component
public class KpiCurrentBalance implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    PaymentService paymentService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @Autowired
    InvoiceHelper invoiceHelper;

    @Override
    public String getCode() {
        return "CURRENT_BALANCE";
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
        return KpiCrm.AGGREGATE_TYPE_LAST_VALUE;
    }

    @Override
    public String getLabelType() {
        return ReportingWidget.LABEL_TYPE_CATEGORY;
    }

    @Override
    public String getUnit() {
        return "€";
    }

    @Override
    public String getIcon() {
        return "tablerScale";
    }

    @Override
    public Boolean getIsPositiveEvolutionGood() {
        return false;
    }

    @Override
    public String getGraphType() {
        return null;
    }

    @Override
    public void computeKpiCrmValues() throws OsirisException {
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());
        if (kpiCrm == null)
            throw new OsirisException("KpiCrm not defined for code " + getCode());

        List<KpiCrmValue> newValuesPayment = getKpiValuesForPayment(kpiCrm);
        List<KpiCrmValue> newValuesInvoice = getKpiValuesForInvoice(kpiCrm);

        // Aggregate all values
        List<KpiCrmValue> allValues = Stream.of(newValuesPayment, newValuesInvoice)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Map<AggregationKey, BigDecimal> aggregatedMap = allValues.stream()
                .collect(Collectors.groupingBy(
                        value -> new AggregationKey(
                                value.getResponsable().getId(),
                                value.getValueDate()),
                        Collectors.reducing(
                                BigDecimal.ZERO, // default value
                                KpiCrmValue::getValue, // mapped value
                                BigDecimal::add // mapped function
                        )));

        // Remapped to value
        List<KpiCrmValue> aggregatedList = aggregatedMap.entrySet().stream()
                .map(entry -> {
                    AggregationKey key = entry.getKey();
                    BigDecimal totalValue = entry.getValue();
                    KpiCrmValue aggregatedKpi = new KpiCrmValue();

                    aggregatedKpi.setKpiCrm(kpiCrm);
                    aggregatedKpi.setResponsable(responsableService.getResponsable(key.responsableId));
                    aggregatedKpi.setValueDate(key.valueDate);
                    aggregatedKpi.setValue(totalValue);

                    return aggregatedKpi;
                })
                .collect(Collectors.toList());

        if (aggregatedList != null && aggregatedList.size() > 0) {
            kpiCrmValueService.deleteKpiCrmValuesForKpiCrm(kpiCrm);
            kpiCrmService.saveValuesForKpiAndDay(kpiCrm, aggregatedList);
        }
    }

    public List<KpiCrmValue> getKpiValuesForPayment(KpiCrm kpiCrm) throws OsirisException {
        List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();
        List<Payment> payments = paymentService.getPaymentsForResponsable(null);

        if (payments != null) {
            payments.sort(new Comparator<Payment>() {
                @Override
                public int compare(Payment firstPayment, Payment secondPayment) {
                    if (getResponsableForPayment(firstPayment) != null
                            && getResponsableForPayment(secondPayment) == null)
                        return 1;
                    if (getResponsableForPayment(firstPayment) == null
                            && getResponsableForPayment(secondPayment) != null)
                        return -1;
                    if (getResponsableForPayment(firstPayment) == null
                            && getResponsableForPayment(secondPayment) == null)
                        return 0;
                    if (getResponsableForPayment(firstPayment).getId() > getResponsableForPayment(secondPayment)
                            .getId())
                        return 1;
                    if (getResponsableForPayment(firstPayment).getId() < getResponsableForPayment(secondPayment)
                            .getId())
                        return -1;
                    return 0;
                }
            });

            Responsable currentResponsable = null;
            Float paymentBalance = 0f;
            InvoiceStatus sendInvoice = constantService.getInvoiceStatusSend();

            for (Payment payment : payments) {
                if (currentResponsable == null
                        || !getResponsableForPayment(payment).getId().equals(currentResponsable.getId())) {
                    if (!(new BigDecimal(paymentBalance)).equals(getDefaultValue())) {
                        KpiCrmValue value = new KpiCrmValue();
                        value.setKpiCrm(kpiCrm);
                        value.setResponsable(currentResponsable);
                        value.setValue(new BigDecimal(paymentBalance));
                        value.setValueDate(LocalDate.now());
                        newValues.add(value);
                    }
                    currentResponsable = getResponsableForPayment(payment);
                    paymentBalance = 0f;
                }
                if (payment.getCustomerOrder() != null
                        || payment.getInvoice().getInvoiceStatus().getId().equals(sendInvoice.getId()))
                    paymentBalance -= payment.getPaymentAmount().floatValue();
            }
        }
        return newValues;
    }

    private Responsable getResponsableForPayment(Payment payment) {
        if (payment.getInvoice() != null)
            return payment.getInvoice().getResponsable();
        else if (payment.getCustomerOrder() != null)
            return payment.getCustomerOrder().getResponsable();
        return null;
    }

    public List<KpiCrmValue> getKpiValuesForInvoice(KpiCrm kpiCrm) throws OsirisException {
        List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();
        List<Invoice> invoices = invoiceService.getInvoicesByStatus(constantService.getInvoiceStatusSend(), true);

        if (invoices != null) {
            invoices.sort(new Comparator<Invoice>() {
                @Override
                public int compare(Invoice firstInvoice, Invoice secondInvoice) {
                    if (firstInvoice.getResponsable() != null
                            && secondInvoice.getResponsable() == null)
                        return 1;
                    if (firstInvoice.getResponsable() == null
                            && secondInvoice.getResponsable() != null)
                        return -1;
                    if (firstInvoice.getResponsable() == null
                            && secondInvoice.getResponsable() == null)
                        return 0;
                    if (firstInvoice.getResponsable().getId() > secondInvoice.getResponsable()
                            .getId())
                        return 1;
                    if (firstInvoice.getResponsable().getId() < secondInvoice.getResponsable()
                            .getId())
                        return -1;
                    return 0;
                }
            });

            Responsable currentResponsable = null;
            Float invoiceBalance = 0f;

            for (Invoice invoice : invoices) {
                if (invoice.getResponsable() == null)
                    continue;
                if (currentResponsable == null
                        || !invoice.getResponsable().getId().equals(currentResponsable.getId())) {
                    if (!(new BigDecimal(invoiceBalance)).equals(getDefaultValue())) {
                        KpiCrmValue value = new KpiCrmValue();
                        value.setKpiCrm(kpiCrm);
                        value.setResponsable(currentResponsable);
                        value.setValue(new BigDecimal(invoiceBalance));
                        value.setValueDate(LocalDate.now());
                        newValues.add(value);
                    }
                    currentResponsable = invoice.getResponsable();
                    invoiceBalance = 0f;
                }
                invoiceBalance += invoiceHelper.getPriceTotal(invoice).floatValue();
            }
        }
        return newValues;
    }

    private static class AggregationKey {
        private final Integer responsableId;
        private final LocalDate valueDate;

        public AggregationKey(Integer responsableId, LocalDate valueDate) {
            this.responsableId = responsableId;
            this.valueDate = valueDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            AggregationKey that = (AggregationKey) o;
            return responsableId == that.responsableId &&
                    valueDate.isEqual(that.valueDate);
        }

        @Override
        public int hashCode() {
            return Objects.hash(responsableId, valueDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
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
