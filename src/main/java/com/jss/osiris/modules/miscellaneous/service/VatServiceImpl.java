package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.Country;
import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.miscellaneous.model.Vat;
import com.jss.osiris.modules.miscellaneous.repository.VatRepository;

@Service
public class VatServiceImpl implements VatService {

    private static final Logger logger = LoggerFactory.getLogger(VatServiceImpl.class);

    @Autowired
    VatRepository vatRepository;

    @Value("${accounting.vat.code.twenty}")
    private String vatTwentyCode;

    @Value("${accounting.vat.code.eight}")
    private String vatEightCode;

    @Value("${miscellaneous.country.code.france}")
    private String franceCountryCode;

    @Value("${miscellaneous.country.code.monaco}")
    private String monacoCountryCode;

    @Value("${miscellaneous.department.code.martinique}")
    private String martiniqueDepartmentCode;

    @Value("${miscellaneous.department.code.guadeloupe}")
    private String guadeloupeDepartmentCode;

    @Value("${miscellaneous.department.code.reunion}")
    private String reunionDepartmentCode;

    @Override
    @Cacheable(value = "vatList", key = "#root.methodName")
    public List<Vat> getVats() {
        return IterableUtils.toList(vatRepository.findAll());
    }

    @Override
    @Cacheable(value = "vat", key = "#id")
    public Vat getVat(Integer id) {
        Optional<Vat> vat = vatRepository.findById(id);
        if (vat.isPresent())
            return vat.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "vatList", allEntries = true),
            @CacheEvict(value = "vat", key = "#vat.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public Vat addOrUpdateVat(
            Vat vat) {
        return vatRepository.save(vat);
    }

    @Override
    public Vat getVatByCode(String code) {
        return vatRepository.findByCode(code);
    }

    /**
     * Using https://sumup.fr/factures/essentiels-facturation/tva-dom-tom/
     */
    @Override
    public Vat getGeographicalApplicableVat(Country country, Department departement, boolean isIndividual)
            throws Exception {
        if (country == null)
            throw new Exception("Country not provided");

        // No VAT abroad (France and Monaco)
        if (!country.getCode().equals(franceCountryCode) && !country.getCode().equals(monacoCountryCode))
            return null;

        Vat vatTwenty = getVatByCode(vatTwentyCode);
        if (vatTwenty == null) {
            logger.error("VAT not found for code " + vatTwentyCode);
            throw new Exception("VAT not found for code " + vatTwentyCode);
        }

        // 20% in Monaco
        if (country.getCode().equals(monacoCountryCode))
            return vatTwenty;

        Vat vatEight = getVatByCode(vatEightCode);
        if (vatEight == null) {
            logger.error("VAT not found for code " + vatEightCode);
            throw new Exception("VAT not found for code " + vatEightCode);
        }

        if (departement == null)
            throw new Exception("Department not provided");

        // 8 % for individual of DOM excepted Guyane and Mayotte (0 %), 20 % for
        // professionals
        if (departement.getCode().equals(martiniqueDepartmentCode)
                || departement.getCode().equals(guadeloupeDepartmentCode)
                || departement.getCode().equals(reunionDepartmentCode)) {
            if (isIndividual)
                return vatEight;
            return vatTwenty;
        }

        // No VAT for TOM and other DOM
        if (departement.getCode().length() > 2)
            return null;

        // 20 % for all other departments
        return vatTwenty;
    }
}
