package com.jss.osiris.modules.osiris.crm.service.KpiThreads;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.AnalyticStatsType;
import com.jss.osiris.modules.osiris.crm.model.IKpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class KpiOverdueBalanceService implements IKpiCrm {

    @Override
    public String getCode() {
        return KpiCrm.OVERDUE_BALANCE;
    }

    @Override
    public String getLabel() {
        return null;
    }

    @Override
    public String getAggregateType() {
        return null;
    }

    public List<KpiCrmValue> getComputeValue(Responsable responsable, LocalDate startDate, LocalDate enDate) {
        return null;
    }

    @Override
    public AnalyticStatsType getKpiCrmAggregatedValue(List<Responsable> responsables, LocalDate startDate,
            LocalDate endDate) {
        return null;
    }

}
