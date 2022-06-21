package com.jss.osiris.modules.tiers.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Civility;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Language;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.profile.model.Employee;

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
