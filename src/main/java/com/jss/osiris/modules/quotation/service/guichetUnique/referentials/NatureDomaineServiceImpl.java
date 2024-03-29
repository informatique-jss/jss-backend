package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureDomaine;
import com.jss.osiris.modules.quotation.repository.guichetUnique.NatureDomaineRepository;

@Service
public class NatureDomaineServiceImpl implements NatureDomaineService {

    @Autowired
    NatureDomaineRepository NatureDomaineRepository;

    @Override
    public List<NatureDomaine> getNatureDomaine() {
        return IterableUtils.toList(NatureDomaineRepository.findAll());
    }
}
