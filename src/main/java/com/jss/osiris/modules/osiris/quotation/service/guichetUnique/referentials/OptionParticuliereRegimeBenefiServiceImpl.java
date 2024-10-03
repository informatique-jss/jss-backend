package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.OptionParticuliereRegimeBenefi;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.OptionParticuliereRegimeBenefiRepository;

@Service
public class OptionParticuliereRegimeBenefiServiceImpl implements OptionParticuliereRegimeBenefiService {

    @Autowired
    OptionParticuliereRegimeBenefiRepository OptionParticuliereRegimeBenefiRepository;

    @Override
    public List<OptionParticuliereRegimeBenefi> getOptionParticuliereRegimeBenefi() {
        return IterableUtils.toList(OptionParticuliereRegimeBenefiRepository.findAll());
    }
}
