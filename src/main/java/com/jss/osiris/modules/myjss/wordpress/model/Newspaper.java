package com.jss.osiris.modules.myjss.wordpress.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.UploadedFile;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;

@Entity
public class Newspaper implements IId, Serializable {

    public static final String JOURNAL_SPECIAL_DES_SOCIETES = "JSS";
    public static final String ANNONCES_DE_LA_SEINE = "JDS";

    @Id
    @SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
    @IndexedField
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    @JsonView(JacksonViews.MyJssListView.class)
    private String titleText;

    @JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
    @IndexedField
    @JsonView(JacksonViews.MyJssListView.class)
    private LocalDateTime date;

    @JsonView(JacksonViews.MyJssListView.class)
    private Integer newspaperIssueNumber;

    // Constants : JOURNAL_SPECIAL_DES_SOCIETES or ANNONCES_DE_LA_SEINE
    @JsonView(JacksonViews.MyJssListView.class)
    private String newspaperCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uploaded_full_file")
    private UploadedFile uploadedFullFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uploaded_cut_file")
    private UploadedFile uploadedCutFile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_uploaded_file_image")
    private UploadedFile uploadedFileImage;

    @Transient
    @JsonView(JacksonViews.MyJssDetailedView.class)
    private byte[] newspaperImage;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Integer getNewspaperIssueNumber() {
        return newspaperIssueNumber;
    }

    public void setNewspaperIssueNumber(Integer newspaperIssueNumber) {
        this.newspaperIssueNumber = newspaperIssueNumber;
    }

    public String getNewspaperCategory() {
        return newspaperCategory;
    }

    public void setNewspaperCategory(String newspaperCategory) {
        this.newspaperCategory = newspaperCategory;
    }

    public UploadedFile getUploadedFullFile() {
        return uploadedFullFile;
    }

    public void setUploadedFullFile(UploadedFile uploadedCompleteFile) {
        this.uploadedFullFile = uploadedCompleteFile;
    }

    public UploadedFile getUploadedCutFile() {
        return uploadedCutFile;
    }

    public void setUploadedCutFile(UploadedFile uploadedCutFile) {
        this.uploadedCutFile = uploadedCutFile;
    }

    public UploadedFile getUploadedFileImage() {
        return uploadedFileImage;
    }

    public void setUploadedFileImage(UploadedFile uploadedFileImage) {
        this.uploadedFileImage = uploadedFileImage;
    }

    public byte[] getNewspaperImage() {
        return newspaperImage;
    }

    public void setNewspaperImage(byte[] newspaperImage) {
        this.newspaperImage = newspaperImage;
    }
}
