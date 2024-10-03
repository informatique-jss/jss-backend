package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.DestinationEtablissement;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.DestinationEtablissementRepository;

@Service
public class DestinationEtablissementServiceImpl implements DestinationEtablissementService {

    @Autowired
    DestinationEtablissementRepository DestinationEtablissementRepository;

    @Override
    public List<DestinationEtablissement> getDestinationEtablissement() {
        return IterableUtils.toList(DestinationEtablissementRepository.findAll());
    }
}
