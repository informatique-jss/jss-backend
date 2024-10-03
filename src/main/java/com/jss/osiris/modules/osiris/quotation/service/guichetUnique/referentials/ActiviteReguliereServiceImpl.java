package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.ActiviteReguliere;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.ActiviteReguliereRepository;

@Service
public class ActiviteReguliereServiceImpl implements ActiviteReguliereService {

    @Autowired
    ActiviteReguliereRepository ActiviteReguliereRepository;

    @Override
    public List<ActiviteReguliere> getActiviteReguliere() {
        return IterableUtils.toList(ActiviteReguliereRepository.findAll());
    }
}
