package com.jss.jssbackend.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.jssbackend.modules.quotation.model.Bodacc;
import com.jss.jssbackend.modules.quotation.repository.BodaccRepository;

@Service
public class BodaccServiceImpl implements BodaccService {

    @Autowired
    BodaccRepository bodaccRepository;

    @Override
    public Bodacc getBodacc(Integer id) {
        Optional<Bodacc> bodacc = bodaccRepository.findById(id);
        if (!bodacc.isEmpty())
            return bodacc.get();
        return null;
    }
}
