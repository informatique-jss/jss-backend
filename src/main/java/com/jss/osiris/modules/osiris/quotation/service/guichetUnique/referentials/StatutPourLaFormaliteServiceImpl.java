package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.StatutPourLaFormalite;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.StatutPourLaFormaliteRepository;

@Service
public class StatutPourLaFormaliteServiceImpl implements StatutPourLaFormaliteService {

    @Autowired
    StatutPourLaFormaliteRepository StatutPourLaFormaliteRepository;

    @Override
    public List<StatutPourLaFormalite> getStatutPourLaFormalite() {
        return IterableUtils.toList(StatutPourLaFormaliteRepository.findAll());
    }
}
