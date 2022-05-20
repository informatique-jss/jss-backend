package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.VatRate;
import com.jss.jssbackend.modules.miscellaneous.repository.VatRateRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VatRateServiceImpl implements VatRateService {

    @Autowired
    VatRateRepository vatRateRepository;

    @Override
    public List<VatRate> getVatRates() {
        return IterableUtils.toList(vatRateRepository.findAll());
    }

    @Override
    public VatRate getVatRate(Integer id) {
        Optional<VatRate> vatRate = vatRateRepository.findById(id);
        if (!vatRate.isEmpty())
            return vatRate.get();
        return null;
    }
}
