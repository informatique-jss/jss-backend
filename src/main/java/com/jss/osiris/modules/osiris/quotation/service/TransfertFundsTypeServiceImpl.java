package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.TransfertFundsType;
import com.jss.osiris.modules.osiris.quotation.repository.TransfertFundsTypeRepository;

@Service
public class TransfertFundsTypeServiceImpl implements TransfertFundsTypeService {

    @Autowired
    TransfertFundsTypeRepository transfertFundsTypeRepository;

    @Override
    public List<TransfertFundsType> getTransfertFundsTypes() {
        return IterableUtils.toList(transfertFundsTypeRepository.findAll());
    }

    @Override
    public TransfertFundsType getTransfertFundsType(Integer id) {
        Optional<TransfertFundsType> transfertFundsType = transfertFundsTypeRepository.findById(id);
        if (transfertFundsType.isPresent())
            return transfertFundsType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransfertFundsType addOrUpdateTransfertFundsType(TransfertFundsType transfertFundsType) {
        return transfertFundsTypeRepository.save(transfertFundsType);
    }
}
