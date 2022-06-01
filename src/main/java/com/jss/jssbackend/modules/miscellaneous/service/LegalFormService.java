package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.LegalForm;

public interface LegalFormService {
    public List<LegalForm> getLegalForms();

    public LegalForm getLegalForm(Integer id);
}
