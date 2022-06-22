package com.jss.osiris.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.IId;
import com.jss.osiris.modules.miscellaneous.model.SpecialOffer;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface IQuotation extends Serializable, IId {

	public Integer getId();

	public Tiers getTiers();

	public Responsable getResponsable();

	public List<SpecialOffer> getSpecialOffers();

	public QuotationLabelType getQuotationLabelType();

	public String getQuotationLabel();

	public RecordType getRecordType();

	public List<Affaire> getAffaires();

}
