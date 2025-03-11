package com.jss.osiris.modules.osiris.crm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CommunicationPreference populateCommunicationPreferenceByMail(String mailString) {

        CommunicationPreference communicationPreference = communicationPreferenceRepository
                .findByMail_Mail(mailString);

        if (communicationPreference == null) {
            Mail mail = new Mail();
            mail.setMail(mailString);
            Mail communicationPreferenceMail = mailService.populateMailId(mail);

            communicationPreference = new CommunicationPreference();
            communicationPreference.setMail(communicationPreferenceMail);
            communicationPreference.setIsSubscribedToCorporateNewsletter(false);
            communicationPreference.setIsSubscribedToNewspaperNewletter(false);
            communicationPreferenceRepository.save(communicationPreference);
        }
        return communicationPreference;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunicationPreference subscribeToNewspaperNewsletter(String emailToSubscribe) {
        CommunicationPreference communicationPreference = populateCommunicationPreferenceByMail(emailToSubscribe);
        communicationPreference.setIsSubscribedToNewspaperNewletter(true);
        return communicationPreferenceRepository.save(communicationPreference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunicationPreference unsubscribeToNewspaperNewsletter(String emailToSubscribe) {
        CommunicationPreference communicationPreference = populateCommunicationPreferenceByMail(emailToSubscribe);
        communicationPreference.setIsSubscribedToNewspaperNewletter(false);
        return communicationPreferenceRepository.save(communicationPreference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunicationPreference subscribeToCorporateNewsletter(String emailToSubscribe) {
        CommunicationPreference communicationPreference = populateCommunicationPreferenceByMail(emailToSubscribe);
        communicationPreference.setIsSubscribedToCorporateNewsletter(true);
        return communicationPreferenceRepository.save(communicationPreference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommunicationPreference unsubscribeToCorporateNewsletter(String emailToSubscribe) {
        CommunicationPreference communicationPreference = populateCommunicationPreferenceByMail(emailToSubscribe);
        communicationPreference.setIsSubscribedToCorporateNewsletter(false);
        return communicationPreferenceRepository.save(communicationPreference);
    }
}
