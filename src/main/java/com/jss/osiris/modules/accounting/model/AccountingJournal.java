package com.jss.osiris.modules.accounting.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class AccountingJournal implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "accounting_journal_sequence", sequenceName = "accounting_journal_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accounting_journal_sequence")
	private Integer id;

	@Column(nullable = false)
	private String label;

	private String code;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
