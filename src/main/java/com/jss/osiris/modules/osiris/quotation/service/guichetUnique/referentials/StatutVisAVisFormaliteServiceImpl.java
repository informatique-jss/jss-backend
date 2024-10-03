package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.StatutVisAVisFormalite;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.StatutVisAVisFormaliteRepository;

@Service
public class StatutVisAVisFormaliteServiceImpl implements StatutVisAVisFormaliteService {

    @Autowired
    StatutVisAVisFormaliteRepository StatutVisAVisFormaliteRepository;

    @Override
    public List<StatutVisAVisFormalite> getStatutVisAVisFormalite() {
        return IterableUtils.toList(StatutVisAVisFormaliteRepository.findAll());
    }
}
