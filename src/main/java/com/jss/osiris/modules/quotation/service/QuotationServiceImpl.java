package com.jss.osiris.modules.quotation.service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Domiciliation;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.quotation.repository.QuotationRepository;

@Service
public class QuotationServiceImpl implements QuotationService {

    @Autowired
    QuotationRepository quotationRepository;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Override
    public Quotation getQuotation(Integer id) {
        Optional<Quotation> quotation = quotationRepository.findById(id);
        if (!quotation.isEmpty())
            return quotation.get();
        return null;
    }

    @Override
    public Quotation addOrUpdateQuotation(Quotation quotation) {
        if (quotation.getId() == null)
            quotation.setCreatedDate(new Date());
        for (Affaire affaire : quotation.getAffaires()) {
            if (affaire.getRna() != null)
                affaire.setRna(affaire.getRna().toUpperCase().replaceAll(" ", ""));
            if (affaire.getSiren() != null)
                affaire.setSiren(affaire.getSiren().toUpperCase().replaceAll(" ", ""));
            if (affaire.getSiret() != null)
                affaire.setSiret(affaire.getSiret().toUpperCase().replaceAll(" ", ""));

            // If mails already exists, get their ids
            if (affaire != null && affaire.getMails() != null && affaire.getMails().size() > 0)
                mailService.populateMailIds(affaire.getMails());

            // If phones already exists, get their ids
            if (affaire != null && affaire.getPhones() != null && affaire.getPhones().size() > 0) {
                phoneService.populateMPhoneIds(affaire.getPhones());
            }

            // Complete domiciliation end date
            for (Provision provision : affaire.getProvisions()) {
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
        }
        quotation = quotationRepository.save(quotation);
        indexEntityService.indexEntity(quotation, quotation.getId());

        for (Affaire affaire : quotation.getAffaires()) {
            for (Provision provision : affaire.getProvisions()) {
                indexEntityService.indexEntity(provision.getAffaire(), provision.getAffaire().getId());
            }
        }
        return quotation;
    }
}
