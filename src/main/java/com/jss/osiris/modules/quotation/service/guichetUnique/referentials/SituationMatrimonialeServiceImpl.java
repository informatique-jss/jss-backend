package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SituationMatrimoniale;
import com.jss.osiris.modules.quotation.repository.guichetUnique.SituationMatrimonialeRepository;

@Service
public class SituationMatrimonialeServiceImpl implements SituationMatrimonialeService {

    @Autowired
    SituationMatrimonialeRepository SituationMatrimonialeRepository;

    @Override
    public List<SituationMatrimoniale> getSituationMatrimoniale() {
        return IterableUtils.toList(SituationMatrimonialeRepository.findAll());
    }
}
