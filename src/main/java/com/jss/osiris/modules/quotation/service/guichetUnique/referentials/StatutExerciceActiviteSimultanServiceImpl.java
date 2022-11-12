package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutExerciceActiviteSimultan;
import com.jss.osiris.modules.quotation.repository.guichetUnique.StatutExerciceActiviteSimultanRepository;

@Service
public class StatutExerciceActiviteSimultanServiceImpl implements StatutExerciceActiviteSimultanService {

    @Autowired
    StatutExerciceActiviteSimultanRepository StatutExerciceActiviteSimultanRepository;

    @Override
    @Cacheable(value = "statutExerciceActiviteSimultanList", key = "#root.methodName")
    public List<StatutExerciceActiviteSimultan> getStatutExerciceActiviteSimultan() {
        return IterableUtils.toList(StatutExerciceActiviteSimultanRepository.findAll());
    }
}
