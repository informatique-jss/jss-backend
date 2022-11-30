package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.guichetUnique.Formalite;

public interface GuichetUniqueDelegateService {
    public List<Formalite> getFormalities(LocalDateTime createdAfter, LocalDateTime updatedAfter)
            throws OsirisException;
}
