package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.BodaccSplitCompany;
import com.jss.osiris.modules.quotation.repository.BodaccSplitCompanyRepository;

@Service
public class BodaccSplitCompanyServiceImpl implements BodaccSplitCompanyService {

    @Autowired
    BodaccSplitCompanyRepository bodaccSplitCompanyRepository;

    @Override
    public BodaccSplitCompany getBodaccSplitCompany(Integer id) {
        Optional<BodaccSplitCompany> bodaccSplitCompany = bodaccSplitCompanyRepository.findById(id);
        if (!bodaccSplitCompany.isEmpty())
            return bodaccSplitCompany.get();
        return null;
    }
}
