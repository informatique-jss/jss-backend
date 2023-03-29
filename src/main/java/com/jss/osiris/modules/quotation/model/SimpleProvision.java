package com.jss.osiris.modules.quotation.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
public class SimpleProvision implements IId {

	@Id
	@SequenceGenerator(name = "simple_provision_sequence", sequenceName = "simple_provision_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "simple_provision_sequence")
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "id_simple_provision_status")
	@IndexedField
	private SimpleProvisionStatus simpleProvisionStatus;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@ManyToOne
	@JoinColumn(name = "id_waited_competent_authority")
	@JsonIgnoreProperties(value = { "departments", "cities", "regions" }, allowSetters = true)
	private CompetentAuthority waitedCompetentAuthority;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SimpleProvisionStatus getSimpleProvisionStatus() {
		return simpleProvisionStatus;
	}

	public void setSimpleProvisionStatus(SimpleProvisionStatus simpleProvisionStatus) {
		this.simpleProvisionStatus = simpleProvisionStatus;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public CompetentAuthority getWaitedCompetentAuthority() {
		return waitedCompetentAuthority;
	}

	public void setWaitedCompetentAuthority(CompetentAuthority waitedCompetentAuthority) {
		this.waitedCompetentAuthority = waitedCompetentAuthority;
	}

}
