package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.miscellaneous.model.LegalForm;
import com.jss.jssbackend.modules.miscellaneous.repository.LegalFormRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (!legalForm.isEmpty())
            return legalForm.get();
        return null;
    }
}
