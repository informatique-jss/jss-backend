package com.jss.jssbackend.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.quotation.model.Shal;
import com.jss.jssbackend.modules.quotation.repository.ShalRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShalServiceImpl implements ShalService {

    @Autowired
    ShalRepository shalRepository;

    @Override
    public List<Shal> getShals() {
        return IterableUtils.toList(shalRepository.findAll());
    }

    @Override
    public Shal getShal(Integer id) {
        Optional<Shal> shal = shalRepository.findById(id);
        if (!shal.isEmpty())
            return shal.get();
        return null;
    }
}
