package com.jss.osiris.modules.myjss.crm.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.crm.model.WebinarParticipant;
import com.jss.osiris.modules.osiris.crm.model.Webinar;

public interface WebinarParticipantService {
    public WebinarParticipant getWebinarParticipant(Integer id);

    public WebinarParticipant addOrUpdateWebinarParticipant(WebinarParticipant webinarParticipant);

    public WebinarParticipant subscribeToWebinar(WebinarParticipant webinarParticipant) throws OsirisException;

    public List<WebinarParticipant> getWebinarParticipants(Webinar webinar);

    public WebinarParticipant deleteWebinarParticipant(WebinarParticipant webinarParticipant);
}
