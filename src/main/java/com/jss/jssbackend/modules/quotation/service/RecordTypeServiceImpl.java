package com.jss.jssbackend.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.quotation.model.RecordType;
import com.jss.jssbackend.modules.quotation.repository.RecordTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RecordTypeServiceImpl implements RecordTypeService {

    @Autowired
    RecordTypeRepository recordTypeRepository;

    @Override
    public List<RecordType> getRecordTypes() {
        return IterableUtils.toList(recordTypeRepository.findAll());
    }

    @Override
    public RecordType getRecordType(Integer id) {
        Optional<RecordType> recordType = recordTypeRepository.findById(id);
        if (!recordType.isEmpty())
            return recordType.get();
        return null;
    }
}
