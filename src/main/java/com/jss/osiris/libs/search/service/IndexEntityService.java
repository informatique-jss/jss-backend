package com.jss.osiris.libs.search.service;

import com.jss.osiris.libs.search.model.IndexEntity;
import com.jss.osiris.modules.miscellaneous.model.IId;

public interface IndexEntityService {
    public void indexEntity(IId entity);

    public void deleteIndexEntity(IndexEntity indexEntity);
}
