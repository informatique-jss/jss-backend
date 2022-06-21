package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Vat;

public interface VatService {
    public List<Vat> getVat();

    public Vat getVat(Integer id);
}
