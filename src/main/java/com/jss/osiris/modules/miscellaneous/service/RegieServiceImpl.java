package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.Regie;
import com.jss.osiris.modules.miscellaneous.repository.RegieRepository;

@Service
public class RegieServiceImpl implements RegieService {

    @Autowired
    RegieRepository regieRepository;

    @Autowired
    MailService mailService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Override
    public List<Regie> getRegies() {
        return IterableUtils.toList(regieRepository.findAll());
    }

    @Override
    public Regie getRegie(Integer id) {
        Optional<Regie> regie = regieRepository.findById(id);
        if (regie.isPresent())
            return regie.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Regie addOrUpdateRegie(Regie regie) throws Exception {
        // If mails already exists, get their ids
        if (regie != null && regie.getMails() != null && regie.getMails().size() > 0)
            mailService.populateMailIds(regie.getMails());

        // If phones already exists, get their ids
        if (regie != null && regie.getPhones() != null && regie.getPhones().size() > 0) {
            phoneService.populateMPhoneIds(regie.getPhones());
        }

        return regieRepository.save(regie);
    }
}
