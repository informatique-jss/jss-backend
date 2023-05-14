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
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.DepartmentVatSetting;
import com.jss.osiris.modules.miscellaneous.model.IVat;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.repository.VatRepository;

@Service
public class VatServiceImpl implements VatService {

    @Autowired
    VatRepository vatRepository;

    @Autowired
    ConstantService constantService;

    @Autowired
    DepartmentVatSettingService departmentVatSettingService;

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
    public Vat getGeographicalApplicableVatForSales(Country country, Department departement, Vat vat)
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

        if (vat == null || vat.getId().equals(constantService.getVatTwenty().getId()))
            return settings.getIntermediateVat().getRate() > 0 ? settings.getIntermediateVat() : null;

        if (vat.getId().equals(constantService.getVatTwo().getId()))
            return settings.getReducedVat().getRate() > 0 ? settings.getReducedVat() : null;

        return constantService.getVatTwenty();
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

        if (vat.getId().equals(constantService.getVatTwo().getId()))
            return settings.getReducedVatForPurshase().getRate() > 0 ? settings.getReducedVatForPurshase() : null;

        return constantService.getVatDeductible();
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
    public Vat getGeographicalApplicableVatForSales(IVat vatTiers, Vat vat)
            throws OsirisValidationException, OsirisException, OsirisClientMessageException {
        if (vatTiers == null)
            throw new OsirisValidationException("provider");

        if (vatTiers.getCountry() == null)
            throw new OsirisValidationException("country");

        if (!vatTiers.getCountry().getId().equals(constantService.getCountryFrance().getId())
                && !vatTiers.getCountry().getId().equals(constantService.getCountryMonaco().getId())) {
            return getGeographicalApplicableVatForSales(vatTiers.getCountry(), null, null);
        }

        if (vatTiers.getCity() == null || vatTiers.getCity().getDepartment() == null
                || vatTiers.getCountry() == null) {
            throw new OsirisValidationException("city");
        }

        return getGeographicalApplicableVatForSales(vatTiers.getCountry(), vatTiers.getCity().getDepartment(), vat);
    }

}
