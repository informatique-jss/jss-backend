package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Vat;

public interface VatService {
    public List<Vat> getVats();

    public Vat getVat(Integer id);
	
	 public Vat addOrUpdateVat(Vat vat);
}
