package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TotalitePartie;
import com.jss.osiris.modules.quotation.repository.guichetUnique.TotalitePartieRepository;

@Service
public class TotalitePartieServiceImpl implements TotalitePartieService {

    @Autowired
    TotalitePartieRepository TotalitePartieRepository;

    @Override
    @Cacheable(value = "totalitePartieList", key = "#root.methodName")
    public List<TotalitePartie> getTotalitePartie() {
        return IterableUtils.toList(TotalitePartieRepository.findAll());
    }
}
