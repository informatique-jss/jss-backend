package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Vat;

public interface VatService {
    public List<Vat> getVat();

    public Vat getVat(Integer id);
}
