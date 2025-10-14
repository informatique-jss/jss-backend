package com.jss.osiris.modules.osiris.crm.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.crm.model.KpiCrm;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmValue;
import com.jss.osiris.modules.osiris.crm.repository.KpiCrmValueRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class KpiCrmValueServiceImpl implements KpiCrmValueService {

    @Autowired
    KpiCrmValueRepository kpiCrmValueRepository;

    @Override
    public KpiCrmValue getKpiCrmValue(Integer id) {
        Optional<KpiCrmValue> kpiCrmValue = kpiCrmValueRepository.findById(id);
        if (kpiCrmValue.isPresent())
            return kpiCrmValue.get();
        return null;
    }

    @Override
    public void addOrUpdateKpiCrmValues(List<KpiCrmValue> kpiCrmValues) {
        kpiCrmValueRepository.saveAll(kpiCrmValues);
    }

    @Override
    public List<KpiCrmValue> getValuesForKpiCrmAndResponsablesAndDates(KpiCrm kpiCrm, List<Responsable> responsables,
            LocalDate startDate, LocalDate endDate) {
        return kpiCrmValueRepository.findByKpiCrmAndResponsableInAndValueDateBetween(kpiCrm, responsables, startDate,
                endDate);
    }

    @Override
    public KpiCrmValue getLastKpiCrmValueByKpiCrm(Integer id) {
        return kpiCrmValueRepository.findFirstByKpiCrmIdOrderByValueDateDesc(id);
    }
}
