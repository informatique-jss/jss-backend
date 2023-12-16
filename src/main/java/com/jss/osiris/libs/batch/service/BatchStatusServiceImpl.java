package com.jss.osiris.libs.batch.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.BatchStatus;
import com.jss.osiris.libs.batch.repository.BatchStatusRepository;
import com.jss.osiris.libs.exception.OsirisException;

@Service
public class BatchStatusServiceImpl implements BatchStatusService {

    @Autowired
    BatchStatusRepository batchStatusRepository;

    @Override
    public List<BatchStatus> getBatchStatus() {
        return IterableUtils.toList(batchStatusRepository.findAll());
    }

    @Override
    public BatchStatus getBatchStatus(Integer id) {
        Optional<BatchStatus> batchStatus = batchStatusRepository.findById(id);
        if (batchStatus.isPresent())
            return batchStatus.get();
        return null;
    }

    @Override
    public BatchStatus getBatchStatusByCode(String code) {
        return batchStatusRepository.findByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusReferential() throws OsirisException {
        updateStatus(BatchStatus.WAITING, "En attente");
        updateStatus(BatchStatus.RUNNING, "En cours");
        updateStatus(BatchStatus.SUCCESS, "Succès");
        updateStatus(BatchStatus.ERROR, "En erreur");
        updateStatus(BatchStatus.ACKNOWLEDGE, "Acquité");

    }

    private void updateStatus(String code, String label) {
        BatchStatus batchStatus = getBatchStatusByCode(code);
        if (batchStatus == null)
            batchStatus = new BatchStatus();
        batchStatus.setCode(code);
        batchStatus.setLabel(label);
        batchStatusRepository.save(batchStatus);
    }

}
