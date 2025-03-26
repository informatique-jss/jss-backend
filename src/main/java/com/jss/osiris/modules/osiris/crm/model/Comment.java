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

@Entity
public class Comment implements Serializable, IId {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	@JsonView(JacksonViews.MyJssView.class)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_mail")
	@JsonView(JacksonViews.MyJssView.class)
	private Mail mail;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_parent_comment")
	@JsonView(JacksonViews.MyJssView.class)
	@JsonIgnoreProperties(value = { "childrenComments" }, allowSetters = true)
	private Comment parentComment;

	@OneToMany(targetEntity = Comment.class, mappedBy = "parentComment")
	@JsonView(JacksonViews.MyJssView.class)
	@JsonIgnoreProperties(value = { "parentComment", "childrenComments" }, allowSetters = true)
	private List<Comment> childrenComments;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_post")
	@JsonView(JacksonViews.MyJssView.class)
	private Post post;

	@Column(columnDefinition = "TEXT", nullable = false)
	@JsonView(JacksonViews.MyJssView.class)
	private String content;

	@JsonView(JacksonViews.MyJssView.class)
	private String authorFirstName;

	@JsonView(JacksonViews.MyJssView.class)
	private String authorLastName;

	@JsonView(JacksonViews.MyJssView.class)
	private LocalDateTime creationDate;

	@JsonView(JacksonViews.MyJssView.class)
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

	public void setAuthorLastName(String authorLastName) {
		this.authorLastName = authorLastName;
	}

	public LocalDateTime getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(LocalDateTime creationDate) {
		this.creationDate = creationDate;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

}
