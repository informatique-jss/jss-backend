package com.jss.osiris.libs.batch.service;

import java.util.List;

import com.jss.osiris.libs.batch.model.BatchCategory;
import com.jss.osiris.libs.exception.OsirisException;

public interface BatchCategoryService {
    public List<BatchCategory> getBatchCategories();

    public BatchCategory getBatchCategory(Integer id);

    public BatchCategory addOrUpdateBatchCategory(BatchCategory vat);

    public BatchCategory getBatchCategoryByCode(String code);

    public void updateCategoryReferential() throws OsirisException;
}
