package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.MailRedirectionType;
import com.jss.osiris.modules.quotation.repository.MailRedirectionTypeRepository;

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
        if (mailRedirectionType.isPresent())
            return mailRedirectionType.get();
        return null;
    }

    @Override
    public MailRedirectionType addOrUpdateMailRedirectionType(MailRedirectionType mailRedirectionType) {
        return mailRedirectionTypeRepository.save(mailRedirectionType);
    }
}
