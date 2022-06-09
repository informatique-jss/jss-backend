package com.jss.jssbackend.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.quotation.model.MailRedirectionType;
import com.jss.jssbackend.modules.quotation.repository.MailRedirectionTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MailRedirectionTypeServiceImpl implements MailRedirectionTypeService {

    @Autowired
    MailRedirectionTypeRepository mailRedirectionTypeRepository;

    @Override
    public List<MailRedirectionType> getMailRedirectionTypes() {
        return IterableUtils.toList(mailRedirectionTypeRepository.findAll());
    }

    @Override
    public MailRedirectionType getMailRedirectionType(Integer id) {
        Optional<MailRedirectionType> mailRedirectionType = mailRedirectionTypeRepository.findById(id);
        if (!mailRedirectionType.isEmpty())
            return mailRedirectionType.get();
        return null;
    }
}
