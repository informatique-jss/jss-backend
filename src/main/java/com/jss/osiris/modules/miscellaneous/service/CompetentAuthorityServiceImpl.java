package com.jss.osiris.modules.miscellaneous.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.AssoMailCompetentAuthorityServiceFamilyGroup;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthorityType;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.ICompetentAuthorityMailReminder;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.repository.CompetentAuthorityRepository;
import com.jss.osiris.modules.profile.service.EmployeeService;
import com.jss.osiris.modules.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.quotation.service.CustomerOrderStatusService;
import com.jss.osiris.modules.quotation.service.FormaliteStatusService;
import com.jss.osiris.modules.quotation.service.ProvisionService;
import com.jss.osiris.modules.quotation.service.SimpleProvisionStatusService;

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

    @Autowired
    MailHelper mailHelper;

    @Autowired
    FormaliteStatusService formaliteStatusService;

    @Autowired
    SimpleProvisionStatusService simpleProvisionStatusService;

    @Autowired
    ProvisionService provisionService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Override
    public List<CompetentAuthority> getCompetentAuthorities() {
        return IterableUtils.toList(competentAuthorityRepository.findAll());
    }

    @Override
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
    public List<CompetentAuthority> getCompetentAuthorityByInpiReference(String inpiReference) {
        return competentAuthorityRepository.findByInpiReference(inpiReference);
    }

    @Override
    public List<CompetentAuthority> getCompetentAuthorityByCityAndAuthorityType(City city,
            CompetentAuthorityType competentAuthorityType) {
        return competentAuthorityRepository.findByCities_IdAndCompetentAuthorityType(city.getId(),
                competentAuthorityType);
    }

    @Override
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

        // If asso mail for specific mail reminder, get their ids
        if (competentAuthority.getAssoMailCompetentAuthorityServiceFamilyGroups() != null
                && competentAuthority.getAssoMailCompetentAuthorityServiceFamilyGroups().size() > 0) {
            for (AssoMailCompetentAuthorityServiceFamilyGroup asso : competentAuthority
                    .getAssoMailCompetentAuthorityServiceFamilyGroups()) {
                mailService.populateMailIds(asso.getMails());
                asso.setCompetentAuthority(competentAuthority);
            }
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
        } else {
            accountingAccountService.updateAccountingAccountLabel(competentAuthority.getAccountingAccountCustomer(),
                    competentAuthority.getLabel());
            accountingAccountService.updateAccountingAccountLabel(competentAuthority.getAccountingAccountDeposit(),
                    competentAuthority.getLabel());
            accountingAccountService.updateAccountingAccountLabel(competentAuthority.getAccountingAccountProvider(),
                    competentAuthority.getLabel());
        }
        return competentAuthorityRepository.save(competentAuthority);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
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
    public List<CompetentAuthority> getCompetentAuthorityByAuthorityType(Integer competentAuthorityTypeId) {
        return competentAuthorityRepository.findByCompetentAuthorityType_Id(competentAuthorityTypeId);
    }

    @Override
    public CompetentAuthority getCompetentAuthorityByIntercommunityVat(String intercommunityVat) {
        return competentAuthorityRepository.findByIntercommunityVat(intercommunityVat);
    }

    @Override
    public CompetentAuthority getCompetentAuthorityByAzureCustomReference(String azureCustomReference) {
        return competentAuthorityRepository.findByAzureCustomReference(azureCustomReference);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendRemindersToCompetentAuthorities() throws OsirisException, OsirisClientMessageException {

        SimpleProvisionStatus simpleProvisionWaitingAcStatus = simpleProvisionStatusService
                .getSimpleProvisionStatusByCode(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY);
        FormaliteStatus formaliteWaitingAcStatus = formaliteStatusService
                .getFormaliteStatusByCode(FormaliteStatus.FORMALITE_WAITING_DOCUMENT_AUTHORITY);
        List<ICompetentAuthorityMailReminder> competentMailResult = competentAuthorityRepository
                .findCompetentAuthoritiesMailToSend(simpleProvisionWaitingAcStatus.getCode(),
                        formaliteWaitingAcStatus.getCode(), simpleProvisionWaitingAcStatus.getId(),
                        formaliteWaitingAcStatus.getId(),
                        customerOrderStatusService.getCustomerOrderStatusByCode(CustomerOrderStatus.ABANDONED).getId());

        if (competentMailResult != null && competentMailResult.size() > 0) {
            String currentKey = null;
            List<Provision> provisionToSend = new ArrayList<Provision>();
            ICompetentAuthorityMailReminder lastReminder = null;

            for (ICompetentAuthorityMailReminder reminder : competentMailResult) {
                if (reminder.getMailId() != null && reminder.getMailId().length() > 0) {
                    String newKey = computeKeyForICompetentAuthorityMailReminder(reminder);
                    if (currentKey != null && !currentKey.equals(newKey) && lastReminder != null) {
                        currentKey = newKey;

                        // fetch mail
                        List<String> mailsId = Arrays.asList(lastReminder.getMailId().split(","));
                        List<Mail> mails = new ArrayList<Mail>();
                        for (String mailId : mailsId) {
                            mails.add(mailService.getMail(Integer.parseInt(mailId)));
                        }

                        mailHelper.sendCompetentAuthorityMailForReminder(
                                employeeService.getEmployee(lastReminder.getEmployeeId()),
                                getCompetentAuthority(lastReminder.getCompetentAuthorityId()), provisionToSend, mails);
                        provisionToSend = new ArrayList<Provision>();
                    }
                    currentKey = newKey;
                    lastReminder = reminder;
                    Provision provision = provisionService.getProvision(reminder.getProvisionId());
                    provision.setLastStatusReminderAcDateTime(LocalDateTime.now());
                    provisionService.addOrUpdateProvision(provision);
                    provision.setLastStatusReminderAcDateTime(reminder.getStatusDate());
                    provisionToSend.add(provisionService.getProvision(reminder.getProvisionId()));
                }
                // Send last one
                if (provisionToSend.size() > 0 && lastReminder != null
                        && reminder.equals(competentMailResult.get(competentMailResult.size() - 1))) {
                    // fetch mail
                    List<String> mailsId = Arrays.asList(lastReminder.getMailId().split(","));
                    List<Mail> mails = new ArrayList<Mail>();
                    for (String mailId : mailsId) {
                        mails.add(mailService.getMail(Integer.parseInt(mailId)));
                    }

                    mailHelper.sendCompetentAuthorityMailForReminder(
                            employeeService.getEmployee(lastReminder.getEmployeeId()),
                            getCompetentAuthority(lastReminder.getCompetentAuthorityId()), provisionToSend, mails);
                }
            }
        }
    }

    private String computeKeyForICompetentAuthorityMailReminder(ICompetentAuthorityMailReminder reminder) {
        return reminder.getEmployeeId() + reminder.getCompetentAuthorityId() + reminder.getMailId();
    }
}