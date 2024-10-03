package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.MailRedirectionType;

public interface MailRedirectionTypeService {
    public List<MailRedirectionType> getMailRedirectionTypes();

    public MailRedirectionType getMailRedirectionType(Integer id);

    public MailRedirectionType addOrUpdateMailRedirectionType(MailRedirectionType mailRedirectionType);
}
