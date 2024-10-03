package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TaciteReconduction;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.TaciteReconductionRepository;

@Service
public class TaciteReconductionServiceImpl implements TaciteReconductionService {

    @Autowired
    TaciteReconductionRepository TaciteReconductionRepository;

    @Override
    public List<TaciteReconduction> getTaciteReconduction() {
        return IterableUtils.toList(TaciteReconductionRepository.findAll());
    }
}
