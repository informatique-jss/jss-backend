package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutContrat;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatutContratRepository;

@Service
public class StatutContratServiceImpl implements StatutContratService {

    @Autowired
    StatutContratRepository StatutContratRepository;

    @Override
    public List<StatutContrat> getStatutContrat() {
        return IterableUtils.toList(StatutContratRepository.findAll());
    }
}
