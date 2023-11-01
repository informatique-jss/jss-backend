package com.jss.osiris.libs.audit.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;

@Entity
@Table(indexes = { @Index(name = "idx_audit_entity", columnList = "entity"),
		@Index(name = "idx_audit_entity", columnList = "entity, entityId") })
public class Audit implements Serializable {

	@Id
	@SequenceGenerator(name = "audit_sequence", sequenceName = "audit_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audit_sequence")
	private Long id;

	@Column(nullable = false)
	private String entity;

	@Column(nullable = false)
	private Integer entityId;

	@Column(nullable = false)
	private String fieldName;

	@Column(columnDefinition = "TEXT")
	private String oldValue;

	@Column(columnDefinition = "TEXT")
	private String newValue;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	private LocalDateTime datetime;

	private String username;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEntity() {
		return entity;
	}

	public void setEntity(String entity) {
		this.entity = entity;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public String getOldValue() {
		return oldValue;
	}

	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}

	public String getNewValue() {
		return newValue;
	}

	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}

	public LocalDateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
