package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
        @Index(name = "idx_announcement_notice_template", columnList = "id_announcement_notice_template"),
        @Index(name = "idx_announcement_notice_template_fragment", columnList = "id_announcement_notice_template_fragment") })
public class AssoAnnouncementNoticeTemplateAnnouncementFragment implements Serializable, IId {

    @Id
    @SequenceGenerator(name = "asso_announcement_template_fragment_sequence", sequenceName = "asso_announcement_template_fragment_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "asso_announcement_template_fragment_sequence")
    @JsonView({ JacksonViews.MyJssDetailedView.class })
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @IndexedField
    @JoinColumn(name = "id_announcement_notice_template")
    private AnnouncementNoticeTemplate announcementNoticeTemplate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_announcement_notice_template_fragment")
    @IndexedField
    @JsonView({ JacksonViews.MyJssDetailedView.class })
    private AnnouncementNoticeTemplateFragment announcementNoticeTemplateFragment;

    @JsonView({ JacksonViews.MyJssDetailedView.class })
    private Boolean isMandatory;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AnnouncementNoticeTemplate getAnnouncementNoticeTemplate() {
        return announcementNoticeTemplate;
    }

    public void setAnnouncementNoticeTemplate(AnnouncementNoticeTemplate announcementNoticeTemplate) {
        this.announcementNoticeTemplate = announcementNoticeTemplate;
    }

    public AnnouncementNoticeTemplateFragment getAnnouncementNoticeTemplateFragment() {
        return announcementNoticeTemplateFragment;
    }

    public void setAnnouncementNoticeTemplateFragment(
            AnnouncementNoticeTemplateFragment announcementNoticeTemplateFragment) {
        this.announcementNoticeTemplateFragment = announcementNoticeTemplateFragment;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean isMandatory) {
        this.isMandatory = isMandatory;
    }
}
