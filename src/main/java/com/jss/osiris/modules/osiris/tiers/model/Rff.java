package com.jss.osiris.modules.osiris.tiers.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.jss.osiris.modules.osiris.invoicing.model.Invoice;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

@Entity
public class Rff implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "rff_sequence", sequenceName = "rff_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rff_sequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tiers")
	@JsonIgnore
	private Tiers tiers;

	@Transient
	private String tiersLabel;

	@Transient
	private Integer tiersId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_responsable")
	@JsonIgnore
	private Responsable responsable;

	@Transient
	private String responsableLabel;

	@Transient
	private Integer responsableId;

	private Float rffInsertion;
	private Float rffFormalite;
	private Float rffTotal;

	@Column(length = 100)
	private String rffMail;

	private LocalDate startDate;
	private LocalDate endDate;

	private Boolean isCancelled;
	private Boolean isSent;

	@OneToMany(mappedBy = "rff")
	@JsonIgnoreProperties(value = { "rff" }, allowSetters = true)
	private List<Invoice> invoices;

	@Column(length = 40)
	@JsonProperty("rffIban")
	private String rffIban;

	@Column(length = 40)
	private String rffBic;

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

	public Responsable getResponsable() {
		return responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

	public Float getRffInsertion() {
		return rffInsertion;
	}

	public void setRffInsertion(Float rffInsertion) {
		this.rffInsertion = rffInsertion;
	}

	public Float getRffFormalite() {
		return rffFormalite;
	}

	public void setRffFormalite(Float rffFormalite) {
		this.rffFormalite = rffFormalite;
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

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public Boolean getIsSent() {
		return isSent;
	}

	public void setIsSent(Boolean isSent) {
		this.isSent = isSent;
	}

	public List<Invoice> getInvoices() {
		return invoices;
	}

	public void setInvoices(List<Invoice> invoices) {
		this.invoices = invoices;
	}

	@JsonGetter(value = "tiersLabel")
	public String getTiersLabel() {
		return tiersLabel;
	}

	public void setTiersLabel(String tiersLabel) {
		this.tiersLabel = tiersLabel;
	}

	@JsonGetter(value = "responsableLabel")
	public String getResponsableLabel() {
		return responsableLabel;
	}

	public void setResponsableLabel(String responsableLabel) {
		this.responsableLabel = responsableLabel;
	}

	@JsonGetter(value = "tiersId")
	public Integer getTiersId() {
		return tiersId;
	}

	public void setTiersId(Integer tiersId) {
		this.tiersId = tiersId;
	}

	@JsonGetter(value = "responsableId")
	public Integer getResponsableId() {
		return responsableId;
	}

	public void setResponsableId(Integer responsableId) {
		this.responsableId = responsableId;
	}

	public String getRffIban() {
		return rffIban;
	}

	public void setRffIban(String rffIban) {
		this.rffIban = rffIban;
	}

	public String getRffBic() {
		return rffBic;
	}

	public void setRffBic(String rffBic) {
		this.rffBic = rffBic;
	}

	public Float getRffTotal() {
		return rffTotal;
	}

	public void setRffTotal(Float rffTotal) {
		this.rffTotal = rffTotal;
	}

	public String getRffMail() {
		return rffMail;
	}

	public void setRffMail(String rffMail) {
		this.rffMail = rffMail;
	}
}
