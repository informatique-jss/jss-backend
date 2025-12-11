package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.CompanySize;

public interface CompanySizeService {
    public List<CompanySize> getCompanySizes();

    public CompanySize getCompanySize(Integer id);

    public CompanySize addOrUpdateCompanySize(CompanySize companySize);
}
