package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.PeriodiciteEtOptionsParticulie;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.PeriodiciteEtOptionsParticulieRepository;

@Service
public class PeriodiciteEtOptionsParticulieServiceImpl implements PeriodiciteEtOptionsParticulieService {

    @Autowired
    PeriodiciteEtOptionsParticulieRepository PeriodiciteEtOptionsParticulieRepository;

    @Override
    public List<PeriodiciteEtOptionsParticulie> getPeriodiciteEtOptionsParticulie() {
        return IterableUtils.toList(PeriodiciteEtOptionsParticulieRepository.findAll());
    }
}
