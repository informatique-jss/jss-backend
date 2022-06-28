package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Regie addOrUpdateRegie(
            Regie regie) {
        // If mails already exists, get their ids
        if (regie != null && regie.getMails() != null
                && regie.getMails().size() > 0)
            mailService.populateMailIds(regie.getMails());

        // If phones already exists, get their ids
        if (regie != null && regie.getPhones() != null
                && regie.getPhones().size() > 0) {
            phoneService.populateMPhoneIds(regie.getPhones());
        }
        return regieRepository.save(regie);
    }
}
