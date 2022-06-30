package com.jss.osiris.modules.quotation.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.repository.CustomerOrderRepository;

@Service
public class CustomerOrderServiceImpl implements CustomerOrderService {

    @Autowired
    CustomerOrderRepository customerOrderRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Override
    public CustomerOrder getCustomerOrder(Integer id) {
        Optional<CustomerOrder> customerOrder = customerOrderRepository.findById(id);
        if (!customerOrder.isEmpty())
            return customerOrder.get();
        return null;
    }

    @Override
    public CustomerOrder addOrUpdateCustomerOrder(CustomerOrder customerOrder) {
        if (customerOrder.getId() == null)
            customerOrder.setCreatedDate(new Date());

        // Complete domiciliation end date
        for (Provision provision : customerOrder.getProvisions()) {
            if (provision.getDomiciliation() != null) {
                Domiciliation domiciliation = provision.getDomiciliation();
                if (domiciliation.getEndDate() == null) {
                    Calendar c = Calendar.getInstance();
                    c.setTime(domiciliation.getStartDate());
                    c.add(Calendar.YEAR, 1);
                    domiciliation.setEndDate(c.getTime());

                    // If mails already exists, get their ids
                    if (domiciliation != null && domiciliation.getMails() != null
                            && domiciliation.getMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getMails());

                    // If mails already exists, get their ids
                    if (domiciliation != null && domiciliation.getActivityMails() != null
                            && domiciliation.getActivityMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getActivityMails());

                    // If mails already exists, get their ids
                    if (domiciliation != null
                            && domiciliation.getLegalGardianMails() != null
                            && domiciliation.getLegalGardianMails().size() > 0)
                        mailService.populateMailIds(domiciliation.getLegalGardianMails());

                    if (domiciliation != null
                            && domiciliation.getLegalGardianPhones() != null
                            && domiciliation.getLegalGardianPhones().size() > 0)
                        phoneService.populateMPhoneIds(domiciliation.getLegalGardianPhones());

                }
            }
        }

        customerOrder = customerOrderRepository.save(customerOrder);
        indexEntityService.indexEntity(customerOrder, customerOrder.getId());
        return customerOrder;
    }
}
