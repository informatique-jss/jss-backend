package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.BodaccFusionMergingCompany;
import com.jss.osiris.modules.quotation.repository.BodaccFusionMergingCompanyRepository;

@Service
public class BodaccFusionMergingCompanyServiceImpl implements BodaccFusionMergingCompanyService {

    @Autowired
    BodaccFusionMergingCompanyRepository bodaccFusionMergingCompanyRepository;

    @Override
    public BodaccFusionMergingCompany getBodaccFusionMergingCompany(Integer id) {
        Optional<BodaccFusionMergingCompany> bodaccFusionMergingCompany = bodaccFusionMergingCompanyRepository
                .findById(id);
        if (!bodaccFusionMergingCompany.isEmpty())
            return bodaccFusionMergingCompany.get();
        return null;
    }
}
