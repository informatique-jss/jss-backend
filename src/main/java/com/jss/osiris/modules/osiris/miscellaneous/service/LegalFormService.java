package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.LegalForm;

public interface LegalFormService {
    public List<LegalForm> getLegalForms();

    public LegalForm getLegalForm(Integer id);

    public LegalForm addOrUpdateLegalForm(LegalForm legalForm);
}
