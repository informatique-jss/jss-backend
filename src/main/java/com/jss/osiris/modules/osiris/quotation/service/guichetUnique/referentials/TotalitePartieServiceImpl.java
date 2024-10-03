package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TotalitePartie;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.TotalitePartieRepository;

@Service
public class TotalitePartieServiceImpl implements TotalitePartieService {

    @Autowired
    TotalitePartieRepository TotalitePartieRepository;

    @Override
    public List<TotalitePartie> getTotalitePartie() {
        return IterableUtils.toList(TotalitePartieRepository.findAll());
    }
}
