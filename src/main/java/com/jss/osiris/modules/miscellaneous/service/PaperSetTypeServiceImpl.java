package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.miscellaneous.model.PaperSetType;
import com.jss.osiris.modules.miscellaneous.repository.PaperSetTypeRepository; 
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaperSetTypeServiceImpl implements PaperSetTypeService {

    @Autowired
    PaperSetTypeRepository paperSetTypeRepository;

    @Override
    public List<PaperSetType> getPaperSetTypes() {
        return IterableUtils.toList(paperSetTypeRepository.findAll());
    }

    @Override
    public PaperSetType getPaperSetType(Integer id) {
        Optional<PaperSetType> paperSetType = paperSetTypeRepository.findById(id);
        if (paperSetType.isPresent())
            return paperSetType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaperSetType addOrUpdatePaperSetType(
            PaperSetType paperSetType) {
        return paperSetTypeRepository.save(paperSetType);
    }
}
