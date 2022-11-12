package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OptionParticuliereRegimeBenefi;
import com.jss.osiris.modules.quotation.repository.guichetUnique.OptionParticuliereRegimeBenefiRepository;

@Service
public class OptionParticuliereRegimeBenefiServiceImpl implements OptionParticuliereRegimeBenefiService {

    @Autowired
    OptionParticuliereRegimeBenefiRepository OptionParticuliereRegimeBenefiRepository;

    @Override
    @Cacheable(value = "optionParticuliereRegimeBenefiList", key = "#root.methodName")
    public List<OptionParticuliereRegimeBenefi> getOptionParticuliereRegimeBenefi() {
        return IterableUtils.toList(OptionParticuliereRegimeBenefiRepository.findAll());
    }
}
