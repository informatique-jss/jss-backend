package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.RecordType;

public interface RecordTypeService {
    public List<RecordType> getRecordTypes();

    public RecordType getRecordType(Integer id);

    public RecordType addOrUpdateRecordType(RecordType recordType);
}
