package com.jss.osiris.modules.myjss.crm.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.myjss.crm.model.WebinarParticipant;
import com.jss.osiris.modules.myjss.crm.service.WebinarParticipantService;
import com.jss.osiris.modules.myjss.wordpress.model.Author;
import com.jss.osiris.modules.myjss.wordpress.model.JssCategory;
import com.jss.osiris.modules.myjss.wordpress.model.Tag;
import com.jss.osiris.modules.myjss.wordpress.service.AssoMailAuthorService;
import com.jss.osiris.modules.myjss.wordpress.service.AssoMailJssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.AssoMailTagService;
import com.jss.osiris.modules.myjss.wordpress.service.AuthorService;
import com.jss.osiris.modules.myjss.wordpress.service.JssCategoryService;
import com.jss.osiris.modules.myjss.wordpress.service.TagService;
import com.jss.osiris.modules.osiris.crm.model.Candidacy;
import com.jss.osiris.modules.osiris.crm.model.CommunicationPreference;
import com.jss.osiris.modules.osiris.crm.service.CandidacyService;
import com.jss.osiris.modules.osiris.crm.service.CommunicationPreferenceService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class MyJssCrmController {

    private static final String inputEntryPoint = "myjss/crm";

    @Autowired
    private CommunicationPreferenceService communicationPreferenceService;

    @Autowired
    private ValidationHelper validationHelper;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    WebinarParticipantService webinarParticipantService;

    @Autowired
    MailHelper mailHelper;

    @Autowired
    CandidacyService candidacyService;

    @Autowired
    AssoMailAuthorService assoMailAuthorService;

    @Autowired
    AuthorService authorService;

    @Autowired
    TagService tagService;

    @Autowired
    JssCategoryService jssCategoryService;

    @Autowired
    AssoMailTagService assoMailTagService;

    @Autowired
    AssoMailJssCategoryService assoMailJssCategoryService;

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

    @JsonView(JacksonViews.MyJssDetailedView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/communication-preference")
    public ResponseEntity<CommunicationPreference> getCommunicationPreferenceByMail(@RequestParam String userMail,
            @RequestParam String validationToken, HttpServletRequest request) throws OsirisValidationException {
        detectFlood(request);
        if (employeeService.getCurrentMyJssUser() == null && (validationToken == null || validationToken.equals("")))
            throw new OsirisValidationException("validationToken");

        if (validationToken.equals("null"))
            validationToken = null;

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
     * @throws OsirisValidationException
     */
    @JsonView(JacksonViews.MyJssDetailedView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/subscribe-to-newspaper-newsletter")
    public ResponseEntity<Boolean> subscribeToNewspaperNewsletter(@RequestParam String userMail,
            HttpServletRequest request) throws OsirisValidationException {
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
     * @throws OsirisValidationException
     */
    @JsonView(JacksonViews.MyJssDetailedView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/unsubscribe-to-newspaper-newsletter")
    public ResponseEntity<Boolean> unsubscribeToNewspaperNewsletter(@RequestParam String userMail,
            @RequestParam String validationToken, HttpServletRequest request) throws OsirisValidationException {
        detectFlood(request);

        if (validationToken.equals("null"))
            validationToken = null;

        Responsable responsable = employeeService.getCurrentMyJssUser();

        if (responsable == null && (validationToken == null || validationToken.equals("")))
            throw new OsirisValidationException("validationToken");

        if (responsable != null)
            userMail = responsable.getMail().getMail();

        if (validationHelper.validateMail(userMail)) {
            communicationPreferenceService.unsubscribeToNewspaperNewsletter(userMail, validationToken);
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
     * @throws OsirisValidationException
     */
    @JsonView(JacksonViews.MyJssDetailedView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/subscribe-to-corporate-newsletter")
    public ResponseEntity<Boolean> subscribeToCorporateNewsletter(@RequestParam String userMail,
            HttpServletRequest request) throws OsirisValidationException {
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
     * @throws OsirisValidationException
     */
    @JsonView(JacksonViews.MyJssDetailedView.class)
    @GetMapping(inputEntryPoint + "/communication-preferences/unsubscribe-to-corporate-newsletter")
    public ResponseEntity<Boolean> unsubscribeToCorporateNewsletter(@RequestParam String userMail,
            @RequestParam String validationToken, HttpServletRequest request) throws OsirisValidationException {
        detectFlood(request);

        if (validationToken.equals("null"))
            validationToken = null;

        Responsable responsable = employeeService.getCurrentMyJssUser();

        if (responsable == null && (validationToken == null || validationToken.equals("")))
            throw new OsirisValidationException("validationToken");

        if (responsable != null)
            userMail = responsable.getMail().getMail();

        if (!validationHelper.validateMail(userMail))
            throw new OsirisValidationException("mail");

        communicationPreferenceService.unsubscribeToCorporateNewsletter(userMail, validationToken);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @JsonView(JacksonViews.MyJssDetailedView.class)
    @PostMapping(inputEntryPoint + "/webinar/subscribe")
    public ResponseEntity<Boolean> subscribeToWebinar(@RequestBody WebinarParticipant webinarParticipant,
            HttpServletRequest request) throws OsirisException {
        detectFlood(request);
        if (!validationHelper.validateMail(webinarParticipant.getMail()))
            throw new OsirisValidationException("mail");

        if (webinarParticipant.getPhoneNumber() != null
                && !validationHelper.validateFrenchPhone(webinarParticipant.getPhoneNumber()))
            throw new OsirisValidationException("phone");

        validationHelper.validateString(webinarParticipant.getFirstname(), true, 50, "firstname");
        validationHelper.validateString(webinarParticipant.getLastname(), true, 50, "lastname");

        webinarParticipantService.subscribeToWebinar(webinarParticipant);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @JsonView(JacksonViews.MyJssDetailedView.class)
    @GetMapping(inputEntryPoint + "/webinar/subscribe/replay")
    public ResponseEntity<Boolean> subscribeWebinarReplay(@RequestParam String mail,
            HttpServletRequest request) throws OsirisException {
        detectFlood(request);
        if (!validationHelper.validateMail(mail))
            throw new OsirisValidationException("mail");

        webinarParticipantService.subscribeToWebinarReplay(mail);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/subscribe/demo")
    public ResponseEntity<Boolean> subscribeDemo(@RequestParam String mail, @RequestParam String firstName,
            @RequestParam String lastName, @RequestParam(required = false) String phoneNumber,
            HttpServletRequest request) throws OsirisException {
        detectFlood(request);
        if (!validationHelper.validateMail(mail))
            throw new OsirisValidationException("mail");

        if (phoneNumber != null
                && !validationHelper.validateFrenchPhone(phoneNumber))
            throw new OsirisValidationException("phone");

        validationHelper.validateString(firstName, true, 50, "firstname");
        validationHelper.validateString(lastName, true, 50, "lastname");

        mailHelper.sendConfirmationDemoMyJss(mail);
        mailHelper.sendCustomerDemoRequestToCommercial(mail, firstName, lastName, phoneNumber);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/subscribe/prices")
    public ResponseEntity<Boolean> subscribePrices(@RequestParam String mail, @RequestParam String firstName,
            @RequestParam String lastName, @RequestParam(required = false) String phoneNumber,
            HttpServletRequest request) throws OsirisException {
        detectFlood(request);
        if (!validationHelper.validateMail(mail))
            throw new OsirisValidationException("mail");

        if (phoneNumber != null
                && !validationHelper.validateFrenchPhone(phoneNumber))
            throw new OsirisValidationException("phone");

        validationHelper.validateString(firstName, true, 50, "firstname");
        validationHelper.validateString(lastName, true, 50, "lastname");

        mailHelper.sendConfirmationPricesMyJss(mail);
        mailHelper.sendCustomerPricesRequestToCommercial(mail, firstName, lastName, phoneNumber);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @GetMapping(inputEntryPoint + "/subscribe/contact")
    public ResponseEntity<Boolean> subscribeContactForm(@RequestParam String mail, @RequestParam String firstName,
            @RequestParam String lastName, @RequestParam(required = false) String phoneNumber,
            @RequestParam String message,
            HttpServletRequest request) throws OsirisException {
        detectFlood(request);
        if (!validationHelper.validateMail(mail))
            throw new OsirisValidationException("mail");

        validationHelper.validateString(firstName, true, 50, "firstname");
        validationHelper.validateString(lastName, true, 50, "lastname");
        validationHelper.validateString(message, true, 250, "message");

        if (phoneNumber != null
                && !validationHelper.validateFrenchPhone(phoneNumber))
            throw new OsirisValidationException("phone");

        mailHelper.sendConfirmationContactFormMyJss(mail);
        mailHelper.sendContactFormNotificationMail(mail, firstName, lastName, phoneNumber, message);
        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
    }

    @JsonView(JacksonViews.MyJssDetailedView.class)
    @PostMapping(inputEntryPoint + "/subscribe/candidacy")
    public ResponseEntity<Candidacy> subscribeCandidacy(@RequestBody Candidacy candidacy,
            HttpServletRequest request) throws OsirisException {
        detectFlood(request);
        if (!validationHelper.validateMail(candidacy.getMail()))
            throw new OsirisValidationException("mail");

        validationHelper.validateString(candidacy.getSearchedJob(), false, 50, "searchedJob");
        validationHelper.validateString(candidacy.getMessage(), true, "message");

        return new ResponseEntity<Candidacy>(candidacyService.declareNewCandidacy(candidacy), HttpStatus.OK);
    }

    @JsonView(JacksonViews.MyJssDetailedView.class)
    @GetMapping(inputEntryPoint + "/preferences/followed/author")
    public ResponseEntity<List<Author>> getFollowedAuthorsForCurrentUser(HttpServletRequest request)
            throws OsirisValidationException {
        detectFlood(request);

        return new ResponseEntity<List<Author>>(
                authorService.getFollowedAuthorsForCurrentUser(), HttpStatus.OK);
    }

    @JsonView(JacksonViews.MyJssDetailedView.class)
    @GetMapping(inputEntryPoint + "/preferences/followed/tag")
    public ResponseEntity<List<Tag>> getFollowedTagsForCurrentUser(HttpServletRequest request)
            throws OsirisValidationException {
        detectFlood(request);

        return new ResponseEntity<List<Tag>>(
                tagService.getFollowedTagsForCurrentUser(), HttpStatus.OK);
    }

    @JsonView(JacksonViews.MyJssDetailedView.class)
    @GetMapping(inputEntryPoint + "/preferences/followed/jss-category")
    public ResponseEntity<List<JssCategory>> getFollowedJssCategoriesForCurrentUser(HttpServletRequest request)
            throws OsirisValidationException {
        detectFlood(request);

        return new ResponseEntity<List<JssCategory>>(
                jssCategoryService.getFollowedJssCategoriesForCurrentUser(), HttpStatus.OK);
    }
}
