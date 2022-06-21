package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.WeekDay;

@Entity
public class Confrere implements Serializable, IId {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	@Column(length = 40)
	private String denomination;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_confrere_department", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_department"))
	private List<Department> departments;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_confrere_mail", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_mail"))
	private List<Mail> mails;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_confrere_phone", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_phone"))
	private List<Phone> phones;

	private Integer discountRate;

	private Integer reinvoicing;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_confrere_publication_day", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_weekday"))
	private List<WeekDay> weekDays;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "asso_confrere_journal_type", joinColumns = @JoinColumn(name = "id_confrere"), inverseJoinColumns = @JoinColumn(name = "id_journal_type"))
	private List<JournalType> journalType;

	@Column(length = 60)
	private String lastShipmentForPublication;

	@Column(length = 20)
	private String preference;

	private Float shippingCosts;

	private Float administrativeFees;

	private Integer numberOfPrint;

	public Integer getId() {
		return id;
	}

	public List<JournalType> getJournalType() {
		return journalType;
	}

	public void setJournalType(List<JournalType> journalType) {
		this.journalType = journalType;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDenomination() {
		return denomination;
	}

	public void setDenomination(String denomination) {
		this.denomination = denomination;
	}

	public List<Department> getDepartments() {
		return departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public List<Mail> getMails() {
		return mails;
	}

	public void setMails(List<Mail> mails) {
		this.mails = mails;
	}

	public List<Phone> getPhones() {
		return phones;
	}

	public void setPhones(List<Phone> phones) {
		this.phones = phones;
	}

	public Integer getDiscountRate() {
		return discountRate;
	}

	public void setDiscountRate(Integer discountRate) {
		this.discountRate = discountRate;
	}

	public Integer getReinvoicing() {
		return reinvoicing;
	}

	public void setReinvoicing(Integer reinvoicing) {
		this.reinvoicing = reinvoicing;
	}

	public List<WeekDay> getWeekDays() {
		return weekDays;
	}

	public void setWeekDays(List<WeekDay> weekDays) {
		this.weekDays = weekDays;
	}

	public String getLastShipmentForPublication() {
		return lastShipmentForPublication;
	}

	public void setLastShipmentForPublication(String lastShipmentForPublication) {
		this.lastShipmentForPublication = lastShipmentForPublication;
	}

	public String getPreference() {
		return preference;
	}

	public void setPreference(String preference) {
		this.preference = preference;
	}

	public Float getShippingCosts() {
		return shippingCosts;
	}

	public void setShippingCosts(Float shippingCosts) {
		this.shippingCosts = shippingCosts;
	}

	public Float getAdministrativeFees() {
		return administrativeFees;
	}

	public void setAdministrativeFees(Float administrativeFees) {
		this.administrativeFees = administrativeFees;
	}

	public Integer getNumberOfPrint() {
		return numberOfPrint;
	}

	public void setNumberOfPrint(Integer numberOfPrint) {
		this.numberOfPrint = numberOfPrint;
	}

}
