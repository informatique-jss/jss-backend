package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.Department;
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
    public List<Confrere> searchConfrereFilteredByDepartmentAndName(Department department, String label) {
        Integer departmentId = 0;
        if (department != null)
            departmentId = department.getId();
        return confrereRepository.findConfrereFilteredByDepartmentAndName(departmentId, label);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Confrere addOrUpdateConfrere(Confrere confrere) throws OsirisException {
        if (confrere == null)
            throw new OsirisException(null, "Confrere provided is null");

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
            phoneService.populatePhoneIds(confrere.getPhones());
        }

        // If document mails already exists, get their ids
        if (confrere.getDocuments() != null && confrere.getDocuments().size() > 0) {
            for (Document document : confrere.getDocuments()) {
                document.setConfrere(confrere);
                if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
                    mailService.populateMailIds(document.getMailsAffaire());
                if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
                    mailService.populateMailIds(document.getMailsClient());
            }
        }

        // Generate accounting accounts
        if (confrere.getId() == null
                || confrere.getAccountingAccountCustomer() == null
                        && confrere.getAccountingAccountProvider() == null
                        && confrere.getAccountingAccountDeposit() == null) {
            AccountingAccountTrouple accountingAccountCouple = accountingAccountService
                    .generateAccountingAccountsForEntity(confrere.getLabel(), false);
            confrere.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
            confrere.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
            confrere.setAccountingAccountDeposit(accountingAccountCouple.getAccountingAccountDeposit());
        }
        confrere = confrereRepository.save(confrere);

        // Set default customer order assignation to sales employee if not set
        if (confrere.getDefaultCustomerOrderEmployee() == null)
            confrere.setDefaultCustomerOrderEmployee(confrere.getSalesEmployee());

        if (confrere.getAccountingAccountCustomer() != null || confrere.getAccountingAccountDeposit() != null
                || confrere.getAccountingAccountProvider() != null) {
            confrere.getAccountingAccountCustomer()
                    .setLabel("Client - " + (confrere.getLabel() != null ? confrere.getLabel() : ""));
            confrere.getAccountingAccountDeposit()
                    .setLabel("Acompte - " + (confrere.getLabel() != null ? confrere.getLabel() : ""));
            confrere.getAccountingAccountProvider()
                    .setLabel("Fournisseur - " + (confrere.getLabel() != null ? confrere.getLabel() : ""));
        }
        confrere = confrereRepository.save(confrere);
        return confrere;
    }
}
