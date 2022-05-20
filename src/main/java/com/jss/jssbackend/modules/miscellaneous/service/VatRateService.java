package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.VatRate;

public interface VatRateService {
    public List<VatRate> getVatRates();

    public VatRate getVatRate(Integer id);
}
