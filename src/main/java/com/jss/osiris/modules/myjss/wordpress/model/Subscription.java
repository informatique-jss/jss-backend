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
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(indexes = {
        @Index(name = "idx_subscription_subcription_mail", columnList = "id_subscription_mail"),
        @Index(name = "idx_subscription_subcription_offered_mail", columnList = "id_subscription_offered_mail"),
        @Index(name = "idx_subscription_validation_token", columnList = "validationToken", unique = true) })
public class Subscription implements IId, Serializable {

    @Id
    private Integer id;

    @JsonDeserialize(using = JacksonLocalDateDeserializer.class)
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private LocalDate startDate;

    @JsonDeserialize(using = JacksonLocalDateDeserializer.class)
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private LocalDate endDate;

    private SubscriptionTypeEnum subscriptionType;

    @ManyToOne
    @JoinColumn(name = "id_post", nullable = true)
    private Post post;

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

    public enum SubscriptionTypeEnum {
        ANNUAL_SUBSCRIPTION,
        ONE_POST_SUBSCRIPTION,
        SHARED_POST_SUBSCRIPTION;
    }

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

    public SubscriptionTypeEnum getSubscriptionType() {
        return subscriptionType;
    }

    public void setSubscriptionType(SubscriptionTypeEnum subscriptionType) {
        this.subscriptionType = subscriptionType;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
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
