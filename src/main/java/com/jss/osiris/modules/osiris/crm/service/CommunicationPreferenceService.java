package com.jss.osiris.modules.osiris.crm.service;

import com.jss.osiris.modules.osiris.crm.model.CommunicationPreference;

public interface CommunicationPreferenceService {

    public CommunicationPreference populateCommunicationPreferenceByMail(String mail);

    public CommunicationPreference subscribeToNewspaperNewsletter(String emailToSubscribe);

    public CommunicationPreference unsubscribeToNewspaperNewsletter(String emailToSubscribe);

    public CommunicationPreference subscribeToCorporateNewsletter(String emailToSubscribe);

    public CommunicationPreference unsubscribeToCorporateNewsletter(String emailToSubscribe);
}
