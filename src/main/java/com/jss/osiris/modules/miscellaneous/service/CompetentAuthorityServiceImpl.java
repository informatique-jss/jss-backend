package com.jss.osiris.modules.miscellaneous.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.repository.CompetentAuthorityRepository;

@Service
public class CompetentAuthorityServiceImpl implements CompetentAuthorityService {

    @Autowired
    CompetentAuthorityRepository competentAuthorityRepository;

    @Autowired
    PhoneService phoneService;

    @Autowired
    MailService mailService;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Override
    @Cacheable(value = "competentAuthorityList", key = "#root.methodName")
    public List<CompetentAuthority> getCompetentAuthorities() {
        return IterableUtils.toList(competentAuthorityRepository.findAll());
    }

    @Override
    @Cacheable(value = "competentAuthority", key = "#id")
    public CompetentAuthority getCompetentAuthority(Integer id) {
        Optional<CompetentAuthority> competentAuthority = competentAuthorityRepository.findById(id);
        if (competentAuthority.isPresent())
            return competentAuthority.get();
        return null;
    }

    @Override
    public CompetentAuthority getCompetentAuthorityByApiId(String apiId) {
        Optional<CompetentAuthority> competentAuthority = competentAuthorityRepository.findByApiId(apiId);
        if (competentAuthority.isPresent())
            return competentAuthority.get();
        return null;
    }

    @Override
    public CompetentAuthority getCompetentAuthorityByOwncloudFolderName(String folderName) {
        Optional<CompetentAuthority> competentAuthority = competentAuthorityRepository
                .findByOwncloudFolderName(folderName);
        if (competentAuthority.isPresent())
            return competentAuthority.get();
        return null;
    }

    @Override
    public CompetentAuthority getCompetentAuthorityByInpiReference(String inpiReference) {
        Optional<CompetentAuthority> competentAuthority = competentAuthorityRepository
                .findByInpiReference(inpiReference);
        if (competentAuthority.isPresent())
            return competentAuthority.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "competentAuthorityList", allEntries = true),
            @CacheEvict(value = "competentAuthorityByTypeList", allEntries = true),
            @CacheEvict(value = "competentAuthority", key = "#competentAuthority.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public CompetentAuthority addOrUpdateCompetentAuthority(
            CompetentAuthority competentAuthority) throws OsirisException {
        if (competentAuthority == null)
            throw new OsirisException(null, "Competent authority provided is null");

        // If mails already exists, get their ids
        if (competentAuthority != null && competentAuthority.getMails() != null
                && competentAuthority.getMails().size() > 0)
            mailService.populateMailIds(competentAuthority.getMails());

        // If mails already exists, get their ids
        if (competentAuthority != null && competentAuthority.getAccountingMails() != null
                && competentAuthority.getAccountingMails().size() > 0)
            mailService.populateMailIds(competentAuthority.getAccountingMails());

        // If phones already exists, get their ids
        if (competentAuthority != null && competentAuthority.getPhones() != null
                && competentAuthority.getPhones().size() > 0) {
            phoneService.populatePhoneIds(competentAuthority.getPhones());
        }

        // Generate accounting accounts
        if (competentAuthority.getId() == null
                || competentAuthority.getAccountingAccountCustomer() == null
                        && competentAuthority.getAccountingAccountProvider() == null
                        && competentAuthority.getAccountingAccountDepositProvider() == null) {
            AccountingAccountTrouple accountingAccountCouple = accountingAccountService
                    .generateAccountingAccountsForEntity(competentAuthority.getLabel(), true);
            competentAuthority.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
            competentAuthority.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
            competentAuthority
                    .setAccountingAccountDepositProvider(accountingAccountCouple.getAccountingAccountDeposit());
        }
        return competentAuthorityRepository.save(competentAuthority);
    }

    @Override
    public List<CompetentAuthority> getCompetentAuthorityByDepartment(Integer departmentId, String authority) {
        List<CompetentAuthority> outAuthorities = new ArrayList<CompetentAuthority>();
        List<CompetentAuthority> authorities;
        if (authority != null)
            authorities = competentAuthorityRepository
                    .findByLabelContainingIgnoreCase(StringUtils.stripAccents(authority));
        else
            authorities = IterableUtils.toList(competentAuthorityRepository.findAll());
        if (departmentId != null && authorities != null && authorities.size() > 0) {
            for (CompetentAuthority a : authorities) {
                for (Department d : a.getDepartments()) {
                    if (d.getId().equals(departmentId) && outAuthorities.indexOf(a) < 0)
                        outAuthorities.add(a);
                }
            }
        } else {
            outAuthorities = authorities;
        }
        return outAuthorities;
    }

    @Override
    public List<CompetentAuthority> getCompetentAuthorityByCity(Integer cityId) {
        return competentAuthorityRepository.findByCities_Id(cityId);
    }

    @Override
    @Cacheable(value = "competentAuthorityByTypeList", key = "#competentAuthorityTypeId")
    public List<CompetentAuthority> getCompetentAuthorityByAuthorityType(Integer competentAuthorityTypeId) {
        return competentAuthorityRepository.findByCompetentAuthorityType_Id(competentAuthorityTypeId);
    }
}
