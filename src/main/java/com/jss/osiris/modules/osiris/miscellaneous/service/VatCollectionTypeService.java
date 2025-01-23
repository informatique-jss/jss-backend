package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.VatCollectionType;

public interface VatCollectionTypeService {
    public List<VatCollectionType> getVatCollectionTypes();

    public VatCollectionType getVatCollectionType(Integer id);

    public VatCollectionType addOrUpdateVatCollectionType(VatCollectionType vatCollectionType);
}
