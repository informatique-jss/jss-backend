package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.repository.VatRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VatServiceImpl implements VatService {

    @Autowired
    VatRepository vatRepository;

    @Override
    public List<Vat> getVats() {
        return IterableUtils.toList(vatRepository.findAll());
    }

    @Override
    public Vat getVat(Integer id) {
        Optional<Vat> vat = vatRepository.findById(id);
        if (!vat.isEmpty())
            return vat.get();
        return null;
    }
	
	 @Override
    public Vat addOrUpdateVat(
            Vat vat) {
        return vatRepository.save(vat);
    }
}
