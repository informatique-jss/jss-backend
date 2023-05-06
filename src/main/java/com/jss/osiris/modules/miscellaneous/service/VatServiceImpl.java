package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.repository.VatRepository;

@Service
public class VatServiceImpl implements VatService {

    @Autowired
    VatRepository vatRepository;

    @Autowired
    ConstantService constantService;

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

    /**
     * Using https://sumup.fr/factures/essentiels-facturation/tva-dom-tom/
     * 
     * @throws OsirisException
     * @throws OsirisClientMessageException
     */
    @Override
    public Vat getGeographicalApplicableVat(Country country, Department departement)
            throws OsirisException, OsirisClientMessageException {
        if (country == null)
            throw new OsirisClientMessageException(
                    "Pays non trouvé sur le donneur d'ordre, l'affaire ou le libellé de facturation");

        Vat vatTwenty = constantService.getVatTwenty();
        if (vatTwenty == null) {
            throw new OsirisException(null, "VAT not found for code " + constantService.getVatTwenty().getCode());
        }

        // 20% in Monaco
        if (country.getId().equals(constantService.getCountryMonaco().getId()))
            return vatTwenty;

        Vat vatEight = constantService.getVatEight();
        if (vatEight == null) {
            throw new OsirisException(null, "VAT not found for code " + constantService.getVatEight().getCode());
        }

        if (departement == null)
            throw new OsirisClientMessageException("Département non trouvé pour le calcul de la TVA");

        // 8 % for individual of DOM excepted Guyane and Mayotte (0 %), 20 % for
        // professionals
        if (departement.getId().equals(constantService.getDepartmentMartinique().getId())
                || departement.getId().equals(constantService.getDepartmentGuadeloupe().getId())
                || departement.getId().equals(constantService.getDepartmentReunion().getId())) {
            return vatEight;
        }

        // No VAT for TOM and other DOM
        if (departement.getCode().length() > 2)
            return null;

        // 20 % for all other departments
        return vatTwenty;
    }
}
