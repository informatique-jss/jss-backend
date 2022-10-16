package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.LegalForm;
import com.jss.osiris.modules.miscellaneous.repository.LegalFormRepository;

@Service
public class LegalFormServiceImpl implements LegalFormService {

    @Autowired
    LegalFormRepository legalFormRepository;

    @Override
    public List<LegalForm> getLegalForms() {
        return IterableUtils.toList(legalFormRepository.findAll());
    }

    @Override
    public LegalForm getLegalForm(Integer id) {
        Optional<LegalForm> legalForm = legalFormRepository.findById(id);
        if (legalForm.isPresent())
            return legalForm.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LegalForm addOrUpdateLegalForm(
            LegalForm legalForm) {
        return legalFormRepository.save(legalForm);
    }
}
