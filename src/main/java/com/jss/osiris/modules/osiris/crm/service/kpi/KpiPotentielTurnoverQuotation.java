package com.jss.osiris.modules.osiris.crm.service.kpi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.IKpiThread;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmValueService;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.quotation.model.QuotationStatus;
import com.jss.osiris.modules.osiris.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationService;
import com.jss.osiris.modules.osiris.quotation.service.QuotationStatusService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Component
public class KpiPotentielTurnoverQuotation implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    QuotationService quotationService;

    @Autowired
    QuotationStatusService quotationStatusService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Override
    public String getCode() {
        return "POTENTIAL_TURNOVER_QUOTATION";
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
        return "tablerZoomQuestion";
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

        List<KpiCrmValue> newValues = getKpiValues(kpiCrm);

        if (newValues != null && newValues.size() > 0) {
            kpiCrmValueService.deleteKpiCrmValuesForKpiCrm(kpiCrm);
            kpiCrmService.saveValuesForKpiAndDay(kpiCrm, newValues);
        }
    }

    public List<KpiCrmValue> getKpiValues(KpiCrm kpiCrm) throws OsirisException {
        List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();

        List<Quotation> quotations = quotationService.searchQuotation(null,
                Arrays.asList(quotationStatusService.getQuotationStatusByCode(QuotationStatus.SENT_TO_CUSTOMER)));

        if (quotations != null) {
            quotations.sort(new Comparator<Quotation>() {
                @Override
                public int compare(Quotation firstOrder, Quotation secondOrder) {
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
            Float quotationTurnover = 0f;
            int i = 0;
            for (Quotation quotation : quotations) {
                if (i > 10)
                    break;
                else
                    i++;
                if (currentResponsable == null
                        || !quotation.getResponsable().getId().equals(currentResponsable.getId())) {
                    if (!(new BigDecimal(quotationTurnover)).equals(getDefaultValue())) {
                        KpiCrmValue value = new KpiCrmValue();
                        value.setKpiCrm(kpiCrm);
                        value.setResponsable(currentResponsable);
                        value.setValue(new BigDecimal(quotationTurnover));
                        value.setValueDate(LocalDate.now());
                        newValues.add(value);
                    }
                    currentResponsable = quotation.getResponsable();
                    quotationTurnover = 0f;
                }
                quotationTurnover += customerOrderService.getInvoicingSummaryForIQuotation(quotation).getTotalPrice()
                        .floatValue();
            }

            if (newValues != null && newValues.size() > 0) {
                kpiCrmValueService.deleteKpiCrmValuesForKpiCrm(kpiCrm);
                kpiCrmService.saveValuesForKpiAndDay(kpiCrm, newValues);
            }
        }
        return newValues;
    }
}
