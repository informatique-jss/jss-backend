package com.jss.osiris.modules.reporting.model;

public interface ITiersReporting {
    public Integer getTiersId();

    public Integer getResponsableId();

    public String getTiersLabel();

    public String getResponsableLabel();

    public String getSalesEmployeeLabel();

    public String getTiersPostalCode();

    public String getTiersCedexComplement();

    public String getTiersCity();

    public String getTiersCountry();

    public String getTiersIntercommunityVat();

    public String getTiersType();

    public String getResponsableType();

    public String getIsTiersIndividual();

    public Integer getNbrCustomerOrder();

    public Integer getNbrAnnouncement();

    public Integer getNbrFormalities();

    public Double getTurnoverAmount();

    public Double getTurnoverAmountWithoutDebour();

    public String getDocTiersPaperRecipient();

    public String getDocTiersPaperNumeric();

    public String getDocTiersBilling();

    public String getDocTiersBillingLabel();

    public String getDocResponsablePaperRecipient();

    public String getDocResponsablePaperNumeric();

    public String getDocResponsableBilling();

    public String getDocResponsableBillingLabel();

    public String getTiersPaymentType();

    public String getTiersPaymentIban();

    public String getTiersPaymentBic();

    public boolean getTiersIsProvisionnalPaymentMandatory();

    public String getResponsableMail();

    public String getResponsablePhone();

    public Integer getNbrQuotation();

    public String getInvoiceDateMonth();
}
