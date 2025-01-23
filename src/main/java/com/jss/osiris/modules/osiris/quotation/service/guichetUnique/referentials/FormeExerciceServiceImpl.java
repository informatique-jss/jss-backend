package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeExercice;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.FormeExerciceRepository;

@Service
public class FormeExerciceServiceImpl implements FormeExerciceService {

    @Autowired
    FormeExerciceRepository FormeExerciceRepository;

    @Override
    public List<FormeExercice> getFormeExercice() {
        return IterableUtils.toList(FormeExerciceRepository.findAll());
    }
}
