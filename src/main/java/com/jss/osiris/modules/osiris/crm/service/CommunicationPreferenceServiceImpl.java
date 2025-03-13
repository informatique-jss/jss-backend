package com.jss.osiris.modules.osiris.crm.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.crm.model.CommunicationPreference;
import com.jss.osiris.modules.osiris.crm.repository.CommunicationPreferenceRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;

@Service
public class CommunicationPreferenceServiceImpl implements CommunicationPreferenceService {

    @Autowired
    private CommunicationPreferenceRepository communicationPreferenceRepository;

    @Autowired
    private MailService mailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunicationPreference getCommunicationPreferenceByMail(String mailString, String validationToken)
            throws OsirisValidationException {

        CommunicationPreference communicationPreference = null;
        if (validationToken != null) {
            communicationPreference = communicationPreferenceRepository.findByValidationToken(validationToken);
            if (communicationPreference == null || !communicationPreference.getMail().getMail().toLowerCase().trim()
                    .equals(mailString.trim().toLowerCase()))
                throw new OsirisValidationException("validationToken");
        } else
            communicationPreference = communicationPreferenceRepository.findByMail_Mail(mailString);

        if (communicationPreference == null) {
            Mail mail = new Mail();
            mail.setMail(mailString);
            Mail communicationPreferenceMail = mailService.populateMailId(mail);

            communicationPreference = new CommunicationPreference();
            communicationPreference.setMail(communicationPreferenceMail);
            communicationPreference.setIsSubscribedToCorporateNewsletter(false);
            communicationPreference.setIsSubscribedToNewspaperNewletter(false);
            communicationPreference.setValidationToken(UUID.randomUUID().toString());
            communicationPreferenceRepository.save(communicationPreference);
        }
        return communicationPreference;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunicationPreference subscribeToNewspaperNewsletter(String emailToSubscribe)
            throws OsirisValidationException {
        CommunicationPreference communicationPreference = getCommunicationPreferenceByMail(emailToSubscribe, null);
        communicationPreference.setIsSubscribedToNewspaperNewletter(true);
        return communicationPreferenceRepository.save(communicationPreference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunicationPreference unsubscribeToNewspaperNewsletter(String emailToSubscribe, String validationToken)
            throws OsirisValidationException {
        CommunicationPreference communicationPreference = getCommunicationPreferenceByMail(emailToSubscribe,
                validationToken);
        communicationPreference.setIsSubscribedToNewspaperNewletter(false);
        return communicationPreferenceRepository.save(communicationPreference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunicationPreference subscribeToCorporateNewsletter(String emailToSubscribe)
            throws OsirisValidationException {
        CommunicationPreference communicationPreference = getCommunicationPreferenceByMail(emailToSubscribe, null);
        communicationPreference.setIsSubscribedToCorporateNewsletter(true);
        return communicationPreferenceRepository.save(communicationPreference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunicationPreference unsubscribeToCorporateNewsletter(String emailToSubscribe, String validationToken)
            throws OsirisValidationException {
        CommunicationPreference communicationPreference = getCommunicationPreferenceByMail(emailToSubscribe,
                validationToken);
        communicationPreference.setIsSubscribedToCorporateNewsletter(false);
        return communicationPreferenceRepository.save(communicationPreference);
    }
}
