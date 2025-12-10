package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmCategory;

public interface KpiCrmCategoryService {

    void updateCategoryReferential() throws OsirisException;

    List<KpiCrmCategory> getKpiCrmCategories();

    KpiCrmCategory getKpiCrmCategory(Integer id);

    KpiCrmCategory addOrUpdateKpiCrmCategory(KpiCrmCategory kKpiCrmCategory);

    KpiCrmCategory getKpiCrmCategoryByCode(String code);

}
