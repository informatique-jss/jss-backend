package com.jss.osiris.modules.tiers.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.accounting.model.AccountingAccountCouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.tiers.model.Responsable;
import com.jss.osiris.modules.tiers.model.Tiers;
import com.jss.osiris.modules.tiers.repository.TiersRepository;

@Service
public class TiersServiceImpl implements TiersService {

    @Autowired
    TiersRepository tiersRepository;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    MailService mailService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    SearchService searchService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Autowired
    InvoiceService invoiceService;

    @Override
    public Tiers getTiers(Integer id) {
        Optional<Tiers> tiers = tiersRepository.findById(id);
        if (!tiers.isEmpty()) {
            Tiers tiersInstance = tiers.get();
            tiersInstance.setFirstBilling(invoiceService.getFirstBillingDateForTiers(tiersInstance));
            if (tiersInstance.getResponsables() != null) {
                for (Responsable responsable : tiersInstance.getResponsables())
                    responsable.setFirstBilling(invoiceService.getFirstBillingDateForResponsable(responsable));
            }
            return tiersInstance;
        }
        return null;
    }

    @Override
    public Tiers addOrUpdateTiers(Tiers tiers) throws Exception {
        // If mails already exists, get their ids
        if (tiers != null && tiers.getMails() != null && tiers.getMails().size() > 0)
            mailService.populateMailIds(tiers.getMails());

        // If phones already exists, get their ids
        if (tiers != null && tiers.getPhones() != null && tiers.getPhones().size() > 0) {
            phoneService.populateMPhoneIds(tiers.getPhones());
        }

        // If document mails already exists, get their ids
        if (tiers.getDocuments() != null && tiers.getDocuments().size() > 0) {
            for (Document document : tiers.getDocuments()) {
                if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
                    mailService.populateMailIds(document.getMailsAffaire());
                if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
                    mailService.populateMailIds(document.getMailsClient());
            }
        }

        // Generate accounting accounts
        if (tiers.getId() == null
                || tiers.getAccountingAccountCustomer() == null && tiers.getAccountingAccountProvider() == null) {
            String label = "";
            if (tiers.getIsIndividual()) {
                label = tiers.getFirstname() + " " + tiers.getLastname();
            } else {
                label = tiers.getDenomination();
            }
            AccountingAccountCouple accountingAccountCouple = accountingAccountService
                    .generateAccountingAccountsForEntity(label);
            tiers.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
            tiers.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
        }

        tiers = tiersRepository.save(tiers);
        indexEntityService.indexEntity(tiers, tiers.getId());
        if (tiers.getResponsables() != null && tiers.getResponsables().size() > 0) {
            for (Responsable responsable : tiers.getResponsables()) {
                indexEntityService.indexEntity(responsable, responsable.getId());

                if (responsable.getDocuments() != null && responsable.getDocuments().size() > 0) {
                    for (Document document : responsable.getDocuments()) {
                        if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
                            mailService.populateMailIds(document.getMailsAffaire());
                        if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
                            mailService.populateMailIds(document.getMailsClient());
                    }
                }
            }
        }

        return tiers;
    }

    @Override
    public Tiers getTiersByIdResponsable(Integer idResponsable) {
        Responsable responsable = responsableService.getResponsable(idResponsable);
        if (responsable != null)
            return this.getTiers(responsable.getTiers().getId());
        return null;
    }

    @Override
    public List<Tiers> getIndividualTiersByKeyword(String searchedValue) {
        List<Tiers> foundTiers = new ArrayList<Tiers>();
        List<IndexEntity> tiers = searchService.searchForEntities(searchedValue, Tiers.class.getSimpleName());
        if (tiers != null && tiers.size() > 0) {
            for (IndexEntity t : tiers) {
                List<Tiers> individualTiers = tiersRepository.findByIsIndividualAndIdTiers(t.getEntityId());
                if (individualTiers != null && individualTiers.size() == 1)
                    foundTiers.add(individualTiers.get(0));
            }
        }
        return foundTiers;
    }
}
