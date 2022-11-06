package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPraticien;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatutPraticienRepository;

@Service
public class StatutPraticienServiceImpl implements StatutPraticienService {

    @Autowired
    StatutPraticienRepository StatutPraticienRepository;

    @Override
    @Cacheable(value = "statutPraticienList", key = "#root.methodName")
    public List<StatutPraticien> getStatutPraticien() {
        return IterableUtils.toList(StatutPraticienRepository.findAll());
    }
}
