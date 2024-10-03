package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.VatCollectionType;
import com.jss.osiris.modules.osiris.miscellaneous.repository.VatCollectionTypeRepository;

@Service
public class VatCollectionTypeServiceImpl implements VatCollectionTypeService {

    @Autowired
    VatCollectionTypeRepository vatCollectionTypeRepository;

    @Override
    public List<VatCollectionType> getVatCollectionTypes() {
        return IterableUtils.toList(vatCollectionTypeRepository.findAll());
    }

    @Override
    public VatCollectionType getVatCollectionType(Integer id) {
        Optional<VatCollectionType> vatCollectionType = vatCollectionTypeRepository.findById(id);
        if (vatCollectionType.isPresent())
            return vatCollectionType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VatCollectionType addOrUpdateVatCollectionType(
            VatCollectionType vatCollectionType) {
        return vatCollectionTypeRepository.save(vatCollectionType);
    }
}
