package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.osiris.quotation.model.Confrere;
import com.jss.osiris.modules.osiris.quotation.repository.ConfrereRepository;

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

        // If phones already exists, get their ids
        if (confrere != null && confrere.getPhones() != null
                && confrere.getPhones().size() > 0) {
            phoneService.populatePhoneIds(confrere.getPhones());
        }

        return confrereRepository.save(confrere);
    }
}
