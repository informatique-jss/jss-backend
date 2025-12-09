package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.crm.model.KpiCrmCategory;
import com.jss.osiris.modules.osiris.crm.repository.KpiCrmCategoryRepository;

@Service
public class KpiCrmCategoryServiceImpl implements KpiCrmCategoryService {

    @Autowired
    KpiCrmCategoryRepository kpiCrmCategoryRepository;

    @Override
    public List<KpiCrmCategory> getKpiCrmCategories() {
        return kpiCrmCategoryRepository.findByOrderByLabel();
    }

    @Override
    public KpiCrmCategory getKpiCrmCategory(Integer id) {
        Optional<KpiCrmCategory> kKpiCrmCategory = kpiCrmCategoryRepository.findById(id);
        if (kKpiCrmCategory.isPresent())
            return kKpiCrmCategory.get();
        return null;
    }

    @Override
    public KpiCrmCategory addOrUpdateKpiCrmCategory(KpiCrmCategory kKpiCrmCategory) {
        return kpiCrmCategoryRepository.save(kKpiCrmCategory);
    }

    @Override
    public KpiCrmCategory getKpiCrmCategoryByCode(String code) {
        return kpiCrmCategoryRepository.findByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryReferential() throws OsirisException {
        updateStatus(KpiCrmCategory.QUOTATION, "Devis");
        updateStatus(KpiCrmCategory.ACCOUNTING, "Comptabiltié");
        updateStatus(KpiCrmCategory.CUSTOMER_ORDER, "Commandes");
        updateStatus(KpiCrmCategory.TURNOVER, "CA");
        updateStatus(KpiCrmCategory.WEBSITE, "Site");
    }

    protected void updateStatus(String code, String label) {
        KpiCrmCategory KpiCrmCategory = getKpiCrmCategoryByCode(code);
        if (getKpiCrmCategoryByCode(code) == null)
            KpiCrmCategory = new KpiCrmCategory();
        KpiCrmCategory.setCode(code);
        KpiCrmCategory.setLabel(label);

        addOrUpdateKpiCrmCategory(KpiCrmCategory);
    }
}
