package com.jss.osiris.libs.mail;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;

public interface CustomerMailService {
    public CustomerMail getMail(Integer id);

    public void addMailToQueue(CustomerMail mail) throws OsirisException;

    public void sendTemporizedMails() throws OsirisException;

    public List<CustomerMail> getMailsByQuotation(Quotation quotation);

    public List<CustomerMail> getMailsByCustomerOrder(CustomerOrder customerOrder);

    public List<CustomerMail> getMailsByConfrere(Confrere confrere);

    public List<CustomerMail> getMailsByTiers(Tiers tiers);

    public List<CustomerMail> getMailsByResponsable(Responsable responsable);

    public void sendMail(CustomerMail mail)
            throws OsirisException, OsirisValidationException, OsirisClientMessageException;

    public void sendCustomerMailImmediatly(CustomerMail mail) throws OsirisException;

    public CustomerMail cancelCustomerMail(CustomerMail mail) throws OsirisException;

}
