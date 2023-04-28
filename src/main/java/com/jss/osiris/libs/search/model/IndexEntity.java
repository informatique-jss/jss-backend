package com.jss.osiris.libs.search.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import com.jss.osiris.modules.miscellaneous.model.IId;

@Entity
@IdClass(CompositeIndexEntityKey.class)
@Table(indexes = { @Index(name = "pk_index", columnList = "entityType,entityId", unique = true) })
public class IndexEntity implements Serializable, IId {
	@Id
	private String entityType;
	@Id
	private Integer entityId;

	@Column(columnDefinition = "TEXT")
	private String text;

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

	@Override
	public Integer getId() {
		return entityId;
	}

}
