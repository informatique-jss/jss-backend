package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.Bodacc;
import com.jss.osiris.modules.quotation.repository.BodaccRepository;

@Service
public class BodaccServiceImpl implements BodaccService {

    @Autowired
    BodaccRepository bodaccRepository;

    @Override
    public Bodacc getBodacc(Integer id) {
        Optional<Bodacc> bodacc = bodaccRepository.findById(id);
        if (bodacc.isPresent())
            return bodacc.get();
        return null;
    }
}
