package com.jss.osiris.modules.osiris.reporting.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.reporting.model.Indicator;
import com.jss.osiris.modules.osiris.reporting.model.Kpi;
import com.jss.osiris.modules.osiris.reporting.repository.KpiRepository;

@Service
public class KpiServiceImpl implements KpiService {

    @Autowired
    KpiRepository kpiRepository;

    @Override
    public List<Kpi> getKpis() {
        return IterableUtils.toList(kpiRepository.findAll());
    }

    @Override
    public Kpi getKpi(Integer id) {
        Optional<Kpi> kpi = kpiRepository.findById(id);
        if (kpi.isPresent())
            return kpi.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Kpi addOrUpdateKpi(
            Kpi kpi) {
        return kpiRepository.save(kpi);
    }

    @Override
    public Kpi getKpiForEmployeeAndDate(Indicator indicator, Employee employee, LocalDate date) {
        List<Kpi> kpis = indicator.getKpis();
        if (kpis != null) {
            kpis.sort(new Comparator<Kpi>() {
                @Override
                public int compare(Kpi o1, Kpi o2) {
                    return o2.getApplicationDate().compareTo(o1.getApplicationDate());
                }
            });
            for (Kpi kpi : kpis) {
                if (kpi.getEmployee().getId() == employee.getId())
                    if (date.isAfter(kpi.getApplicationDate().minusDays(1)))
                        return kpi;
            }
        }
        return null;
    }
}
