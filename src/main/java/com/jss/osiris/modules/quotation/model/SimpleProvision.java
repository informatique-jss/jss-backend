package com.jss.osiris.modules.quotation.model;

import jakarta.persistence.Column;
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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
@Table(indexes = { @Index(name = "idx_simple_provision_status", columnList = "id_simple_provision_status"),
})
public class SimpleProvision implements IId {

	@Id
	@SequenceGenerator(name = "simple_provision_sequence", sequenceName = "simple_provision_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "simple_provision_sequence")
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_simple_provision_status")
	@IndexedField
	private SimpleProvisionStatus simpleProvisionStatus;

	@Column(columnDefinition = "TEXT")
	private String observations;

	@ManyToOne(fetch = FetchType.LAZY)
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
