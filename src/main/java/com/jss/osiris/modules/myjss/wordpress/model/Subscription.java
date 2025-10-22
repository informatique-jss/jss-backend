package com.jss.osiris.modules.myjss.wordpress.model;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateDeserializer;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "idx_subscription_subcription_mail", columnList = "id_subscription_mail"),
        @Index(name = "idx_subscription_subcription_offered_mail", columnList = "id_subscription_offered_mail"),
        @Index(name = "idx_subscription_validation_token", columnList = "validationToken", unique = true) })
public class Subscription implements IId, Serializable {

    public final static String ANNUAL_SUBSCRIPTION = "ANNUAL_SUBSCRIPTION";
    public final static String ENTERPRISE_ANNUAL_SUBSCRIPTION = "ENTERPRISE_ANNUAL_SUBSCRIPTION";
    public final static String MONTHLY_SUBSCRIPTION = "MONTHLY_SUBSCRIPTION";
    public final static String ONE_POST_SUBSCRIPTION = "ONE_POST_SUBSCRIPTION";
    public final static String SHARED_POST_SUBSCRIPTION = "SHARED_POST_SUBSCRIPTION";
    public final static String NEWSPAPER_KIOSK_BUY = "NEWSPAPER_KIOSK_BUY";

    @Id
    @SequenceGenerator(name = "subscription_sequence", sequenceName = "subscription_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subscription_sequence")
    private Integer id;

    @JsonDeserialize(using = JacksonLocalDateDeserializer.class)
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private LocalDate startDate;

    @JsonDeserialize(using = JacksonLocalDateDeserializer.class)
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private LocalDate endDate;

    private String subscriptionType;

    @ManyToOne
    @JoinColumn(name = "id_post", nullable = true)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "id_newspaper", nullable = true)
    private Newspaper newspaper;

    @ManyToOne
    @JoinColumn(name = "id_subscription_mail")
    private Mail subcriptionMail;

    @ManyToOne
    @JoinColumn(name = "id_subscription_offered_mail")
    private Mail subscriptionOfferedMail;

    @Column(length = 40)
    private String validationToken;

    private LocalDate sharedDate;

    private Integer viewsPerTokenNumber;

    @Override
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(String subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Newspaper getNewspaper() {
        return newspaper;
    }

    public void setNewspaper(Newspaper newspaper) {
        this.newspaper = newspaper;
    }

    public Mail getSubcriptionMail() {
        return subcriptionMail;
    }

    public void setSubcriptionMail(Mail subcriptionMail) {
        this.subcriptionMail = subcriptionMail;
    }

    public Mail getSubscriptionOfferedMail() {
        return subscriptionOfferedMail;
    }

    public void setSubscriptionOfferedMail(Mail subscriptionOfferedMail) {
        this.subscriptionOfferedMail = subscriptionOfferedMail;
    }

    public String getValidationToken() {
        return validationToken;
    }

    public void setValidationToken(String validationToken) {
        this.validationToken = validationToken;
    }

    public LocalDate getSharedDate() {
        return sharedDate;
    }

    public void setSharedDate(LocalDate sharedDate) {
        this.sharedDate = sharedDate;
    }

    public Integer getViewsPerTokenNumber() {
        return viewsPerTokenNumber;
    }

    public void setViewsPerTokenNumber(Integer viewsPerTokenNumber) {
        this.viewsPerTokenNumber = viewsPerTokenNumber;
    }

}
