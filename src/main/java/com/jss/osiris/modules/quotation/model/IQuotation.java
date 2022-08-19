package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface IQuotation extends Serializable, IId {

	public Integer getId();

	public void setId(Integer id);

	public Tiers getTiers();

	public void setTiers(Tiers tiers);

	public Responsable getResponsable();

	public void setResponsable(Responsable responsable);

	public List<SpecialOffer> getSpecialOffers();

	public void setSpecialOffers(List<SpecialOffer> specialOffers);

	public LocalDateTime getCreatedDate();

	public void setCreatedDate(LocalDateTime createdDate);

	public QuotationStatus getStatus();

	public void setStatus(QuotationStatus status);

	public String getObservations();

	public void setObservations(String observations);

	public String getDescription();

	public void setDescription(String description);

	public List<Attachment> getAttachments();

	public void setAttachments(List<Attachment> attachments);

	public List<Document> getDocuments();

	public void setDocuments(List<Document> documents);

	public QuotationLabelType getQuotationLabelType();

	public void setQuotationLabelType(QuotationLabelType quotationLabelType);

	public Responsable getCustomLabelResponsable();

	public void setCustomLabelResponsable(Responsable customLabelResponsable);

	public Tiers getCustomLabelTiers();

	public void setCustomLabelTiers(Tiers customLabelTiers);

	public RecordType getRecordType();

	public void setRecordType(RecordType recordType);

	public List<Provision> getProvisions();

	public void setProvisions(List<Provision> provisions);

	public List<Mail> getMails();

	public void setMails(List<Mail> mails);

	public List<Phone> getPhones();

	public void setPhones(List<Phone> phones);

	public List<InvoiceItem> getInvoiceItems();

	public void setInvoiceItems(List<InvoiceItem> invoiceItems);

	public Boolean getIsQuotation();

	public void setIsQuotation(Boolean isQuotation);

	public Confrere getConfrere();

	public void setConfrere(Confrere confrere);

}
