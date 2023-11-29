package com.jss.osiris.modules.quotation.model;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Attachment;
import com.jss.osiris.modules.miscellaneous.model.CustomerOrderOrigin;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IAttachment;
import com.jss.osiris.modules.miscellaneous.model.IDocument;
import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface IQuotation extends IId, IAttachment, IDocument {
	public Integer getId();

	public void setId(Integer id);

	public Employee getAssignedTo();

	public void setAssignedTo(Employee assignedTo);

	public Tiers getTiers();

	public void setTiers(Tiers tiers);

	public Responsable getResponsable();

	public void setResponsable(Responsable responsable);

	public List<SpecialOffer> getSpecialOffers();

	public void setSpecialOffers(List<SpecialOffer> specialOffers);

	public LocalDateTime getCreatedDate();

	public void setCreatedDate(LocalDateTime createdDate);

	public String getObservations();

	public void setObservations(String observations);

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

	public Confrere getConfrere();

	public void setConfrere(Confrere confrere);

	public String getCustomerMailCustomMessage();

	public CustomerOrderOrigin getCustomerOrderOrigin();

	public void setCustomerOrderOrigin(CustomerOrderOrigin customerOrderOrigin);

	public Boolean getOverrideSpecialOffer();

	public AbandonReason getAbandonReason();

	public void setAbandonReason(AbandonReason abandonReason);

}
