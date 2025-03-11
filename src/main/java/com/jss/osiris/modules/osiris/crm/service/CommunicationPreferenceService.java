package com.jss.osiris.modules.osiris.crm.service;

import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.osiris.crm.model.CommunicationPreference;

public interface CommunicationPreferenceService {

    public CommunicationPreference getCommunicationPreferenceByMail(String mail, String validationToken)
            throws OsirisValidationException;

    public CommunicationPreference subscribeToNewspaperNewsletter(String emailToSubscribe);

    public CommunicationPreference unsubscribeToNewspaperNewsletter(String emailToSubscribe);

    public CommunicationPreference subscribeToCorporateNewsletter(String emailToSubscribe);

    public CommunicationPreference unsubscribeToCorporateNewsletter(String emailToSubscribe);
}
