package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.quotation.model.Regie;
import com.jss.osiris.modules.quotation.repository.RegieRepository;

@Service
public class RegieServiceImpl implements RegieService {

    @Autowired
    RegieRepository regieRepository;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Override
    public List<Regie> getRegies() {
        return IterableUtils.toList(regieRepository.findAll());
    }

    @Override
    public Regie getRegie(Integer id) {
        Optional<Regie> regie = regieRepository.findById(id);
        if (!regie.isEmpty())
            return regie.get();
        return null;
    }

    @Override
    public Regie addOrUpdateRegie(Regie regie) throws Exception {
        // If mails already exists, get their ids
        if (regie != null && regie.getMails() != null
                && regie.getMails().size() > 0)
            mailService.populateMailIds(regie.getMails());

        // If phones already exists, get their ids
        if (regie != null && regie.getPhones() != null
                && regie.getPhones().size() > 0) {
            phoneService.populateMPhoneIds(regie.getPhones());
        }

        // Generate accounting accounts
        if (regie.getId() == null
                || regie.getAccountingAccountCustomer() == null && regie.getAccountingAccountProvider() == null
                        && regie.getAccountingAccountDeposit() == null) {
            AccountingAccountTrouple accountingAccountCouple = accountingAccountService
                    .generateAccountingAccountsForEntity(regie.getLabel());
            regie.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
            regie.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
            regie.setAccountingAccountDeposit(accountingAccountCouple.getAccountingAccountDeposit());
        }

        return regieRepository.save(regie);
    }
}
