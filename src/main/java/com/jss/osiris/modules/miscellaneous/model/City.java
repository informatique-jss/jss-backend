package com.jss.osiris.modules.miscellaneous.model;

import java.io.Serializable;

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

import com.jss.osiris.libs.search.model.IndexedField;

@Entity
@Table(indexes = { @Index(name = "idx_city_country", columnList = "id_country") })
public class City implements Serializable, IId {

	private static final long serialVersionUID = 8884467187549297L;

	@Id
	@SequenceGenerator(name = "city_sequence", sequenceName = "city_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city_sequence")
	private Integer id;

	@Column(nullable = false, length = 100)
	@IndexedField
	private String label;

	@Column(nullable = false, length = 20)
	private String code;

	@Column(length = 10)
	@IndexedField
	private String postalCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_department")
	private Department department;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_country")
	private Country country;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

}
