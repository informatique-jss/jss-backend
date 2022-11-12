package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SituationVisAVisMsa;
import com.jss.osiris.modules.quotation.repository.guichetUnique.SituationVisAVisMsaRepository;

@Service
public class SituationVisAVisMsaServiceImpl implements SituationVisAVisMsaService {

    @Autowired
    SituationVisAVisMsaRepository SituationVisAVisMsaRepository;

    @Override
    @Cacheable(value = "situationVisAVisMsaList", key = "#root.methodName")
    public List<SituationVisAVisMsa> getSituationVisAVisMsa() {
        return IterableUtils.toList(SituationVisAVisMsaRepository.findAll());
    }
}
