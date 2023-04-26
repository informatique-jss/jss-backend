package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutFormalite;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatutFormaliteRepository;

@Service
public class StatutFormaliteServiceImpl implements StatutFormaliteService {

    @Autowired
    StatutFormaliteRepository StatutFormaliteRepository;

    @Override
    public List<StatutFormalite> getStatutFormalite() {
        return IterableUtils.toList(StatutFormaliteRepository.findAll());
    }
}
