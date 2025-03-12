package com.jss.osiris.modules.myjss.crm.controller;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MyJssCrmController {

    private static final String inputEntryPoint = "myjss/crm";

    @Autowired
    private CommunicationPreferenceService communicationPreferenceService;

    @Autowired
    private ValidationHelper validationHelper;

    private final ConcurrentHashMap<String, AtomicLong> requestCount = new ConcurrentHashMap<>();
    private final long rateLimit = 1000;
    private LocalDateTime lastFloodFlush = LocalDateTime.now();
    private int floodFlushDelayMinute = 1;

    private ResponseEntity<String> detectFlood(HttpServletRequest request) {
        if (lastFloodFlush.isBefore(LocalDateTime.now().minusMinutes(floodFlushDelayMinute)))
            requestCount.clear();

        String ipAddress = request.getRemoteAddr();
        AtomicLong count = requestCount.computeIfAbsent(ipAddress, k -> new AtomicLong());

        if (count.incrementAndGet() > rateLimit) {
            return new ResponseEntity<String>(new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS);
        }
        return null;
    }

    @JsonView(JacksonViews.MyJssView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/communication-preference")
    public ResponseEntity<CommunicationPreference> getCommunicationPreferenceByMail(@RequestParam String userMail,
            @RequestParam String validationToken, HttpServletRequest request) throws OsirisValidationException {
        detectFlood(request);
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
            @RequestParam String validationToken, HttpServletRequest request) throws OsirisException {
        detectFlood(request);
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
            @RequestParam String validationToken, HttpServletRequest request) throws OsirisException {
        detectFlood(request);
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
            @RequestParam String validationToken, HttpServletRequest request) throws OsirisException {
        detectFlood(request);
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
            @RequestParam String validationToken, HttpServletRequest request) throws OsirisException {
        detectFlood(request);
        if (validationHelper.validateMail(userMail)) {
            communicationPreferenceService.unsubscribeToCorporateNewsletter(userMail);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } else {
            return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
        }
    }
}
