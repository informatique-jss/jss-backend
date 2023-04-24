package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.PeriodiciteVersement;
import com.jss.osiris.modules.quotation.repository.guichetUnique.PeriodiciteVersementRepository;

@Service
public class PeriodiciteVersementServiceImpl implements PeriodiciteVersementService {

    @Autowired
    PeriodiciteVersementRepository PeriodiciteVersementRepository;

    @Override
    public List<PeriodiciteVersement> getPeriodiciteVersement() {
        return IterableUtils.toList(PeriodiciteVersementRepository.findAll());
    }
}
