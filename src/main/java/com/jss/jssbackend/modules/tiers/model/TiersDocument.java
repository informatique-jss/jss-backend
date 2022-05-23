package com.jss.jssbackend.modules.tiers.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class TiersDocument implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_tiers")
	@JsonBackReference
	private Tiers tiers;

	@ManyToOne
	@JoinColumn(name = "id_tiers_document_type")
	private TiersDocumentType tiersDocumentType;

	private Boolean isRecipientClient;
	private Boolean isRecipientAffaire;
	private String affaireAddress;
	private String affaireRecipient;
	private String clientAddress;
	private String clientRecipient;
	private Boolean isMailingPaper;
	private Boolean isMailingPdf;
	private Integer numberMailingAffaire;
	private Integer numberMailingClient;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_tiers_document_mail_client", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mailsClient;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_tiers_document_mail_affaire", joinColumns = @JoinColumn(name = "id_tiers_document"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mailsAffaire;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Tiers getTiers() {
		return tiers;
	}

	public void setTiers(Tiers tiers) {
		this.tiers = tiers;
	}

	public TiersDocumentType getTiersDocumentType() {
		return tiersDocumentType;
	}

	public void setTiersDocumentType(TiersDocumentType tiersDocumentType) {
		this.tiersDocumentType = tiersDocumentType;
	}

	public Boolean getIsRecipientClient() {
		return isRecipientClient;
	}

	public void setIsRecipientClient(Boolean isRecipientClient) {
		this.isRecipientClient = isRecipientClient;
	}

	public Boolean getIsRecipientAffaire() {
		return isRecipientAffaire;
	}

	public void setIsRecipientAffaire(Boolean isRecipientAffaire) {
		this.isRecipientAffaire = isRecipientAffaire;
	}

	public String getAffaireAddress() {
		return affaireAddress;
	}

	public void setAffaireAddress(String affaireAddress) {
		this.affaireAddress = affaireAddress;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public String getAffaireRecipient() {
		return affaireRecipient;
	}

	public void setAffaireRecipient(String affaireRecipient) {
		this.affaireRecipient = affaireRecipient;
	}

	public String getClientRecipient() {
		return clientRecipient;
	}

	public void setClientRecipient(String clientRecipient) {
		this.clientRecipient = clientRecipient;
	}

	public Boolean getIsMailingPaper() {
		return isMailingPaper;
	}

	public void setIsMailingPaper(Boolean isMailingPaper) {
		this.isMailingPaper = isMailingPaper;
	}

	public Boolean getIsMailingPdf() {
		return isMailingPdf;
	}

	public void setIsMailingPdf(Boolean isMailingPdf) {
		this.isMailingPdf = isMailingPdf;
	}

	public Integer getNumberMailingAffaire() {
		return numberMailingAffaire;
	}

	public void setNumberMailingAffaire(Integer numberMailingAffaire) {
		this.numberMailingAffaire = numberMailingAffaire;
	}

	public Integer getNumberMailingClient() {
		return numberMailingClient;
	}

	public void setNumberMailingClient(Integer numberMailingClient) {
		this.numberMailingClient = numberMailingClient;
	}

	public List<Mail> getMailsClient() {
		return mailsClient;
	}

	public void setMailsClient(List<Mail> mailsClient) {
		this.mailsClient = mailsClient;
	}

	public List<Mail> getMailsAffaire() {
		return mailsAffaire;
	}

	public void setMailsAffaire(List<Mail> mailsAffaire) {
		this.mailsAffaire = mailsAffaire;
	}
}
