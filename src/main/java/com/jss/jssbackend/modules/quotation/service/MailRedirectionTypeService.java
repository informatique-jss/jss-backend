package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.MailRedirectionType;

public interface MailRedirectionTypeService {
    public List<MailRedirectionType> getMailRedirectionTypes();

    public MailRedirectionType getMailRedirectionType(Integer id);
}
