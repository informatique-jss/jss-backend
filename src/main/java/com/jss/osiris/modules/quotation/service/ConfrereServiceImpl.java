package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.repository.ConfrereRepository;

@Service
public class ConfrereServiceImpl implements ConfrereService {

    @Autowired
    ConfrereRepository confrereRepository;

    @Override
    public List<Confrere> getConfreres() {
        return IterableUtils.toList(confrereRepository.findAll());
    }

    @Override
    public Confrere getConfrere(Integer id) {
        Optional<Confrere> confrere = confrereRepository.findById(id);
        if (!confrere.isEmpty())
            return confrere.get();
        return null;
    }
}
