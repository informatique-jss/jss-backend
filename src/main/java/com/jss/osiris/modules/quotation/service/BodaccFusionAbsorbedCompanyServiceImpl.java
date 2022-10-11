package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.BodaccFusionAbsorbedCompany;
import com.jss.osiris.modules.quotation.repository.BodaccFusionAbsorbedCompanyRepository;

@Service
public class BodaccFusionAbsorbedCompanyServiceImpl implements BodaccFusionAbsorbedCompanyService {

    @Autowired
    BodaccFusionAbsorbedCompanyRepository bodaccFusionAbsorbedCompanyRepository;

    @Override
    public BodaccFusionAbsorbedCompany getBodaccFusionAbsorbedCompany(Integer id) {
        Optional<BodaccFusionAbsorbedCompany> bodaccFusionAbsorbedCompany = bodaccFusionAbsorbedCompanyRepository
                .findById(id);
        if (bodaccFusionAbsorbedCompany.isPresent())
            return bodaccFusionAbsorbedCompany.get();
        return null;
    }
}
