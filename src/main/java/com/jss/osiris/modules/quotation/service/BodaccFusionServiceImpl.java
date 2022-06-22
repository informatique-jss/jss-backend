package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.BodaccFusion;
import com.jss.osiris.modules.quotation.repository.BodaccFusionRepository;

@Service
public class BodaccFusionServiceImpl implements BodaccFusionService {

    @Autowired
    BodaccFusionRepository bodaccFusionRepository;

    @Override
    public BodaccFusion getBodaccFusion(Integer id) {
        Optional<BodaccFusion> bodaccFusion = bodaccFusionRepository.findById(id);
        if (!bodaccFusion.isEmpty())
            return bodaccFusion.get();
        return null;
    }
}
