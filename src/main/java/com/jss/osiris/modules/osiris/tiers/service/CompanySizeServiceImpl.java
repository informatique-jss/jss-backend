package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.tiers.model.CompanySize;
import com.jss.osiris.modules.osiris.tiers.repository.CompanySizeRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanySizeServiceImpl implements CompanySizeService {

    @Autowired
    CompanySizeRepository companySizeRepository;

    @Override
    public List<CompanySize> getCompanySizes() {
        return IterableUtils.toList(companySizeRepository.findAll());
    }

    @Override
    public CompanySize getCompanySize(Integer id) {
        Optional<CompanySize> companySize = companySizeRepository.findById(id);
        if (companySize.isPresent())
            return companySize.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanySize addOrUpdateCompanySize(
            CompanySize companySize) {
        return companySizeRepository.save(companySize);
    }
}
