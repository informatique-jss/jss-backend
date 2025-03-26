package com.jss.osiris.modules.osiris.crm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.myjss.wordpress.service.PostService;
import com.jss.osiris.modules.osiris.crm.model.Comment;
import com.jss.osiris.modules.osiris.crm.model.CommunicationPreference;
import com.jss.osiris.modules.osiris.crm.service.CommentService;
import com.jss.osiris.modules.osiris.crm.service.CommunicationPreferenceService;
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
        private PostService postService;

        @JsonView(JacksonViews.MyJssView.class)
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
        @JsonView(JacksonViews.MyJssView.class)
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
        @JsonView(JacksonViews.MyJssView.class)
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
        @JsonView(JacksonViews.MyJssView.class)
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
        @JsonView(JacksonViews.MyJssView.class)
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
         * Fetch all comments in DB
         * 
         * @param request
         * @return
         */
        @GetMapping(inputEntryPoint + "/comments")
        @JsonView(JacksonViews.MyJssView.class)
        public ResponseEntity<Page<Comment>> getComments(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "creationDate") String sortBy,
                        @RequestParam(defaultValue = "desc") String sortDir) throws OsirisException {

                Pageable pageable = PageRequest.of(page, size,
                                Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                                                sortBy));

                return ResponseEntity.ok(commentService.getComments(pageable));
        }

        /**
         * Get comment by id
         * 
         * @param commentId
         * @return
         */
        @GetMapping(inputEntryPoint + "/comment")
        @JsonView(JacksonViews.MyJssView.class)
        public ResponseEntity<Comment> getComment(Integer commentId) {

                return new ResponseEntity<Comment>(commentService.getComment(commentId), HttpStatus.OK);
        }

        /**
         * Save or update comment
         * 
         * @param comment
         * @return
         * @throws OsirisException
         */
        @GetMapping(inputEntryPoint + "/comment/add")
        @JsonView(JacksonViews.MyJssView.class)
        public ResponseEntity<Comment> saveOrUpdate(Comment comment) throws OsirisException {

                if (comment != null && comment.getPost() != null) {
                        Post post = postService.getPost(comment.getPost().getId());

                        if (post != null) {
                                return new ResponseEntity<Comment>(commentService.addOrUpdateComment(comment),
                                                HttpStatus.OK);
                        }
                }
                return new ResponseEntity<Comment>(new Comment(), HttpStatus.OK);
        }

        /**
         * Deletes a comment
         * 
         * @param comment
         * @return
         * @throws OsirisException
         */
        @GetMapping(inputEntryPoint + "/comment/delete")
        @JsonView(JacksonViews.MyJssView.class)
        public ResponseEntity<Boolean> delete(Integer commentId) {

                Comment comment = commentService.getComment(commentId);

                if (comment != null) {
                        return new ResponseEntity<Boolean>(commentService.delete(commentId), HttpStatus.OK);
                }

                return new ResponseEntity<Boolean>(false, HttpStatus.OK);
        }

}
