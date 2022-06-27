package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.BodaccPublicationType;
import com.jss.osiris.modules.quotation.repository.BodaccPublicationTypeRepository;

@Service
public class BodaccPublicationTypeServiceImpl implements BodaccPublicationTypeService {

    @Autowired
    BodaccPublicationTypeRepository bodaccPublicationTypeRepository;

    @Override
    public List<BodaccPublicationType> getBodaccPublicationTypes() {
        return IterableUtils.toList(bodaccPublicationTypeRepository.findAll());
    }

    @Override
    public BodaccPublicationType getBodaccPublicationType(Integer id) {
        Optional<BodaccPublicationType> bodaccPublicationType = bodaccPublicationTypeRepository.findById(id);
        if (!bodaccPublicationType.isEmpty())
            return bodaccPublicationType.get();
        return null;
    }

    @Override
    public BodaccPublicationType addOrUpdateBodaccPublicationType(BodaccPublicationType bodaccPublicationType) {
        return bodaccPublicationTypeRepository.save(bodaccPublicationType);
    }
}
