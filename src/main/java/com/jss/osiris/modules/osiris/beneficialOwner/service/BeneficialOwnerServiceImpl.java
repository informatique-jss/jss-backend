package com.jss.osiris.modules.osiris.beneficialOwner.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.beneficialOwner.model.BeneficialOwner;
import com.jss.osiris.modules.osiris.beneficialOwner.repository.BeneficialOwnerRepository;
import com.jss.osiris.modules.osiris.quotation.model.Formalite;

@Service
public class BeneficialOwnerServiceImpl implements BeneficialOwnerService {
    @Autowired
    BeneficialOwnerRepository beneficialOwnerRepository;

    @Override
    public BeneficialOwner addOrUpdateBeneficialOwner(BeneficialOwner beneficialOwner) {
        return beneficialOwnerRepository.save(beneficialOwner);
    }

    @Override
    public BeneficialOwner getBeneficialOwner(Integer id) {
        Optional<BeneficialOwner> beneficialOwner = beneficialOwnerRepository.findById(id);

        if (beneficialOwner.isPresent())
            return beneficialOwner.get();
        return null;
    }

    @Override
    public List<BeneficialOwner> getBeneficialOwnersByFormalite(Formalite formalite) {
        return beneficialOwnerRepository.findBeneficialOwnersByFormalite(formalite);
    }

    @Override
    public Boolean deleteBeneficialOwner(BeneficialOwner beneficialOwner) {
        beneficialOwnerRepository.delete(beneficialOwner);
        return true;
    }

}
