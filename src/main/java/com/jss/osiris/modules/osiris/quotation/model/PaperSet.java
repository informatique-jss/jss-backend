package com.jss.osiris.modules.osiris.quotation.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.PaperSetType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class PaperSet implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "paper_set_sequence", sequenceName = "paper_set_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "paper_set_sequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_paper_set_type")
	private PaperSetType paperSetType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_customer_order")
	@JsonIgnoreProperties(value = { "paperSets" }, allowSetters = true)
	private CustomerOrder customerOrder;

	private Integer locationNumber;

	private Boolean isCancelled;

	private Boolean isValidated;

	@Column(columnDefinition = "TEXT")
	private String creationComment;

	@Column(columnDefinition = "TEXT")
	private String validationComment;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public PaperSetType getPaperSetType() {
		return paperSetType;
	}

	public void setPaperSetType(PaperSetType paperSetType) {
		this.paperSetType = paperSetType;
	}

	public CustomerOrder getCustomerOrder() {
		return customerOrder;
	}

	public void setCustomerOrder(CustomerOrder customerOrder) {
		this.customerOrder = customerOrder;
	}

	public Integer getLocationNumber() {
		return locationNumber;
	}

	public void setLocationNumber(Integer locationNumber) {
		this.locationNumber = locationNumber;
	}

	public Boolean getIsCancelled() {
		return isCancelled;
	}

	public void setIsCancelled(Boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	public Boolean getIsValidated() {
		return isValidated;
	}

	public void setIsValidated(Boolean isValidated) {
		this.isValidated = isValidated;
	}

	public String getCreationComment() {
		return creationComment;
	}

	public void setCreationComment(String creationComment) {
		this.creationComment = creationComment;
	}

	public String getValidationComment() {
		return validationComment;
	}

	public void setValidationComment(String validationComment) {
		this.validationComment = validationComment;
	}

}
