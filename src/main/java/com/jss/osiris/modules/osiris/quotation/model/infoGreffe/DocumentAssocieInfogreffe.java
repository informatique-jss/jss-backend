package com.jss.osiris.modules.osiris.quotation.model.infoGreffe;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.IAttachment;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class DocumentAssocieInfogreffe implements IAttachment {
    public static String INFOGREFFE_ATTACHMENT_FACTURE = "GRF-FACTURE";
    public static String INFOGREFFE_ATTACHMENT_AUTRE = "GRF-AUTRE";
    public static String INFOGREFFE_ATTACHMENT_INVALIDATION = "GRF-INVALIDATION";
    public static String INFOGREFFE_ATTACHMENT_PIECE_MANQUANTE = "GRF-PIECE_MANQUANTE";
    public static String INFOGREFFE_ATTACHMENT_RBE_COPIE = "GRF-RBE_COPIE";
    public static String INFOGREFFE_ATTACHMENT_KBIS = "GRF-KBIS";
    public static String INFOGREFFE_ATTACHMENT_CERT_DEPOT = "GRF-CERT_DEPOT";
    public static String INFOGREFFE_ATTACHMENT_RELANCE = "GRF-RELANCE";
    public static String INFOGREFFE_ATTACHMENT_REJET = "GRF-REJET";

    @Id
    private String urlTelechargement;
    private String typeDocument;

    @OneToMany(mappedBy = "documentAssocieInfogreffe", fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "documentAssocieInfogreffe" }, allowSetters = true)
    private List<Attachment> attachments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "id_evenement_infogreffe_created_date", referencedColumnName = "createdDate"),
            @JoinColumn(name = "id_evenement_infogreffe_code_etat", referencedColumnName = "codeEtat")
    })
    @JsonIgnoreProperties(value = { "documentAssocies" }, allowSetters = true)
    private EvenementInfogreffe evenementInfogreffe;

    public String getUrlTelechargement() {
        return urlTelechargement;
    }

    public void setUrlTelechargement(String urlTelechargement) {
        this.urlTelechargement = urlTelechargement;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public EvenementInfogreffe getEvenementInfogreffe() {
        return evenementInfogreffe;
    }

    public void setEvenementInfogreffe(EvenementInfogreffe evenementInfogreffe) {
        this.evenementInfogreffe = evenementInfogreffe;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

}
