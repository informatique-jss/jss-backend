package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Phone implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "phone_sequence", sequenceName = "phone_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "phone_sequence")
	@JsonView(JacksonViews.MyJssListView.class)
	private Integer id;

	@Column(nullable = false)
	@JsonView(JacksonViews.MyJssListView.class)
	private String phoneNumber;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
