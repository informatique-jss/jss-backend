package com.jss.osiris.modules.osiris.profile.model;

import java.io.Serializable;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jss.osiris.libs.search.model.IndexedField;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Employee implements Serializable, IId, AttributesMapper<Employee>, IOsirisUser {

	@Id
	@SequenceGenerator(name = "hibernate_sequence", sequenceName = "hibernate_sequence", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hibernate_sequence")
	private Integer id;

	@Column(length = 20)
	@IndexedField
	private String firstname;

	@Column(length = 20)
	@IndexedField
	private String lastname;

	@IndexedField
	private String username;

	private String mail;
	private String title;
	@Column(length = 1000)
	private String adPath;
	private Boolean isActive;

	@OneToMany(targetEntity = Employee.class, fetch = FetchType.LAZY)
	@JoinTable(name = "asso_employee_backup", joinColumns = @JoinColumn(name = "id_employee"), inverseJoinColumns = @JoinColumn(name = "id_employee_backup"))
	@JsonIgnoreProperties(value = { "backups" }, allowSetters = true)
	private List<Employee> backups;

	public Employee mapFromAttributes(Attributes attrs) throws NamingException {
		if (attrs.get("givenName") == null || attrs.get("sn") == null || attrs.get("sAMAccountName") == null
				|| attrs.get("distinguishedName") == null)
			return null;

		Employee employee = new Employee();
		employee.setFirstname((String) attrs.get("givenName").get());

		employee.setLastname((String) attrs.get("sn").get());
		employee.setUsername((String) attrs.get("sAMAccountName").get());
		employee.setAdPath((String) attrs.get("distinguishedName").get());
		if (attrs.get("mail") != null)
			employee.setMail((String) attrs.get("mail").get());
		if (attrs.get("title") != null)
			employee.setTitle((String) attrs.get("title").get());
		return employee;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getAdPath() {
		return adPath;
	}

	public void setAdPath(String adPath) {
		this.adPath = adPath;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Employee> getBackups() {
		return backups;
	}

	public void setBackups(List<Employee> backups) {
		this.backups = backups;
	}

}
