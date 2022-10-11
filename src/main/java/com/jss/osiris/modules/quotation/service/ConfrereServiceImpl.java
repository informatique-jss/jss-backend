package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.quotation.model.Confrere;
import com.jss.osiris.modules.quotation.repository.ConfrereRepository;

@Service
public class ConfrereServiceImpl implements ConfrereService {

    @Autowired
    ConfrereRepository confrereRepository;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Override
    public List<Confrere> getConfreres() {
        return IterableUtils.toList(confrereRepository.findAll());
    }

    @Override
    public Confrere getConfrere(Integer id) {
        Optional<Confrere> confrere = confrereRepository.findById(id);
        if (confrere.isPresent())
            return confrere.get();
        return null;
    }

    @Override
    public Confrere addOrUpdateConfrere(Confrere confrere) throws Exception {
        if (confrere == null)
            throw new Exception("Confrere provided is null");

        // If mails already exists, get their ids
        if (confrere != null && confrere.getMails() != null
                && confrere.getMails().size() > 0)
            mailService.populateMailIds(confrere.getMails());

        if (confrere != null && confrere.getAccountingMails() != null
                && confrere.getAccountingMails().size() > 0)
            mailService.populateMailIds(confrere.getAccountingMails());

        // If phones already exists, get their ids
        if (confrere != null && confrere.getPhones() != null
                && confrere.getPhones().size() > 0) {
            phoneService.populateMPhoneIds(confrere.getPhones());
        }

        // If document mails already exists, get their ids
        if (confrere.getDocuments() != null && confrere.getDocuments().size() > 0) {
            for (Document document : confrere.getDocuments()) {
                if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
                    mailService.populateMailIds(document.getMailsAffaire());
                if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
                    mailService.populateMailIds(document.getMailsClient());
            }
        }

        // Generate accounting accounts
        if (confrere.getRegie() == null)
            if (confrere.getId() == null
                    || confrere.getAccountingAccountCustomer() == null
                            && confrere.getAccountingAccountProvider() == null
                            && confrere.getAccountingAccountDeposit() == null) {
                AccountingAccountTrouple accountingAccountCouple = accountingAccountService
                        .generateAccountingAccountsForEntity(confrere.getLabel());
                confrere.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
                confrere.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
                confrere.setAccountingAccountDeposit(accountingAccountCouple.getAccountingAccountDeposit());
            }
        return confrereRepository.save(confrere);
    }
}
