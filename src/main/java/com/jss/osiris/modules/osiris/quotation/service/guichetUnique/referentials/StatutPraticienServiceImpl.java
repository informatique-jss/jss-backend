package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.StatutPraticien;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.StatutPraticienRepository;

@Service
public class StatutPraticienServiceImpl implements StatutPraticienService {

    @Autowired
    StatutPraticienRepository StatutPraticienRepository;

    @Override
    public List<StatutPraticien> getStatutPraticien() {
        return IterableUtils.toList(StatutPraticienRepository.findAll());
    }
}
