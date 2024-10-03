package com.jss.osiris.modules.osiris.quotation.model;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.model.Document;
import com.jss.osiris.modules.osiris.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.IDocument;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface IQuotation extends IId, IAttachment, IDocument {
	public Integer getId();

	public void setId(Integer id);

	public Employee getAssignedTo();

	public void setAssignedTo(Employee assignedTo);

	public Responsable getResponsable();

	public void setResponsable(Responsable responsable);

	public List<SpecialOffer> getSpecialOffers();

	public void setSpecialOffers(List<SpecialOffer> specialOffers);

	public LocalDateTime getCreatedDate();

	public void setCreatedDate(LocalDateTime createdDate);

	public String getDescription();

	public void setDescription(String description);

	public List<Attachment> getAttachments();

	public void setAttachments(List<Attachment> attachments);

	public List<Document> getDocuments();

	public void setDocuments(List<Document> documents);

	public List<AssoAffaireOrder> getAssoAffaireOrders();

	public void setAssoAffaireOrders(List<AssoAffaireOrder> assoAffaireOrder);

	public Boolean getIsQuotation();

	public void setIsQuotation(Boolean isQuotation);

	public CustomerOrderOrigin getCustomerOrderOrigin();

	public void setCustomerOrderOrigin(CustomerOrderOrigin customerOrderOrigin);

	public QuotationAbandonReason getAbandonReason();

	public void setAbandonReason(QuotationAbandonReason abandonReason);

}
