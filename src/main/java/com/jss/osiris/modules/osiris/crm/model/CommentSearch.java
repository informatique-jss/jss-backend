package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.jss.osiris.libs.search.model.IndexEntity;

public class CommentSearch implements Serializable {

	private IndexEntity post;

	private String content;

	private String authorFirstLastName;

	private LocalDateTime creationDate;

	private Boolean isModerated = false;

	private Boolean isDeleted = false;

	public IndexEntity getPost() {
		return post;
	}

	public void setPost(IndexEntity post) {
		this.post = post;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthorFirstLastName() {
		return authorFirstLastName;
	}

	public void setAuthorFirstLastName(String authorFirstLastName) {
		this.authorFirstLastName = authorFirstLastName;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public Boolean getIsModerated() {
		return isModerated;
	}

	public void setIsModerated(Boolean isModerated) {
		this.isModerated = isModerated;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
