package com.jss.osiris.libs.mail;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;

public interface CustomerMailService {
    public CustomerMail getMail(Integer id);

    public void addMailToQueue(CustomerMail mail);

    public List<CustomerMail> getMailsByQuotation(Quotation quotation);

    public List<CustomerMail> getMailsByCustomerOrder(CustomerOrder customerOrder);

    public List<CustomerMail> getMailsByConfrere(Confrere confrere);

    public List<CustomerMail> getMailsByTiers(Tiers tiers);

    public List<CustomerMail> getMailsByResponsable(Responsable responsable);

    public void sendNextMail() throws OsirisException;

}
