package com.jss.osiris.modules.tiers.service;

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
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.CustomerMailService;
import com.jss.osiris.libs.mail.model.CustomerMail;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.libs.search.service.SearchService;
import com.jss.osiris.modules.accounting.model.AccountingAccountTrouple;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.invoicing.model.InvoiceSearch;
import com.jss.osiris.modules.invoicing.model.InvoiceSearchResult;
import com.jss.osiris.modules.invoicing.service.InvoiceService;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.quotation.model.OrderingSearch;
import com.jss.osiris.modules.quotation.model.OrderingSearchResult;
import com.jss.osiris.modules.quotation.model.QuotationSearch;
import com.jss.osiris.modules.quotation.model.QuotationSearchResult;
import com.jss.osiris.modules.quotation.service.CustomerOrderService;
import com.jss.osiris.modules.quotation.service.QuotationService;
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

    @Autowired
    CustomerMailService customerMailService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    QuotationService quotationService;

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
            return tiersInstance;
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tiers addOrUpdateTiers(Tiers tiers) throws OsirisException, OsirisDuplicateException {
        if (tiers == null)
            throw new OsirisException(null, "Provided tiers is null");

        // Find duplicate Tiers
        if (tiers.getId() == null) {
            List<Tiers> tiersDuplicates = new ArrayList<Tiers>();
            if (tiers.getIsIndividual() != null && tiers.getIsIndividual() == true)
                tiersDuplicates = tiersRepository.findByPostalCodeAndName(tiers.getPostalCode(), tiers.getFirstname(),
                        tiers.getLastname());
            else
                tiersDuplicates = tiersRepository.findByPostalCodeAndDenomination(tiers.getPostalCode(),
                        tiers.getDenomination());

            if (tiersDuplicates.size() > 0)
                throw new OsirisDuplicateException(tiersDuplicates.stream().map(Tiers::getId).toList());
        }

        // Find duplicate Responsable
        if (tiers.getResponsables() != null && tiers.getResponsables().size() > 0) {
            for (Responsable responsable : tiers.getResponsables()) {
                if (responsable.getId() == null) {
                    List<Responsable> responsablesDuplicates = new ArrayList<Responsable>();

                    for (Responsable responsableCheck : tiers.getResponsables()) {
                        if (responsableCheck.getId() != null && responsableCheck.getFirstname() != null
                                && responsableCheck.getLastname() != null) {
                            if ((StringUtils.stripAccents(responsable.getFirstname().trim()).toUpperCase()
                                    + StringUtils.stripAccents(responsable.getLastname().trim()))
                                    .toUpperCase()
                                    .equals(StringUtils.stripAccents(responsableCheck.getFirstname().trim())
                                            .toUpperCase()
                                            + StringUtils.stripAccents(responsableCheck.getLastname().trim())
                                                    .toUpperCase()))
                                responsablesDuplicates.add(responsableCheck);
                        }
                    }

                    if (responsablesDuplicates.size() > 0)
                        throw new OsirisDuplicateException(
                                responsablesDuplicates.stream().map(Responsable::getId).toList());
                }
            }
        }

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

        String tiersLabel = "";
        if (tiers.getIsIndividual()) {
            tiersLabel = tiers.getFirstname() + " " + tiers.getLastname();
        } else {
            tiersLabel = tiers.getDenomination();
        }

        // Generate accounting accounts
        if (tiers.getId() == null
                || tiers.getAccountingAccountCustomer() == null && tiers.getAccountingAccountProvider() == null
                        && tiers.getAccountingAccountDeposit() == null) {
            AccountingAccountTrouple accountingAccountCouple = accountingAccountService
                    .generateAccountingAccountsForEntity(tiersLabel, false);
            tiers.setAccountingAccountCustomer(accountingAccountCouple.getAccountingAccountCustomer());
            tiers.setAccountingAccountProvider(accountingAccountCouple.getAccountingAccountProvider());
            tiers.setAccountingAccountDeposit(accountingAccountCouple.getAccountingAccountDeposit());
        } else {
            accountingAccountService.updateAccountingAccountLabel(tiers.getAccountingAccountCustomer(), tiersLabel);
            accountingAccountService.updateAccountingAccountLabel(tiers.getAccountingAccountDeposit(), tiersLabel);
            accountingAccountService.updateAccountingAccountLabel(tiers.getAccountingAccountProvider(), tiersLabel);
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

        tiers = tiersRepository.save(tiers);

        indexEntityService.indexEntity(tiers);
        if (tiers.getResponsables() != null)
            for (Responsable responsable : tiers.getResponsables()) {
                if (responsable.getLoginWeb() == null)
                    responsable.setLoginWeb(responsable.getId() + "");
                indexEntityService.indexEntity(responsable);
            }

        // Set default customer order assignation to sales employee if not set
        if (tiers.getDefaultCustomerOrderEmployee() == null)
            tiers.setDefaultCustomerOrderEmployee(tiers.getSalesEmployee());

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
    @Transactional(rollbackFor = Exception.class)
    public void reindexTiers() {
        List<Tiers> tiers = IterableUtils.toList(tiersRepository.findAll());
        if (tiers != null)
            for (Tiers tier : tiers)
                indexEntityService.indexEntity(tier);
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteTiers(Tiers tiers)
            throws OsirisClientMessageException, OsirisException, OsirisDuplicateException {
        tiers = getTiers(tiers.getId());
        if (tiers.getAttachments() != null && tiers.getAttachments().size() > 0)
            throw new OsirisClientMessageException("Impossible de supprimer le tiers, des documents existent");

        if (tiers.getTiersFollowups() != null && tiers.getTiersFollowups().size() > 0)
            throw new OsirisClientMessageException("Impossible de supprimer le tiers, des suivis existent");

        List<CustomerMail> mails = customerMailService.getMailsByTiers(tiers);
        if (mails != null && mails.size() > 0)
            throw new OsirisClientMessageException("Impossible de supprimer le tiers, des mails existent");

        OrderingSearch orderingSearch = new OrderingSearch();
        orderingSearch.setCustomerOrders(Arrays.asList(tiers));
        List<OrderingSearchResult> customerOrder = customerOrderService.searchOrders(orderingSearch);
        if (customerOrder != null && customerOrder.size() > 0)
            throw new OsirisClientMessageException("Impossible de supprimer le tiers, des commandes existent");

        QuotationSearch quotationSearch = new QuotationSearch();
        quotationSearch.setCustomerOrders(Arrays.asList(tiers));
        List<QuotationSearchResult> quotations = quotationService.searchQuotations(quotationSearch);
        if (quotations != null && quotations.size() > 0)
            throw new OsirisClientMessageException("Impossible de supprimer le tiers, des devis existent");

        InvoiceSearch invoiceSearch = new InvoiceSearch();
        invoiceSearch.setCustomerOrders(Arrays.asList(tiers));
        List<InvoiceSearchResult> invoices = invoiceService.searchInvoices(invoiceSearch);
        if (invoices != null && invoices.size() > 0)
            throw new OsirisClientMessageException("Impossible de supprimer le tiers, des factures existent");

        if (tiers.getResponsables() != null && tiers.getResponsables().size() > 0) {
            for (Responsable responsable : tiers.getResponsables()) {
                if (responsable.getAttachments() != null && responsable.getAttachments().size() > 0)
                    throw new OsirisClientMessageException("Impossible de supprimer le tiers, des documents existent");

                if (responsable.getTiersFollowups() != null && responsable.getTiersFollowups().size() > 0)
                    throw new OsirisClientMessageException("Impossible de supprimer le tiers, des suivis existent");

                mails = customerMailService.getMailsByResponsable(responsable);
                if (mails != null && mails.size() > 0)
                    throw new OsirisClientMessageException("Impossible de supprimer le tiers, des mails existent");

                orderingSearch = new OrderingSearch();
                Tiers dummyTiers = new Tiers();
                dummyTiers.setId(responsable.getId());
                orderingSearch.setCustomerOrders(Arrays.asList(dummyTiers));
                customerOrder = customerOrderService.searchOrders(orderingSearch);
                if (customerOrder != null && customerOrder.size() > 0)
                    throw new OsirisClientMessageException("Impossible de supprimer le tiers, des commandes existent");

                quotationSearch = new QuotationSearch();
                quotationSearch.setCustomerOrders(Arrays.asList(dummyTiers));
                quotations = quotationService.searchQuotations(quotationSearch);
                if (quotations != null && quotations.size() > 0)
                    throw new OsirisClientMessageException("Impossible de supprimer le tiers, des devis existent");

                invoiceSearch = new InvoiceSearch();
                invoiceSearch.setCustomerOrders(Arrays.asList(dummyTiers));
                invoices = invoiceService.searchInvoices(invoiceSearch);

                responsable.setMails(null);
                responsable.setPhones(null);
            }
        }

        tiers.setMails(null);
        tiers.setPhones(null);
        tiers.setCompetitors(null);
        tiers.setSpecialOffers(null);

        addOrUpdateTiers(tiers);

        tiersRepository.delete(tiers);
        return true;
    }

}
