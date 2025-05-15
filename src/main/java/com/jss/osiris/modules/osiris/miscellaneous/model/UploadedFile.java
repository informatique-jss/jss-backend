package com.jss.osiris.modules.osiris.miscellaneous.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.jackson.JacksonViews;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

@Entity
public class UploadedFile implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "uploaded_file_sequence", sequenceName = "uploaded_file_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "uploaded_file_sequence")
	@JsonView(JacksonViews.OsirisListView.class)
	private Integer id;

	@JsonView(JacksonViews.OsirisListView.class)
	private String filename;

	@Column(columnDefinition = "TEXT")
	@JsonView(JacksonViews.OsirisListView.class)
	private String path;

	private String checksum;

	@JsonSerialize(using = JacksonLocalDateTimeSerializer.class)
	@JsonDeserialize(using = JacksonLocalDateTimeDeserializer.class)
	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.OsirisDetailedView.class,
			JacksonViews.OsirisListView.class })
	private LocalDateTime creationDate;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.OsirisDetailedView.class,
			JacksonViews.OsirisListView.class })
	private String createdBy;

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.OsirisDetailedView.class,
			JacksonViews.OsirisListView.class })
	private Long size;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

}
