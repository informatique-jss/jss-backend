package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.StatutExerciceActiviteSimultan;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.StatutExerciceActiviteSimultanRepository;

@Service
public class StatutExerciceActiviteSimultanServiceImpl implements StatutExerciceActiviteSimultanService {

    @Autowired
    StatutExerciceActiviteSimultanRepository StatutExerciceActiviteSimultanRepository;

    @Override
    public List<StatutExerciceActiviteSimultan> getStatutExerciceActiviteSimultan() {
        return IterableUtils.toList(StatutExerciceActiviteSimultanRepository.findAll());
    }
}
