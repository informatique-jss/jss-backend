package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.repository.VatRepository;

@Service
public class VatServiceImpl implements VatService {

    @Autowired
    VatRepository vatRepository;

    @Override
    @Cacheable(value = "vatList", key = "#root.methodName")
    public List<Vat> getVats() {
        return IterableUtils.toList(vatRepository.findAll());
    }

    @Override
    @Cacheable(value = "vat", key = "#id")
    public Vat getVat(Integer id) {
        Optional<Vat> vat = vatRepository.findById(id);
        if (!vat.isEmpty())
            return vat.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "vatList", allEntries = true),
            @CacheEvict(value = "vat", key = "#vat.id")
    })
    public Vat addOrUpdateVat(
            Vat vat) {
        return vatRepository.save(vat);
    }
}
