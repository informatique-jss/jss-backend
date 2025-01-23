package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.RecordType;

public interface RecordTypeService {
    public List<RecordType> getRecordTypes();

    public RecordType getRecordType(Integer id);

    public RecordType addOrUpdateRecordType(RecordType recordType);
}
