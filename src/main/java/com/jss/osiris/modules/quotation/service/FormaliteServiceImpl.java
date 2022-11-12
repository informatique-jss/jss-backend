package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.Formalite;
import com.jss.osiris.modules.quotation.repository.FormaliteRepository;

@Service
public class FormaliteServiceImpl implements FormaliteService {

    @Autowired
    FormaliteRepository formaliteRepository;

    @Override
    public Formalite getFormalite(Integer id) {
        Optional<Formalite> formalite = formaliteRepository.findById(id);
        if (formalite.isPresent())
            return formalite.get();
        return null;
    }
}
