package com.jss.osiris.modules.osiris.crm.service.KpiThreads;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsType;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class KpiMeasuredRevenueCumulService implements IKpiCrm {
    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @Override
    public String getCode() {
        return KpiCrm.MEASURED_REVENUE_CUMUL;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getAggregateType() {
        return KpiCrm.AGGREGATE_TYPE_CUMUL;
    }

    @Override
    public List<KpiCrmValue> getComputeValue(Responsable responsable, LocalDate startDate, LocalDate endDate) {
        List<KpiCrmValue> dailyKpis = new ArrayList<>();
        List<Invoice> invoices = null;
        try {
            invoices = invoiceService.searchInvoices(
                    List.of(constantService.getInvoiceStatusSend(), constantService.getInvoiceStatusCancelled(),
                            constantService.getInvoiceStatusCreditNoteEmited(),
                            constantService.getInvoiceStatusPayed()),
                    List.of(responsable));
        } catch (OsirisException e) {
            e.printStackTrace();
        }

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            BigDecimal kpiTotal = BigDecimal.ZERO;
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

            invoices.stream().filter(order -> !order.getCreatedDate().isBefore(startOfDay) &&
                    !order.getCreatedDate().isAfter(endOfDay))
                    .toList();

            if (!invoices.isEmpty())
                for (Invoice invoice : invoices)
                    if (!invoice.getInvoiceItems().isEmpty())
                        for (InvoiceItem invoiceItem : invoice.getInvoiceItems())
                            if (invoiceItem.getBillingItem() != null
                                    && invoiceItem.getBillingItem().getBillingType() != null) {
                                try {
                                    if (invoice.getInvoiceStatus().getId()
                                            .equals(constantService.getInvoiceStatusCreditNoteEmited().getId()))
                                        kpiTotal.add(invoiceItem.getPreTaxPrice().negate());
                                    if (invoiceItem.getBillingItem().getBillingType().getIsDebour())
                                        kpiTotal.add(BigDecimal.ZERO);
                                    else
                                        kpiTotal.add(
                                                invoiceItem.getPreTaxPrice()
                                                        .subtract(invoiceItem.getDiscountAmount() != null
                                                                ? invoiceItem.getDiscountAmount()
                                                                : BigDecimal.ZERO));

                                } catch (OsirisException e) {
                                    e.printStackTrace();
                                }
                            }
            KpiCrmValue kpiCrmValue = new KpiCrmValue();
            kpiCrmValue.setResponsable(responsable);
            kpiCrmValue.setValueDate(date);
            kpiCrmValue.setValue(kpiTotal);
            dailyKpis.add(kpiCrmValue);
        }
        return dailyKpis;
    }

    @Override
    public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable> responsables, LocalDate startDate,
            LocalDate endDate) {
        return null;
    }

}
