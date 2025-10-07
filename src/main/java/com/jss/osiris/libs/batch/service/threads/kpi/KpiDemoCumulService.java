package com.jss.osiris.libs.batch.service.threads.kpi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.invoicing.service.InvoiceService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

@Service
public class KpiDemoCumulService implements IKpiCrm {

    @Autowired
    InvoiceService invoiceService;

    @Autowired
    ConstantService constantService;

    @Autowired
    ResponsableService responsableService;

    @Override
    public String getCode() {
        return KpiCrm.DEMO_CUMUL;
    }

    @Override
    public List<KpiCrmValue> computeKpiCrmValues() {
        List<KpiCrmValue> dailyPerResponsableKpiValues = new ArrayList<>();
        List<Responsable> responsables = responsableService.getAllActiveResponsables().stream()
                .sorted(Comparator.comparing((Responsable r) -> -r.getTiers().getId()))
                .limit(200).collect(Collectors.toList());

        BigDecimal kpiTotal = BigDecimal.ONE;
        for (LocalDate date = LocalDate.of(2023, 01, 01); !date.isAfter(LocalDate.now()); date = date.plusDays(1)) {
            for (Responsable responsable : responsables) {
                KpiCrmValue kpiCrmValue = new KpiCrmValue();
                kpiCrmValue.setResponsable(responsable);
                kpiCrmValue.setValueDate(date);
                kpiCrmValue.setValue(kpiTotal);
                dailyPerResponsableKpiValues.add(kpiCrmValue);
            }
        }
        return dailyPerResponsableKpiValues;
    }

    @Override
    public LocalDate getClosestLastDate(LocalDate date) {
        return date.minusDays(3);
    }

    @Override
    public BigDecimal getDefaultValue() {
        return BigDecimal.ZERO;
    }

    @Override
    public String getAggregateType() {
        return KpiCrm.AGGREGATE_TYPE_SUM;
    }
}
