package com.jss.osiris.modules.osiris.quotation.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.osiris.miscellaneous.model.Department;

public class ServiceTypeChosen {
    @JsonView(JacksonViews.MyJssView.class)
    private ServiceType service;

    @JsonView(JacksonViews.MyJssView.class)
    private Affaire affaire;

    @JsonView(JacksonViews.MyJssView.class)
    private Integer temporaryId;

    @JsonView(JacksonViews.MyJssView.class)
    private BigDecimal preTaxPrice;

    @JsonView(JacksonViews.MyJssView.class)
    private BigDecimal discountedAmount;

    @JsonView(JacksonViews.MyJssView.class)
    private LocalDate announcementPublicationDate;

    @JsonView(JacksonViews.MyJssView.class)
    private Boolean announcementRedactedByJss;

    @JsonView(JacksonViews.MyJssView.class)
    private Boolean announcementProofReading;

    @JsonView(JacksonViews.MyJssView.class)
    private NoticeTypeFamily announcementNoticeFamily;

    @JsonView(JacksonViews.MyJssView.class)
    private NoticeType announcementNoticeType;

    @JsonView(JacksonViews.MyJssView.class)
    private Department announcementDepartment;

    @JsonView(JacksonViews.MyJssView.class)
    private String announcementNotice;

    public ServiceType getService() {
        return service;
    }

    public void setService(ServiceType service) {
        this.service = service;
    }

    public Affaire getAffaire() {
        return affaire;
    }

    public void setAffaire(Affaire affaire) {
        this.affaire = affaire;
    }

    public Integer getTemporaryId() {
        return temporaryId;
    }

    public void setTemporaryId(Integer temporaryId) {
        this.temporaryId = temporaryId;
    }

    public BigDecimal getPreTaxPrice() {
        return preTaxPrice;
    }

    public void setPreTaxPrice(BigDecimal preTaxPrice) {
        this.preTaxPrice = preTaxPrice;
    }

    public BigDecimal getDiscountedAmount() {
        return discountedAmount;
    }

    public void setDiscountedAmount(BigDecimal discountedAmount) {
        this.discountedAmount = discountedAmount;
    }

    public LocalDate getAnnouncementPublicationDate() {
        return announcementPublicationDate;
    }

    public void setAnnouncementPublicationDate(LocalDate announcementPublicationDate) {
        this.announcementPublicationDate = announcementPublicationDate;
    }

    public Boolean getAnnouncementRedactedByJss() {
        return announcementRedactedByJss;
    }

    public void setAnnouncementRedactedByJss(Boolean announcementRedactedByJss) {
        this.announcementRedactedByJss = announcementRedactedByJss;
    }

    public Boolean getAnnouncementProofReading() {
        return announcementProofReading;
    }

    public void setAnnouncementProofReading(Boolean announcementProofReading) {
        this.announcementProofReading = announcementProofReading;
    }

    public NoticeTypeFamily getAnnouncementNoticeFamily() {
        return announcementNoticeFamily;
    }

    public void setAnnouncementNoticeFamily(NoticeTypeFamily announcementNoticeFamily) {
        this.announcementNoticeFamily = announcementNoticeFamily;
    }

    public NoticeType getAnnouncementNoticeType() {
        return announcementNoticeType;
    }

    public void setAnnouncementNoticeType(NoticeType announcementNoticeType) {
        this.announcementNoticeType = announcementNoticeType;
    }

    public Department getAnnouncementDepartment() {
        return announcementDepartment;
    }

    public void setAnnouncementDepartment(Department announcementDepartment) {
        this.announcementDepartment = announcementDepartment;
    }

    public String getAnnouncementNotice() {
        return announcementNotice;
    }

    public void setAnnouncementNotice(String announcementNotice) {
        this.announcementNotice = announcementNotice;
    }

}