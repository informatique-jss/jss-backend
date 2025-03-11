package com.jss.osiris.modules.myjss.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.crm.model.CommunicationPreference;
import com.jss.osiris.modules.osiris.crm.service.CommunicationPreferenceService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

@RestController
public class MyJssCrmController {

    private static final String inputEntryPoint = "myjss/crm";

    @Autowired
    private CommunicationPreferenceService communicationPreferenceService;

    @Autowired
    private ValidationHelper validationHelper;

    @JsonView(JacksonViews.MyJssView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/communication-preference")
    public ResponseEntity<CommunicationPreference> getCommunicationPreferenceByMail(@RequestParam String userMail,
            @RequestParam String validationToken) throws OsirisValidationException {

        if (validationHelper.validateMail(userMail)) {

            CommunicationPreference communicationPreference = communicationPreferenceService
                    .getCommunicationPreferenceByMail(userMail, validationToken);

            return new ResponseEntity<CommunicationPreference>(communicationPreference, HttpStatus.OK);

        } else {
            return new ResponseEntity<CommunicationPreference>(null, new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Subscribe the email to newspaper newletter. If the email does not exist, it
     * is created in the {@link Mail} DB as id for {@link CommunicationPreference}
     * 
     * @param email
     * @return
     * @throws OsirisException
     */
    @JsonView(JacksonViews.MyJssView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/subscribe-to-newspaper-newsletter")
    public ResponseEntity<Boolean> subscribeToNewspaperNewsletter(@RequestParam String userMail,
            @RequestParam String validationToken) throws OsirisException {

        if (validationHelper.validateMail(userMail)) {
            communicationPreferenceService.subscribeToNewspaperNewsletter(userMail);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Unsubscribe the email to newspaper newletter. If the email does not exist, it
     * is created in the {@link Mail} DB as id for {@link CommunicationPreference}
     * 
     * @param email
     * @return
     * @throws OsirisException
     */
    @JsonView(JacksonViews.MyJssView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/unsubscribe-to-newspaper-newsletter")
    public ResponseEntity<Boolean> unsubscribeToNewspaperNewsletter(@RequestParam String userMail,
            @RequestParam String validationToken) throws OsirisException {
        if (validationHelper.validateMail(userMail)) {
            communicationPreferenceService.unsubscribeToNewspaperNewsletter(userMail);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Subscribe the email to corporate newletter. If the email does not exist, it
     * is created in the {@link Mail} DB as id for {@link CommunicationPreference}
     * 
     * @param email
     * @return
     * @throws OsirisException
     */
    @JsonView(JacksonViews.MyJssView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/subscribe-to-corporate-newsletter")
    public ResponseEntity<Boolean> subscribeToCorporateNewsletter(@RequestParam String userMail,
            @RequestParam String validationToken) throws OsirisException {
        if (validationHelper.validateMail(userMail)) {
            communicationPreferenceService.subscribeToCorporateNewsletter(userMail);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Unsubscribe the email to corporate newletter. If the email does not exist, it
     * is created in the {@link Mail} DB as id for {@link CommunicationPreference}
     * 
     * @param email
     * @return
     * @throws OsirisException
     */
    @JsonView(JacksonViews.MyJssView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/unsubscribe-to-corporate-newsletter")
    public ResponseEntity<Boolean> unsubscribeToCorporateNewsletter(@RequestParam String userMail,
            @RequestParam String validationToken) throws OsirisException {
        if (validationHelper.validateMail(userMail)) {
            communicationPreferenceService.unsubscribeToCorporateNewsletter(userMail);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
    }
}
