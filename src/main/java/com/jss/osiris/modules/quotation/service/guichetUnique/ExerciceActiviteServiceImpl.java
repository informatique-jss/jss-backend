package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ExerciceActivite;
import com.jss.osiris.modules.quotation.repository.guichetUnique.ExerciceActiviteRepository;

@Service
public class ExerciceActiviteServiceImpl implements ExerciceActiviteService {

    @Autowired
    ExerciceActiviteRepository ExerciceActiviteRepository;

    @Override
    @Cacheable(value = "exerciceActiviteList", key = "#root.methodName")
    public List<ExerciceActivite> getExerciceActivite() {
        return IterableUtils.toList(ExerciceActiviteRepository.findAll());
    }
}
