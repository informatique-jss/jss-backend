package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.BodaccSplit;
import com.jss.osiris.modules.quotation.repository.BodaccSplitRepository;

@Service
public class BodaccSplitServiceImpl implements BodaccSplitService {

    @Autowired
    BodaccSplitRepository bodaccSplitRepository;

    @Override
    public BodaccSplit getBodaccSplit(Integer id) {
        Optional<BodaccSplit> bodaccSplit = bodaccSplitRepository.findById(id);
        if (bodaccSplit.isPresent())
            return bodaccSplit.get();
        return null;
    }
}
