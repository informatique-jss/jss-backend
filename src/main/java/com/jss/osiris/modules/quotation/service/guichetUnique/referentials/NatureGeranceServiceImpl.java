package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureGerance;
import com.jss.osiris.modules.quotation.repository.guichetUnique.NatureGeranceRepository;

@Service
public class NatureGeranceServiceImpl implements NatureGeranceService {

    @Autowired
    NatureGeranceRepository NatureGeranceRepository;

    @Override
    public List<NatureGerance> getNatureGerance() {
        return IterableUtils.toList(NatureGeranceRepository.findAll());
    }
}
