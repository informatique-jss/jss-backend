package com.jss.osiris.modules.osiris.crm.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;
import com.jss.osiris.modules.myjss.wordpress.model.Post;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Transient;

@Entity
public class Comment implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "comment_sequence", sequenceName = "comment_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comment_sequence")
	@JsonView({ JacksonViews.OsirisListView.class, JacksonViews.MyJssDetailedView.class })
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_mail")
	@JsonView(JacksonViews.OsirisListView.class)
	private Mail mail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_parent_comment")
	@JsonIgnoreProperties(value = { "childrenComments" }, allowSetters = true)
	private Comment parentComment;

	@OneToMany(targetEntity = Comment.class, mappedBy = "parentComment")
	@JsonView({ JacksonViews.MyJssDetailedView.class })
	@JsonIgnoreProperties(value = { "parentComment", "childrenComments" }, allowSetters = true)
	private List<Comment> childrenComments;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_post")
	@JsonView({ JacksonViews.OsirisListView.class })
	private Post post;

	@Column(columnDefinition = "TEXT", nullable = false)
	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
	private String content;

	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
	private String authorFirstName;

	@JsonView({ JacksonViews.OsirisListView.class })
	private String authorLastName;

	@Transient
	private String authorLastNameInitials;

	@JsonView({ JacksonViews.MyJssDetailedView.class, JacksonViews.OsirisListView.class })
	private LocalDateTime creationDate;

	@JsonView(JacksonViews.OsirisListView.class)
	private Boolean isModerated = false;

	@JsonView(JacksonViews.OsirisListView.class)
	private Boolean isDeleted = false;

	public Integer getId() {
		return id;
	}

	public Mail getMail() {
		return mail;
	}

	public void setMail(Mail mail) {
		this.mail = mail;
	}

	public Comment getParentComment() {
		return parentComment;
	}

	public void setParentComment(Comment parentComment) {
		this.parentComment = parentComment;
	}

	public List<Comment> getChildrenComments() {
		return childrenComments;
	}

	public void setChildrenComments(List<Comment> childrenComments) {
		this.childrenComments = childrenComments;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthorFirstName() {
		return authorFirstName;
	}

	public void setAuthorFirstName(String authorFirstName) {
		this.authorFirstName = authorFirstName;
	}

	public String getAuthorLastName() {
		return authorLastName;
	}

	@JsonView(JacksonViews.MyJssListView.class)
	public void setAuthorLastName(String authorLastName) {
		this.authorLastName = authorLastName;
	}

	@JsonView({ JacksonViews.MyJssListView.class, JacksonViews.MyJssDetailedView.class })
	public String getAuthorLastNameInitials() {
		if (this.getAuthorLastName() != null) {
			this.authorLastNameInitials = this.getAuthorLastName().substring(0, 1);
		}
		return this.authorLastNameInitials;
	}

	public void setAuthorLastNameInitials(String authorLastNameInitials) {
		this.authorLastNameInitials = authorLastNameInitials;
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
