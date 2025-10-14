package com.jss.osiris.libs.batch.service.threads.kpi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.audit.model.Audit;
import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.service.KpiCrmService;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.reporting.model.ReportingWidget;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class KpiNbOverdueBalanceThread implements IKpiCrm {

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
        return KpiCrm.NB_OVERDUE_BALANCE;
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
        KpiCrm kpiCrm = kpiCrmService.getKpiCrmByCode(getCode());

        List<Invoice> invoices = new ArrayList<Invoice>();
        invoices = invoiceService.searchInvoicesWithFirstReminderDateTime();

        Map<Responsable, List<Invoice>> responsablesInvoicesMap = new HashMap<>();
        Responsable previousRespo = null;

        // Initialising the Map
        for (Invoice invoice : invoices) {
            Responsable currentRespo = invoice.getResponsable();

            if (previousRespo == null) {
                previousRespo = currentRespo;
                responsablesInvoicesMap.put(currentRespo,
                        invoices.stream().filter((inv) -> inv.getResponsable().getId().equals(currentRespo.getId()))
                                .toList());

            } else if (!currentRespo.getId().equals(previousRespo.getId())) {
                responsablesInvoicesMap.put(currentRespo,
                        invoices.stream().filter((inv) -> inv.getResponsable().getId().equals(currentRespo.getId()))
                                .toList());
                previousRespo = currentRespo;
            }
        }

        // Creation of KpiCrmValues for each responsable
        for (Responsable responsable : responsablesInvoicesMap.keySet()) {
            List<Invoice> invoicesOrdered = responsablesInvoicesMap.get(responsable).stream()
                    .sorted(Comparator.comparing(Invoice::getCreatedDate)).toList();

            // Limit the loop to when there are existing invoices
            for (LocalDate day = invoicesOrdered.get(0).getCreatedDate().toLocalDate(); day
                    .isBefore(LocalDate.now()); day.plusDays(1)) {
                BigDecimal valueForKpi = BigDecimal.ZERO;

                for (Invoice invoice : invoicesOrdered) {
                    Audit payedStatusAudit = auditService
                            .getAuditForEntityAndFieldName("Invoice", invoice.getId(), "5", "invoiceStatus").stream()
                            .filter((a) -> a.getNewValue().equals("5")).toList().get(0);

                    if (invoice.getFirstReminderDateTime().toLocalDate().isBefore(day)
                            && payedStatusAudit.getDatetime().toLocalDate().isAfter(LocalDate.now())) {
                        valueForKpi.add(BigDecimal.ONE);
                    }
                }

                KpiCrmValue kpiCrmValue = new KpiCrmValue();
                kpiCrmValue.setKpiCrm(kpiCrm);
                kpiCrmValue.setResponsable(responsable);
                kpiCrmValue.setValue(valueForKpi);
                kpiCrmValue.setValueDate(day);
                dailyKpis.add(kpiCrmValue);
            }
        }

        return dailyKpis;
    }

    @Override
    public BigDecimal getDefaultValue() {
        return BigDecimal.ZERO;
    }
}