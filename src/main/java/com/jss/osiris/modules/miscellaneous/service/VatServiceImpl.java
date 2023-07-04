package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.invoicing.model.Invoice;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.DepartmentVatSetting;
import com.jss.osiris.modules.miscellaneous.model.Document;
import com.jss.osiris.modules.miscellaneous.model.IVat;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.repository.VatRepository;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.IQuotation;
import com.jss.osiris.modules.tiers.model.ITiers;

@Service
public class VatServiceImpl implements VatService {

    @Autowired
    VatRepository vatRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    DepartmentVatSettingService departmentVatSettingService;

    @Autowired
    DocumentService documentService;

    @Autowired
    CityService cityService;

    @Override
    public List<Vat> getVats() {
        return IterableUtils.toList(vatRepository.findAll());
    }

    @Override
    public Vat getVat(Integer id) {
        Optional<Vat> vat = vatRepository.findById(id);
        if (vat.isPresent())
            return vat.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Vat addOrUpdateVat(
            Vat vat) {
        return vatRepository.save(vat);
    }

    @Override
    public Vat getGeographicalApplicableVatForSales(IQuotation quotation, Vat vat)
            throws OsirisException, OsirisClientMessageException {
        Country country = getCountryForIQuotation(quotation);
        if (country == null)
            throw new OsirisClientMessageException(
                    "Pays non trouvé sur le donneur d'ordre, l'affaire ou le libellé de facturation");

        City city = getCityForIQuotation(quotation);
        if (city == null)
            throw new OsirisClientMessageException(
                    "Ville non trouvée sur le donneur d'ordre, l'affaire ou le libellé de facturation");

        if (!country.getId().equals(constantService.getCountryFrance().getId())
                && !country.getId().equals(constantService.getCountryMonaco().getId()))
            return null;

        if (country.getId().equals(constantService.getCountryMonaco().getId()))
            return vat != null ? vat : constantService.getVatTwenty();

        if (city.getDepartment() == null)
            throw new OsirisClientMessageException("Département non trouvé pour le calcul de la TVA");

        DepartmentVatSetting settings = departmentVatSettingService
                .getDepartmentVatSettingByDepartment(city.getDepartment());

        if (settings == null)
            return vat != null ? vat : constantService.getVatTwenty();

        if (vat == null || vat.getId().equals(constantService.getVatTwenty().getId()))
            return settings.getIntermediateVat().getRate() > 0 ? settings.getIntermediateVat() : null;

        if (vat.getId().equals(constantService.getVatTwo().getId()))
            return settings.getReducedVat().getRate() > 0 ? settings.getReducedVat() : null;

        return vat != null ? vat : constantService.getVatTwenty();
    }

    @Override
    public Vat getGeographicalApplicableVatForPurshase(Country country, Department departement, Vat vat)
            throws OsirisException, OsirisClientMessageException {
        if (country == null)
            throw new OsirisClientMessageException(
                    "Pays non trouvé sur le donneur d'ordre, l'affaire ou le libellé de facturation");

        if (!country.getId().equals(constantService.getCountryFrance().getId())
                && !country.getId().equals(constantService.getCountryMonaco().getId()))
            return null;

        if (country.getId().equals(constantService.getCountryMonaco().getId()))
            return vat != null ? vat : constantService.getVatTwenty();

        if (departement == null)
            throw new OsirisClientMessageException("Département non trouvé pour le calcul de la TVA");

        DepartmentVatSetting settings = departmentVatSettingService.getDepartmentVatSettingByDepartment(departement);

        if (settings == null)
            return vat != null ? vat : constantService.getVatTwenty();

        if (vat == null || vat.getId().equals(constantService.getVatDeductible().getId()))
            return settings.getIntermediateVatForPurshase().getRate() > 0 ? settings.getIntermediateVatForPurshase()
                    : null;

        if (vat.getId().equals(constantService.getVatDeductibleTwo().getId()))
            return settings.getReducedVatForPurshase().getRate() > 0 ? settings.getReducedVatForPurshase() : null;

        return (vat != null ? vat : constantService.getVatDeductible());
    }

    @Override
    public Vat getGeographicalApplicableVatForPurshases(IVat vatTiers, Vat vat)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (vatTiers == null)
            throw new OsirisValidationException("provider");

        if (vatTiers.getCountry() == null)
            throw new OsirisValidationException("country");

        if (!vatTiers.getCountry().getId().equals(constantService.getCountryFrance().getId())
                && !vatTiers.getCountry().getId().equals(constantService.getCountryMonaco().getId())) {
            return getGeographicalApplicableVatForPurshase(vatTiers.getCountry(), null, null);
        }

        if (vatTiers.getCity() == null || vatTiers.getCity().getDepartment() == null
                || vatTiers.getCountry() == null) {
            throw new OsirisValidationException("city");
        }

        return getGeographicalApplicableVatForPurshase(vatTiers.getCountry(), vatTiers.getCity().getDepartment(), vat);
    }

    @Override
    public Vat getGeographicalApplicableVatForSales(Invoice invoice, Vat vat)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        Country country = getCountryForInvoice(invoice);
        if (country == null)
            throw new OsirisClientMessageException(
                    "Pays non trouvé sur le donneur d'ordre, l'affaire ou le libellé de facturation");

        City city = getCityForInvoice(invoice);
        if (city == null)
            throw new OsirisClientMessageException(
                    "Ville non trouvée sur le donneur d'ordre, l'affaire ou le libellé de facturation");

        if (!country.getId().equals(constantService.getCountryFrance().getId())
                && !country.getId().equals(constantService.getCountryMonaco().getId()))
            return null;

        if (country.getId().equals(constantService.getCountryMonaco().getId()))
            return vat != null ? vat : constantService.getVatTwenty();

        if (city.getDepartment() == null)
            throw new OsirisClientMessageException("Département non trouvé pour le calcul de la TVA");

        DepartmentVatSetting settings = departmentVatSettingService
                .getDepartmentVatSettingByDepartment(city.getDepartment());

        if (settings == null)
            return vat != null ? vat : constantService.getVatTwenty();

        if (vat == null || vat.getId().equals(constantService.getVatTwenty().getId()))
            return settings.getIntermediateVat().getRate() > 0 ? settings.getIntermediateVat() : null;

        if (vat.getId().equals(constantService.getVatTwo().getId()))
            return settings.getReducedVat().getRate() > 0 ? settings.getReducedVat() : null;

        return vat != null ? vat : constantService.getVatTwenty();
    }

    private Country getCountryForIQuotation(IQuotation quotation) throws OsirisException, OsirisClientMessageException {
        Document billingDocument = documentService.getBillingDocument(quotation.getDocuments());

        ITiers customerOrder = null;
        if (quotation.getConfrere() != null)
            customerOrder = quotation.getConfrere();
        else if (quotation.getResponsable() != null)
            customerOrder = quotation.getResponsable().getTiers();
        else
            customerOrder = quotation.getTiers();

        // No VAT abroad (France and Monaco)
        Country country = null;
        if (billingDocument == null || billingDocument.getBillingLabelType() == null
                || billingDocument.getBillingLabelType().getId()
                        .equals(constantService.getBillingLabelTypeCustomer().getId())) {
            country = customerOrder.getCountry();
        } else if (billingDocument.getBillingLabelType().getId()
                .equals(constantService.getBillingLabelTypeCodeAffaire().getId())
                && quotation.getAssoAffaireOrders() != null && quotation.getAssoAffaireOrders().size() > 0) {
            Affaire affaire = quotation.getAssoAffaireOrders().get(0).getAffaire();
            country = affaire.getCountry();
        } else {
            if (billingDocument.getBillingLabelCountry() == null)
                throw new OsirisClientMessageException(
                        "Pays non trouvé dans l'adresse indiquée dans la configuration de facturation de la commande");
            country = billingDocument.getBillingLabelCountry();
        }
        return country;
    }

    private City getCityForIQuotation(IQuotation quotation) throws OsirisException, OsirisClientMessageException {
        Document billingDocument = documentService.getBillingDocument(quotation.getDocuments());

        ITiers customerOrder = null;
        if (quotation.getConfrere() != null)
            customerOrder = quotation.getConfrere();
        else if (quotation.getResponsable() != null)
            customerOrder = quotation.getResponsable().getTiers();
        else
            customerOrder = quotation.getTiers();

        City city = null;
        if (billingDocument == null || billingDocument.getBillingLabelType() == null
                || billingDocument.getBillingLabelType().getId()
                        .equals(constantService.getBillingLabelTypeCustomer().getId())) {
            city = cityService.getCity(customerOrder.getCity().getId());
        } else if (billingDocument.getBillingLabelType().getId()
                .equals(constantService.getBillingLabelTypeCodeAffaire().getId())
                && quotation.getAssoAffaireOrders() != null && quotation.getAssoAffaireOrders().size() > 0) {
            Affaire affaire = quotation.getAssoAffaireOrders().get(0).getAffaire();
            city = affaire.getCity();
        } else {
            if (billingDocument.getBillingLabelCity() == null)
                throw new OsirisClientMessageException(
                        "Ville non trouvée dans l'adresse indiquée dans la configuration de facturation de la commande");
            city = billingDocument.getBillingLabelCity();
            if (city != null && city.getId() != null)
                city = cityService.getCity(city.getId());
        }
        return city;
    }

    private Country getCountryForInvoice(Invoice invoice) throws OsirisException, OsirisClientMessageException {
        ITiers customerOrder = null;
        if (invoice.getConfrere() != null)
            customerOrder = invoice.getConfrere();
        else if (invoice.getResponsable() != null)
            customerOrder = invoice.getResponsable().getTiers();
        else
            customerOrder = invoice.getTiers();

        // No VAT abroad (France and Monaco)
        Country country = null;
        if (invoice.getBillingLabelType() == null
                || invoice.getBillingLabelType().getId()
                        .equals(constantService.getBillingLabelTypeCustomer().getId())) {
            country = customerOrder.getCountry();
        } else if (invoice.getBillingLabelType().getId()
                .equals(constantService.getBillingLabelTypeCodeAffaire().getId())
                && invoice.getCustomerOrder() != null && invoice.getCustomerOrder().getAssoAffaireOrders() != null
                && invoice.getCustomerOrder().getAssoAffaireOrders().size() > 0) {
            Affaire affaire = invoice.getCustomerOrder().getAssoAffaireOrders().get(0).getAffaire();
            country = affaire.getCountry();
        } else {
            if (invoice.getBillingLabelCountry() == null)
                throw new OsirisClientMessageException(
                        "Pays non trouvé dans l'adresse indiquée dans la configuration de facturation de la commande");
            country = invoice.getBillingLabelCountry();
        }
        return country;
    }

    private City getCityForInvoice(Invoice invoice) throws OsirisException, OsirisClientMessageException {
        ITiers customerOrder = null;
        if (invoice.getConfrere() != null)
            customerOrder = invoice.getConfrere();
        else if (invoice.getResponsable() != null)
            customerOrder = invoice.getResponsable().getTiers();
        else
            customerOrder = invoice.getTiers();

        City city = null;
        if (invoice.getBillingLabelType() == null
                || invoice.getBillingLabelType().getId()
                        .equals(constantService.getBillingLabelTypeCustomer().getId())) {
            city = cityService.getCity(customerOrder.getCity().getId());
        } else if (invoice.getBillingLabelType().getId()
                .equals(constantService.getBillingLabelTypeCodeAffaire().getId())
                && invoice.getCustomerOrder() != null && invoice.getCustomerOrder().getAssoAffaireOrders() != null
                && invoice.getCustomerOrder().getAssoAffaireOrders().size() > 0) {
            Affaire affaire = invoice.getCustomerOrder().getAssoAffaireOrders().get(0).getAffaire();
            city = affaire.getCity();
        } else {
            if (invoice.getBillingLabelCity() == null)
                throw new OsirisClientMessageException(
                        "Ville non trouvée dans l'adresse indiquée dans la configuration de facturation de la commande");
            city = invoice.getBillingLabelCity();
            if (city != null && city.getId() != null)
                city = cityService.getCity(city.getId());
        }
        return city;
    }

}
