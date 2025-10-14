package com.jss.osiris.libs.batch.service.threads.kpi;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.invoicing.model.InvoiceItem;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;

@Service
public class KpiPotentialRevenueCumulThread implements IKpiCrm {
    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    QuotationStatusService quotationStatusService;

    @Override
    public String getCode() {
        return KpiCrm.POTENTIAL_REVENUE_CUMUL;
    }

    @Override
    public String getLabelType() {
        return ReportingWidget.LABEL_TYPE_DATETIME;
    }

    @Override
    public String getAggregateType() {
        return KpiCrm.AGGREGATE_TYPE_SUM;
    }

    @Override
    public List<KpiCrmValue> computeKpiCrmValues() {
        List<KpiCrmValue> dailyKpis = new ArrayList<>();

        // for (LocalDate date = startDate; !date.isAfter(endDate); date =
        // date.plusDays(1)) {
        // LocalDateTime startOfDay = date.atStartOfDay();
        // LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // List<CustomerOrder> inProgressOrders =
        // customerOrderService.getCustomerOrderByResponsableAndStatusAndDates(
        // responsable,
        // customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED),
        // null,
        // startOfDay, endOfDay);
        // List<CustomerOrder> reccuringOrders = customerOrderService
        // .getCustomerOrderByResponsableAndStatusAndDates(responsable, null, true,
        // startOfDay, endOfDay);

        // List<Quotation> quotations =
        // quotationService.getQuotationsByResponsablesAndStatusAndDates(
        // List.of(responsable), startOfDay, endOfDay,
        // quotationStatusService.getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER));

        // BigDecimal revenueHt = BigDecimal.ZERO;
        // if (!inProgressOrders.isEmpty())
        // revenueHt.add(computeCumulForOrders(inProgressOrders));

        // if (!reccuringOrders.isEmpty())
        // revenueHt.add(computeCumulForOrders(reccuringOrders));

        // if (!quotations.isEmpty())
        // for (Quotation quotation : quotations)
        // for (AssoAffaireOrder asso : quotation.getAssoAffaireOrders())
        // for (com.jss.osiris.modules.osiris.quotation.model.Service service :
        // asso.getServices())
        // for (Provision provision : service.getProvisions())
        // for (InvoiceItem invoiceItem : provision.getInvoiceItems())
        // if (invoiceItem.getPreTaxPrice() != null)
        // revenueHt.add(invoiceItem.getPreTaxPrice());

        // KpiCrmValue kpiCrmValue = new KpiCrmValue();
        // kpiCrmValue.setResponsable(responsable);
        // kpiCrmValue.setValueDate(date);
        // kpiCrmValue.setValue(revenueHt);
        // dailyKpis.add(kpiCrmValue);
        // }

        return dailyKpis;
    }

    private BigDecimal computeCumulForOrders(List<CustomerOrder> customerOrders) {
        BigDecimal revenueHt = BigDecimal.ZERO;
        for (CustomerOrder currentOrder : customerOrders)
            for (AssoAffaireOrder asso : currentOrder.getAssoAffaireOrders())
                for (com.jss.osiris.modules.osiris.quotation.model.Service service : asso.getServices())
                    for (Provision provision : service.getProvisions())
                        for (InvoiceItem invoiceItem : provision.getInvoiceItems())
                            if (invoiceItem.getPreTaxPrice() != null)
                                revenueHt.add(invoiceItem.getPreTaxPrice());
        return revenueHt;
    }

    @Override
    public BigDecimal getDefaultValue() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDefaultValue'");
    }

    // @Override
    // public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable>
    // responsables, LocalDate startDate,
    // LocalDate endDate) {

    // return null;
    // }

}
