package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.Vat;

public interface VatService {
    public List<Vat> getVats();

    public Vat getVat(Integer id);

    public Vat getApplicableVat(Country country, Department departement, boolean isIndividual) throws Exception;

    public Vat addOrUpdateVat(Vat vat);

    public Vat getVatByCode(String code);
}
