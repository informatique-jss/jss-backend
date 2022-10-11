package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.BodaccSplitBeneficiary;
import com.jss.osiris.modules.quotation.repository.BodaccSplitBeneficiaryRepository;

@Service
public class BodaccSplitBeneficiaryServiceImpl implements BodaccSplitBeneficiaryService {

    @Autowired
    BodaccSplitBeneficiaryRepository bodaccSplitBeneficiaryRepository;

    @Override
    public BodaccSplitBeneficiary getBodaccSplitBeneficiary(Integer id) {
        Optional<BodaccSplitBeneficiary> bodaccSplitBeneficiary = bodaccSplitBeneficiaryRepository.findById(id);
        if (bodaccSplitBeneficiary.isPresent())
            return bodaccSplitBeneficiary.get();
        return null;
    }
}
