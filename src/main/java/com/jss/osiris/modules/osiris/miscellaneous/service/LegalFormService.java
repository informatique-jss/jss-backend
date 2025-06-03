package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.jss.osiris.modules.osiris.miscellaneous.model.LegalForm;

public interface LegalFormService {
    public List<LegalForm> getLegalForms();

    public LegalForm getLegalForm(Integer id);

    public LegalForm addOrUpdateLegalForm(LegalForm legalForm);

    public Page<LegalForm> getLegalFormsByName(String label, Pageable pageable);
}
