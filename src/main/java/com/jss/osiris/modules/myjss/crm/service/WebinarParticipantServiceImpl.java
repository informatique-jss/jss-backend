package com.jss.osiris.modules.myjss.crm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.myjss.crm.model.WebinarParticipant;
import com.jss.osiris.modules.myjss.crm.repository.WebinarParticipantRepository;
import com.jss.osiris.modules.osiris.crm.model.Webinar;
import com.jss.osiris.modules.osiris.crm.service.WebinarService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;

@Service
public class WebinarParticipantServiceImpl implements WebinarParticipantService {

    @Autowired
    WebinarParticipantRepository webinarParticipantRepository;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    WebinarService webinarService;

    @Autowired
    MailService mailService;

    @Override
    public WebinarParticipant getWebinarParticipant(Integer id) {
        Optional<WebinarParticipant> webinarParticipant = webinarParticipantRepository.findById(id);
        if (webinarParticipant.isPresent())
            return webinarParticipant.get();
        return null;
    }

    @Override
    public WebinarParticipant addOrUpdateWebinarParticipant(WebinarParticipant webinarParticipant) {
        return webinarParticipantRepository.save(webinarParticipant);
    }

    @Override
    public WebinarParticipant subscribeToWebinar(WebinarParticipant webinarParticipant) throws OsirisException {
        if (webinarParticipant != null) {
            webinarParticipant.setIsParticipating(true);
            webinarParticipant.setWebinars(List.of(webinarService.getWebinars().get(0)));
            if (webinarParticipant.getMail() != null)
                mailService.populateMailId(webinarParticipant.getMail());
            this.addOrUpdateWebinarParticipant(webinarParticipant);
            mailHelper.sendConfirmationSubscriptionWebinarMyJss(webinarParticipant);
        }
        return null;
    }

    @Override
    public List<WebinarParticipant> getWebinarParticipants(Webinar webinar) {
        if (webinar != null)
            return webinarParticipantRepository.findByWebinarsAndIsParticipating(webinar, true);
        return null;
    }

    @Override
    public WebinarParticipant deleteWebinarParticipant(WebinarParticipant webinarParticipant) {
        if (webinarParticipant != null)
            webinarParticipant.setIsParticipating(false);
        return webinarParticipantRepository.save(webinarParticipant);
    }
}
