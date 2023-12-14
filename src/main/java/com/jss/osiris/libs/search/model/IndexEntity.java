package com.jss.osiris.libs.search.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.jss.osiris.modules.profile.model.Employee;

@Entity
@IdClass(CompositeIndexEntityKey.class)
@Table(indexes = { @Index(name = "pk_index", columnList = "entityType,entityId", unique = true) })
public class IndexEntity implements Serializable {
	@Id
	private String entityType;
	@Id
	private Integer entityId;

	@Column(columnDefinition = "TEXT")
	private String text;

	private LocalDateTime createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee_createdy_by")
	private Employee createdBy;

	private LocalDateTime udpatedDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_employee_updated_by")
	private Employee updatedBy;

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public Integer getEntityId() {
		return entityId;
	}

	public void setEntityId(Integer entityId) {
		this.entityId = entityId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public Employee getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Employee createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getUdpatedDate() {
		return udpatedDate;
	}

	public void setUdpatedDate(LocalDateTime udpatedDate) {
		this.udpatedDate = udpatedDate;
	}

	public Employee getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Employee updatedBy) {
		this.updatedBy = updatedBy;
	}

}
