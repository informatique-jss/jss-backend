package com.jss.jssbackend.modules.tiers.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Attachment;
import com.jss.jssbackend.modules.miscellaneous.model.City;
import com.jss.jssbackend.modules.miscellaneous.model.Civility;
import com.jss.jssbackend.modules.miscellaneous.model.Country;
import com.jss.jssbackend.modules.miscellaneous.model.Document;
import com.jss.jssbackend.modules.miscellaneous.model.IId;
import com.jss.jssbackend.modules.miscellaneous.model.Language;
import com.jss.jssbackend.modules.miscellaneous.model.Mail;
import com.jss.jssbackend.modules.miscellaneous.model.Phone;
import com.jss.jssbackend.modules.profile.model.Employee;

public interface ITiers extends Serializable, IId {

	public Date getFirstBilling();

	public Civility getCivility();

	public TiersType getTiersType();

	public TiersCategory getTiersCategory();

	public String getFirstname();

	public String getLastname();

	public Employee getSalesEmployee();

	public Employee getFormalisteEmployee();

	public Employee getInsertionEmployee();

	public String getMailRecipient();

	public Language getLanguage();

	public String getAddress();

	public String getPostalCode();

	public City getCity();

	public Country getCountry();

	public Float getRcaFormaliteRate();

	public Float getRcaInsertionRate();

	public String getObservations();

	public List<Mail> getMails();

	public List<Phone> getPhones();

	public List<Document> getDocuments();

	public List<Attachment> getAttachments();

	public List<TiersFollowup> getTiersFollowups();

}
