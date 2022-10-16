package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.RecordType;
import com.jss.osiris.modules.quotation.repository.RecordTypeRepository;

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
        if (recordType.isPresent())
            return recordType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RecordType addOrUpdateRecordType(RecordType recordType) {
        return recordTypeRepository.save(recordType);
    }
}
