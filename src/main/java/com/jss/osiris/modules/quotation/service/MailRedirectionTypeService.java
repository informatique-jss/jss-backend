package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.MailRedirectionType;

public interface MailRedirectionTypeService {
    public List<MailRedirectionType> getMailRedirectionTypes();

    public MailRedirectionType getMailRedirectionType(Integer id);
}
