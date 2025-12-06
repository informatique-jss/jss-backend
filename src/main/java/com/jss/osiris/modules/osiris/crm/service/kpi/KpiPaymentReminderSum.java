package com.jss.osiris.modules.osiris.crm.service.kpi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
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
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class KpiPaymentReminderSum implements IKpiThread {

    @Autowired
    KpiCrmValueService kpiCrmValueService;

    @Autowired
    KpiCrmService kpiCrmService;

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @PersistenceContext
    EntityManager em;

    @Override
    public String getCode() {
        return "PAYMENT_REMINDER_SUM";
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
        return "tablerTaxEuro";
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

        List<Invoice> customerInvoices = invoiceService.getInvoicesByStatus(constantService.getInvoiceStatusPayed(),
                true);
        List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();

        if (customerInvoices != null) {
            customerInvoices.sort(new Comparator<Invoice>() {
                @Override
                public int compare(Invoice firstInvoice, Invoice secondInvoice) {
                    if (firstInvoice.getResponsable() != null && secondInvoice.getResponsable() == null)
                        return 1;
                    if (firstInvoice.getResponsable() == null && secondInvoice.getResponsable() != null)
                        return -1;
                    if (firstInvoice.getResponsable() == null && secondInvoice.getResponsable() == null)
                        return 0;
                    if (firstInvoice.getResponsable().getId() > secondInvoice.getResponsable().getId())
                        return 1;
                    if (firstInvoice.getResponsable().getId() < secondInvoice.getResponsable().getId())
                        return -1;
                    return 0;
                }
            });

            Responsable currentResponsable = null;
            List<Invoice> currentResponsableInvoices = new ArrayList<Invoice>();

            for (Invoice invoice : customerInvoices) {
                if (invoice.getResponsable() != null) {
                    if (currentResponsable == null
                            || !invoice.getResponsable().getId().equals(currentResponsable.getId())) {
                        if (currentResponsableInvoices != null)
                            newValues.addAll(getKpiCrmValuesForResponsableInvoice(currentResponsableInvoices, kpiCrm,
                                    currentResponsable));

                        currentResponsable = invoice.getResponsable();
                        currentResponsableInvoices = new ArrayList<Invoice>();
                    }
                    currentResponsableInvoices.add(invoice);
                }
            }

            if (newValues != null && newValues.size() > 0) {
                kpiCrmService.saveValuesForKpiAndDay(kpiCrm, newValues);
            }
        }
    }

    private LocalDate getLastReminderDate(Invoice invoice) {
        if (invoice.getThirdReminderDateTime() != null)
            return invoice.getThirdReminderDateTime().toLocalDate();
        if (invoice.getThirdReminderDateTime() != null)
            return invoice.getThirdReminderDateTime().toLocalDate();
        if (invoice.getThirdReminderDateTime() != null)
            return invoice.getThirdReminderDateTime().toLocalDate();
        return invoice.getDueDate();
    }

    private List<KpiCrmValue> getKpiCrmValuesForResponsableInvoice(
            List<Invoice> currentResponsableInvoices, KpiCrm kpiCrm, Responsable currentResponsable) {

        Integer currentScore = getDefaultValue().intValue();
        Integer numberInvoices = 0;
        List<KpiCrmValue> newValues = new ArrayList<KpiCrmValue>();

        currentResponsableInvoices.sort(new Comparator<Invoice>() {
            @Override
            public int compare(Invoice firstInvoice, Invoice secondInvoice) {
                if (getLastReminderDate(firstInvoice) != null && getLastReminderDate(secondInvoice) == null)
                    return 1;
                if (getLastReminderDate(firstInvoice) == null && getLastReminderDate(secondInvoice) != null)
                    return -1;
                return getLastReminderDate(firstInvoice).compareTo(getLastReminderDate(secondInvoice));
            }
        });

        LocalDate startDate = LocalDate.of(2025, 1, 1);
        int currentMonth = startDate.getMonthValue();
        while (startDate.isBefore(LocalDate.now())) {

            for (Invoice invoice : currentResponsableInvoices) {
                if (getLastReminderDate(invoice).isEqual(startDate)) {
                    if (invoice.getThirdReminderDateTime() != null || invoice.getSecondReminderDateTime() != null
                            || invoice.getFirstReminderDateTime() != null)
                        currentScore--;
                    else
                        currentScore++;
                    numberInvoices++;
                }
            }

            if (!currentScore.equals(getDefaultValue().intValue()) && startDate.getMonthValue() != currentMonth) {
                KpiCrmValue value = new KpiCrmValue();
                value.setKpiCrm(kpiCrm);
                value.setResponsable(currentResponsable);
                value.setValue(new BigDecimal(currentScore));
                value.setWeight(numberInvoices);
                value.setValueDate(startDate);
                currentMonth = startDate.getMonthValue();
                newValues.add(value);
            }

            startDate = startDate.plusDays(1);
        }

        if (!currentScore.equals(getDefaultValue().intValue())) {
            KpiCrmValue value = new KpiCrmValue();
            value.setKpiCrm(kpiCrm);
            value.setResponsable(currentResponsable);
            value.setValue(new BigDecimal(currentScore));
            value.setWeight(numberInvoices);
            value.setValueDate(startDate);
            newValues.add(value);
        }

        return newValues;
    }
}
