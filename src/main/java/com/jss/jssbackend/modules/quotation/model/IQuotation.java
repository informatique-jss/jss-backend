package com.jss.jssbackend.modules.quotation.model;

import java.io.Serializable;
import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.SpecialOffer;
import com.jss.jssbackend.modules.tiers.model.Responsable;
import com.jss.jssbackend.modules.tiers.model.Tiers;

public interface IQuotation extends Serializable {

	public Integer getId();

	public Tiers getTiers();

	public Responsable getResponsable();

	public SpecialOffer getSpecialOffer();

	public QuotationLabelType getQuotationLabelType();

	public String getQuotationLabel();

	public RecordType getRecordType();

	public List<Provision> getProvisions();

}
