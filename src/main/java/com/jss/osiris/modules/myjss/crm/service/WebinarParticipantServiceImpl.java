package com.jss.osiris.modules.myjss.crm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public WebinarParticipant subscribeToWebinar(WebinarParticipant webinarParticipant) throws OsirisException {
        WebinarParticipant existingWebinarParticipant = null;
        if (webinarParticipant != null) {
            webinarParticipant.setIsParticipating(true);
            webinarParticipant.setWebinar(webinarService.getNextWebinar());
            mailService.populateMailId(webinarParticipant.getMail());

            existingWebinarParticipant = webinarParticipantRepository
                    .findByMailAndWebinar(webinarParticipant.getMail(), webinarParticipant.getWebinar());

            if (existingWebinarParticipant == null) {
                addOrUpdateWebinarParticipant(webinarParticipant);
                mailHelper.sendConfirmationSubscriptionWebinarMyJss(webinarParticipant);
                if (webinarParticipant.getQuestion() != null && webinarParticipant.getMail().getMail() != null &&
                        webinarParticipant.getFirstname() != null &&
                        webinarParticipant.getLastname() != null && webinarParticipant.getPhoneNumber() != null
                        && !webinarParticipant.getQuestion().isEmpty()) {
                    mailHelper.sendContactFormNotificationMail(webinarParticipant.getMail().getMail(),
                            webinarParticipant.getFirstname(),
                            webinarParticipant.getLastname(), webinarParticipant.getPhoneNumber(),
                            webinarParticipant.getQuestion());
                }
                return webinarParticipant;
            } else {
                existingWebinarParticipant.setIsParticipating(true);
                addOrUpdateWebinarParticipant(existingWebinarParticipant);
                return existingWebinarParticipant;
            }
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void subscribeToWebinarReplay(String mail) throws OsirisException {
        mailHelper.sendConfirmationSubscriptionWebinarReplayMyJss(mail);
        mailHelper.sendCustomerWebinarRequestToWebinarManager(mail);
    }

    @Override
    public List<WebinarParticipant> getWebinarParticipants(Webinar webinar) {
        if (webinar != null)
            return webinarParticipantRepository.findByWebinarAndIsParticipating(webinar, true);
        return null;
    }

    @Override
    public WebinarParticipant deleteWebinarParticipant(WebinarParticipant webinarParticipant) {
        if (webinarParticipant != null)
            webinarParticipant.setIsParticipating(false);
        return addOrUpdateWebinarParticipant(webinarParticipant);
    }
}
