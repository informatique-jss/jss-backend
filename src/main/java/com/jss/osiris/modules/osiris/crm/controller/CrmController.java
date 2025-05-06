package com.jss.osiris.modules.osiris.crm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.jss.osiris.modules.myjss.crm.model.WebinarParticipant;
import com.jss.osiris.modules.myjss.crm.service.WebinarParticipantService;
import com.jss.osiris.modules.osiris.crm.model.Comment;
import com.jss.osiris.modules.osiris.crm.model.CommentSearch;
import com.jss.osiris.modules.osiris.crm.model.CommunicationPreference;
import com.jss.osiris.modules.osiris.crm.model.Webinar;
import com.jss.osiris.modules.osiris.crm.service.CommentService;
import com.jss.osiris.modules.osiris.crm.service.CommunicationPreferenceService;
import com.jss.osiris.modules.osiris.crm.service.WebinarService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

@RestController
public class CrmController {

        private static final String inputEntryPoint = "crm";

        @Autowired
        private CommunicationPreferenceService communicationPreferenceService;

        @Autowired
        private ValidationHelper validationHelper;

        @Autowired
        private CommentService commentService;

        @Autowired
        WebinarService webinarService;

        @Autowired
        WebinarParticipantService webinarParticipantService;

        @JsonView(JacksonViews.MyJssDetailedView.class)
        @GetMapping(inputEntryPoint + "/communication-preferences/communication-preference")
        public ResponseEntity<CommunicationPreference> getCommunicationPreferenceByMail(@RequestParam String userMail)
                        throws OsirisValidationException {

                if (validationHelper.validateMail(userMail)) {
                        CommunicationPreference communicationPreference = communicationPreferenceService
                                        .getCommunicationPreferenceByMail(userMail, null);

                        return new ResponseEntity<CommunicationPreference>(communicationPreference, HttpStatus.OK);

                } else {
                        return new ResponseEntity<CommunicationPreference>(null, new HttpHeaders(),
                                        HttpStatus.BAD_REQUEST);
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
        @JsonView(JacksonViews.MyJssDetailedView.class)
        @GetMapping(inputEntryPoint + "/communication-preferences/subscribe-to-newspaper-newsletter")
        public ResponseEntity<Boolean> subscribeToNewspaperNewsletter(@RequestParam String userMail)
                        throws OsirisException {

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
        @JsonView(JacksonViews.MyJssDetailedView.class)
        @GetMapping(inputEntryPoint + "/communication-preferences/unsubscribe-to-newspaper-newsletter")
        public ResponseEntity<Boolean> unsubscribeToNewspaperNewsletter(@RequestParam String userMail)
                        throws OsirisException {
                if (validationHelper.validateMail(userMail)) {
                        communicationPreferenceService.unsubscribeToNewspaperNewsletter(userMail, null);
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
        @JsonView(JacksonViews.MyJssDetailedView.class)
        @GetMapping(inputEntryPoint + "/communication-preferences/subscribe-to-corporate-newsletter")
        public ResponseEntity<Boolean> subscribeToCorporateNewsletter(@RequestParam String userMail)
                        throws OsirisException {
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
        @JsonView(JacksonViews.MyJssDetailedView.class)
        @GetMapping(inputEntryPoint + "/communication-preferences/unsubscribe-to-corporate-newsletter")
        public ResponseEntity<Boolean> unsubscribeToCorporateNewsletter(@RequestParam String userMail)
                        throws OsirisException {
                if (validationHelper.validateMail(userMail)) {
                        communicationPreferenceService.unsubscribeToCorporateNewsletter(userMail, null);
                        return new ResponseEntity<Boolean>(true, HttpStatus.OK);
                } else {
                        return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
                }
        }

        /**
         * Fetch all comments in DB filtered by parameters
         * 
         * @param request
         * @return
         */
        @PostMapping(inputEntryPoint + "/comments/search")
        @JsonView(JacksonViews.OsirisListView.class)
        public ResponseEntity<Page<Comment>> getComments(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestBody CommentSearch commentSearch)
                        throws OsirisException {

                Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "creationDate"));

                return ResponseEntity
                                .ok(commentService.getComments(commentSearch, pageable));
        }

        /**
         * Fetch all comments in DB filtered by parameters
         * 
         * @param request
         * @return
         */
        @GetMapping(inputEntryPoint + "/comment/get")
        @JsonView(JacksonViews.OsirisListView.class)
        public ResponseEntity<Comment> getComment(@RequestParam Integer commentId) {

                return ResponseEntity
                                .ok(commentService.getComment(commentId));
        }

        /**
         * Update isModerated in a comment
         * 
         * @param comment
         * @return
         * @throws OsirisException
         */
        @GetMapping(inputEntryPoint + "/post/comment/moderate")
        public ResponseEntity<Boolean> updateIsModerated(@RequestParam Boolean isModerated,
                        @RequestParam Integer commentId) {
                Comment comment = commentService.getComment(commentId);

                if (comment != null) {
                        return new ResponseEntity<Boolean>(commentService.updateIsModerated(isModerated, commentId),
                                        HttpStatus.OK);
                }

                return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        }

        /**
         * Updates the content of a comment
         * 
         * @param comment
         * @return
         * @throws OsirisException
         */
        @GetMapping(inputEntryPoint + "/post/comment/content")
        public ResponseEntity<Boolean> updateContent(@RequestParam String newContent,
                        @RequestParam Integer commentId) {
                Comment comment = commentService.getComment(commentId);

                if (comment != null) {
                        return new ResponseEntity<Boolean>(commentService.updateContent(newContent, commentId),
                                        HttpStatus.OK);
                }

                return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        }

        /**
         * Deletes a comment
         * 
         * @param comment
         * @return
         * @throws OsirisException
         */
        @GetMapping(inputEntryPoint + "/post/comment/delete")
        public ResponseEntity<Boolean> delete(@RequestParam Integer commentId) {

                Comment comment = commentService.getComment(commentId);

                if (comment != null) {
                        return new ResponseEntity<Boolean>(commentService.delete(commentId), HttpStatus.OK);
                }

                return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        }

        @GetMapping(inputEntryPoint + "/webinars")
        @JsonView(JacksonViews.OsirisListView.class)
        public ResponseEntity<List<Webinar>> getWebinars() {
                return new ResponseEntity<List<Webinar>>(webinarService.getWebinars(), HttpStatus.OK);
        }

        @PostMapping(inputEntryPoint + "/webinar")
        @JsonView(JacksonViews.OsirisDetailedView.class)
        public ResponseEntity<Webinar> addOrUpdateWebinar(
                        @RequestBody Webinar webinars) throws OsirisValidationException, OsirisException {
                if (webinars.getId() != null)
                        validationHelper.validateReferential(webinars, true, "webinars");
                validationHelper.validateString(webinars.getCode(), true, "code");
                validationHelper.validateString(webinars.getLabel(), true, "label");

                return new ResponseEntity<Webinar>(webinarService.addOrUpdateWebinar(webinars), HttpStatus.OK);
        }

        @GetMapping(inputEntryPoint + "/webinar-participants")
        @JsonView(JacksonViews.OsirisListView.class)
        public ResponseEntity<List<WebinarParticipant>> getWebinarParticipants(@RequestParam Integer webinarId) {
                Webinar webinar = webinarService.getWebinar(webinarId);
                if (webinar == null)
                        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
                return new ResponseEntity<List<WebinarParticipant>>(
                                webinarParticipantService.getWebinarParticipants(webinar),
                                HttpStatus.OK);
        }

        @GetMapping(inputEntryPoint + "/webinar-participant/delete")
        @JsonView(JacksonViews.OsirisListView.class)
        public ResponseEntity<WebinarParticipant> deleteWebinarParticipant(@RequestParam Integer webinarParticipantId)
                        throws OsirisValidationException {
                WebinarParticipant webinarParticipant = webinarParticipantService
                                .getWebinarParticipant(webinarParticipantId);
                if (webinarParticipant == null)
                        throw new OsirisValidationException("webinarParticipant");

                return new ResponseEntity<WebinarParticipant>(
                                webinarParticipantService.deleteWebinarParticipant(webinarParticipant), HttpStatus.OK);
        }
}
