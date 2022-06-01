package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.RecordType;

public interface RecordTypeService {
    public List<RecordType> getRecordTypes();

    public RecordType getRecordType(Integer id);
}
