package com.jss.osiris.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
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

    @Autowired
    ConstantService constantService;

    @Override
    @Transactional
    public Tiers getTiersFromUser(Integer id) {
        return getTiers(id);
    }

    @Override
    @Transactional
    public Tiers getTiers(Integer id) {
        Optional<Tiers> tiers = tiersRepository.findById(id);
        if (tiers.isPresent()) {
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
    @Transactional(rollbackFor = Exception.class)
    public Tiers addOrUpdateTiers(Tiers tiers) throws OsirisException {
        if (tiers == null)
            throw new OsirisException(null, "Provided tiers is null");

        // If mails already exists, get their ids
        if (tiers != null && tiers.getMails() != null && tiers.getMails().size() > 0)
            mailService.populateMailIds(tiers.getMails());

        // If phones already exists, get their ids
        if (tiers != null && tiers.getPhones() != null && tiers.getPhones().size() > 0) {
            phoneService.populatePhoneIds(tiers.getPhones());
        }

        // If document mails already exists, get their ids
        if (tiers.getDocuments() != null && tiers.getDocuments().size() > 0) {
            for (Document document : tiers.getDocuments()) {
                document.setTiers(tiers);
                if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
                    mailService.populateMailIds(document.getMailsAffaire());
                if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
                    mailService.populateMailIds(document.getMailsClient());
            }
        }

        // Generate accounting accounts
        if (tiers.getId() == null
                || tiers.getAccountingAccountCustomer() == null && tiers.getAccountingAccountProvider() == null
                        && tiers.getAccountingAccountDeposit() == null) {
            String label = "";
            if (tiers.getIsIndividual()) {
                label = tiers.getFirstname() + " " + tiers.getLastname();
            } else {
                label = tiers.getDenomination();
            }
            AccountingAccountTrouple accountingAccountCouple = accountingAccountService
                    .generateAccountingAccountsForEntity(label, false);
            tiers.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
            tiers.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
            tiers.setAccountingAccountDeposit(accountingAccountCouple.getAccountingAccountDeposit());
        }

        if (tiers.getResponsables() != null && tiers.getResponsables().size() > 0)
            for (Responsable responsable : tiers.getResponsables()) {
                responsable.setTiers(tiers);
                if (responsable.getDocuments() != null)
                    for (Document document : responsable.getDocuments())
                        document.setResponsable(responsable);
            }

        if (tiers.getResponsables() != null && tiers.getResponsables().size() > 0) {
            for (Responsable responsable : tiers.getResponsables()) {
                // Set default customer order assignation to sales employee if not set
                if (responsable.getDefaultCustomerOrderEmployee() == null)
                    responsable.setDefaultCustomerOrderEmployee(responsable.getSalesEmployee());

                // If mails already exists, get their ids
                if (responsable.getMails() != null && responsable.getMails().size() > 0)
                    mailService.populateMailIds(responsable.getMails());

                // If phones already exists, get their ids
                if (responsable.getPhones() != null && responsable.getPhones().size() > 0) {
                    phoneService.populatePhoneIds(responsable.getPhones());
                }

                if (responsable.getDocuments() != null && responsable.getDocuments().size() > 0) {
                    for (Document document : responsable.getDocuments()) {
                        document.setResponsable(responsable);
                        if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
                            mailService.populateMailIds(document.getMailsAffaire());
                        if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
                            mailService.populateMailIds(document.getMailsClient());
                    }
                }
            }
        }

        // Set default customer order assignation to sales employee if not set
        if (tiers.getDefaultCustomerOrderEmployee() == null)
            tiers.setDefaultCustomerOrderEmployee(tiers.getSalesEmployee());

        tiers = tiersRepository.save(tiers);

        indexEntityService.indexEntity(tiers, tiers.getId());
        if (tiers.getResponsables() != null)
            for (Responsable responsable : tiers.getResponsables()) {
                if (responsable.getLoginWeb() == null)
                    responsable.setLoginWeb(responsable.getId() + "");
                indexEntityService.indexEntity(responsable, responsable.getId());
            }

        // Set default customer order assignation to sales employee if not set
        if (tiers.getDefaultCustomerOrderEmployee() == null)
            tiers.setDefaultCustomerOrderEmployee(tiers.getSalesEmployee());

        if (tiers.getAccountingAccountCustomer() != null || tiers.getAccountingAccountDeposit() != null
                || tiers.getAccountingAccountProvider() != null) {
            tiers.getAccountingAccountCustomer()
                    .setLabel("Client - " + (tiers.getDenomination() != null ? tiers.getDenomination() : ""));
            tiers.getAccountingAccountDeposit()
                    .setLabel("Acompte - " + (tiers.getDenomination() != null ? tiers.getDenomination() : ""));
            tiers.getAccountingAccountProvider()
                    .setLabel("Fournisseur - " + (tiers.getDenomination() != null ? tiers.getDenomination() : ""));
        }
        tiers = tiersRepository.save(tiers);

        return tiers;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tiers getTiersByIdResponsable(Integer idResponsable) {
        Responsable responsable = responsableService.getResponsable(idResponsable);
        if (responsable != null)
            return this.getTiers(responsable.getTiers().getId());
        return null;
    }

    @Override
    public void reindexTiers() {
        List<Tiers> tiers = IterableUtils.toList(tiersRepository.findAll());
        if (tiers != null)
            for (Tiers tier : tiers)
                indexEntityService.indexEntity(tier, tier.getId());
    }

    @Override
    public List<Tiers> findAllTiersTypeClient() throws OsirisException {
        return tiersRepository.findByTiersType(constantService.getTiersTypeClient());
    }

    @Override
    public List<Tiers> findAllTiersForBillingClosureReceiptSend() throws OsirisException {
        return tiersRepository.findAllTiersForBillingClosureReceiptSend(constantService.getInvoiceStatusSend().getId(),
                constantService.getTiersTypeClient().getId());
    }

    @Override
    public List<Tiers> getTiers() {
        return IterableUtils.toList(tiersRepository.findAll());
    }

}
