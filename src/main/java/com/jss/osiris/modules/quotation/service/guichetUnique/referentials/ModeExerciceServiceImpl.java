package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ModeExercice;
import com.jss.osiris.modules.quotation.repository.guichetUnique.ModeExerciceRepository;

@Service
public class ModeExerciceServiceImpl implements ModeExerciceService {

    @Autowired
    ModeExerciceRepository ModeExerciceRepository;

    @Override
    public List<ModeExercice> getModeExercice() {
        return IterableUtils.toList(ModeExerciceRepository.findAll());
    }
}
