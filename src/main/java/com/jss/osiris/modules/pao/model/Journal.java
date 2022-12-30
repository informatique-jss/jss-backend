package com.jss.osiris.modules.pao.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class Journal implements IId, IAttachment {

	@Id
	@SequenceGenerator(name = "journal_sequence", sequenceName = "journal_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "journal_sequence")
	private Integer id;

	@Column(nullable = false)
	private String label;

	@Column(nullable = false)
	private LocalDate journalDate;

	@OneToMany(mappedBy = "journal")
	@JsonIgnoreProperties(value = { "journal" }, allowSetters = true)
	private List<Attachment> attachments;

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

	public LocalDate getJournalDate() {
		return journalDate;
	}

	public void setJournalDate(LocalDate journalDate) {
		this.journalDate = journalDate;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

}
