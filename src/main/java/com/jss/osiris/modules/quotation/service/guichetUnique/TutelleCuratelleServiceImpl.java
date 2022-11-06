package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TutelleCuratelle;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TutelleCuratelleRepository;

@Service
public class TutelleCuratelleServiceImpl implements TutelleCuratelleService {

    @Autowired
    TutelleCuratelleRepository TutelleCuratelleRepository;

    @Override
    @Cacheable(value = "tutelleCuratelleList", key = "#root.methodName")
    public List<TutelleCuratelle> getTutelleCuratelle() {
        return IterableUtils.toList(TutelleCuratelleRepository.findAll());
    }
}
