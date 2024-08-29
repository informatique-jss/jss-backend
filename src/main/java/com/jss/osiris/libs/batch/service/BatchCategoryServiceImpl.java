package com.jss.osiris.libs.batch.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.BatchCategory;
import com.jss.osiris.libs.batch.repository.BatchCategoryRepository;
import com.jss.osiris.libs.exception.OsirisException;

@Service
public class BatchCategoryServiceImpl implements BatchCategoryService {

    @Autowired
    BatchCategoryRepository batchCategoryRepository;

    @Override
    public List<BatchCategory> getBatchCategories() {
        return IterableUtils.toList(batchCategoryRepository.findAll());
    }

    @Override
    public BatchCategory getBatchCategory(Integer id) {
        Optional<BatchCategory> batchStatus = batchCategoryRepository.findById(id);
        if (batchStatus.isPresent())
            return batchStatus.get();
        return null;
    }

    @Override
    public BatchCategory getBatchCategoryByCode(String code) {
        Optional<BatchCategory> batchStatus = batchCategoryRepository.findByCode(code);
        if (batchStatus.isPresent())
            return batchStatus.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BatchCategory addOrUpdateBatchCategory(BatchCategory batchCategory) {
        return batchCategoryRepository.save(batchCategory);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategoryReferential() throws OsirisException {
        updateCategory(BatchCategory.GUICHET_UNIQUE, "Guichet unique");
        updateCategory(BatchCategory.INFOGREFFE, "Infogreffe");
        updateCategory(BatchCategory.INDEXATION, "Indexation");
        updateCategory(BatchCategory.ACCOUNTING, "Comptabilité");
        updateCategory(BatchCategory.SYSTEM, "Système");
        updateCategory(BatchCategory.MISCELLANEOUS, "Divers");
        updateCategory(BatchCategory.REMINDERS, "Relances");
        updateCategory(BatchCategory.REFERENTIALS, "Référentiels");
        updateCategory(BatchCategory.MAILS, "Envoi de mails");
    }

    private void updateCategory(String code, String label) {
        BatchCategory batchCategory = getBatchCategoryByCode(code);
        if (batchCategory == null) {
            batchCategory = new BatchCategory();
        }
        batchCategory.setLabel(label);
        batchCategory.setCode(code);
        batchCategoryRepository.save(batchCategory);
    }
}