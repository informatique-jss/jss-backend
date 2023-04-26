package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.AggregateStatus;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.repository.FormaliteStatusRepository;

@Service
public class FormaliteStatusServiceImpl implements FormaliteStatusService {

    @Autowired
    FormaliteStatusRepository formaliteStatusRepository;

    @Override
    public List<FormaliteStatus> getFormaliteStatus() {
        return IterableUtils.toList(formaliteStatusRepository.findAll());
    }

    @Override
    public FormaliteStatus getFormaliteStatus(Integer id) {
        Optional<FormaliteStatus> formaliteStatus = formaliteStatusRepository.findById(id);
        if (formaliteStatus.isPresent())
            return formaliteStatus.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FormaliteStatus addOrUpdateFormaliteStatus(
            FormaliteStatus formaliteStatus) {
        return formaliteStatusRepository.save(formaliteStatus);
    }

    @Override
    public FormaliteStatus getFormaliteStatusByCode(String code) {
        return formaliteStatusRepository.findByCode(code);
    }

    @Override
    public void updateStatusReferential() throws OsirisException {
        updateStatus(FormaliteStatus.FORMALITE_NEW, "Nouveau", "auto_awesome", true, false,
                AggregateStatus.AGGREGATE_STATUS_NEW);
        updateStatus(FormaliteStatus.FORMALITE_IN_PROGRESS, "En cours", "autorenew", false, false,
                AggregateStatus.AGGREGATE_STATUS_IN_PROGRESS);
        updateStatus(FormaliteStatus.FORMALITE_WAITING_DOCUMENT, "En attente de documents",
                "hourglass_top", false,
                false,
                AggregateStatus.AGGREGATE_STATUS_WAITING);
        updateStatus(FormaliteStatus.FORMALITE_WAITING_DOCUMENT_AUTHORITY,
                "En attente de l'autorité compétente", "pending",
                false, false,
                AggregateStatus.AGGREGATE_STATUS_WAITING);
        updateStatus(FormaliteStatus.FORMALITE_DONE, "Terminé", "check_small", false, true,
                AggregateStatus.AGGREGATE_STATUS_DONE);

        setSuccessor(FormaliteStatus.FORMALITE_NEW, FormaliteStatus.FORMALITE_IN_PROGRESS);
        setSuccessor(FormaliteStatus.FORMALITE_IN_PROGRESS,
                FormaliteStatus.FORMALITE_WAITING_DOCUMENT);
        setSuccessor(FormaliteStatus.FORMALITE_IN_PROGRESS,
                FormaliteStatus.FORMALITE_WAITING_DOCUMENT_AUTHORITY);
        setSuccessor(FormaliteStatus.FORMALITE_IN_PROGRESS, FormaliteStatus.FORMALITE_DONE);
        setSuccessor(FormaliteStatus.FORMALITE_WAITING_DOCUMENT,
                FormaliteStatus.FORMALITE_DONE);
        setSuccessor(FormaliteStatus.FORMALITE_WAITING_DOCUMENT,
                FormaliteStatus.FORMALITE_WAITING_DOCUMENT_AUTHORITY);
        setSuccessor(FormaliteStatus.FORMALITE_WAITING_DOCUMENT_AUTHORITY,
                FormaliteStatus.FORMALITE_DONE);

        setPredecessor(FormaliteStatus.FORMALITE_WAITING_DOCUMENT_AUTHORITY,
                FormaliteStatus.FORMALITE_IN_PROGRESS);
        setPredecessor(FormaliteStatus.FORMALITE_WAITING_DOCUMENT,
                FormaliteStatus.FORMALITE_IN_PROGRESS);
        setPredecessor(FormaliteStatus.FORMALITE_DONE,
                FormaliteStatus.FORMALITE_IN_PROGRESS);

    }

    protected void updateStatus(String code, String label, String icon, boolean isOpenState, boolean isCloseState,
            String aggregateLabel) {
        FormaliteStatus formaliteStatus = getFormaliteStatusByCode(code);
        if (getFormaliteStatusByCode(code) == null)
            formaliteStatus = new FormaliteStatus();
        formaliteStatus.setPredecessors(null);
        formaliteStatus.setSuccessors(null);
        formaliteStatus.setCode(code);
        formaliteStatus.setLabel(label);
        formaliteStatus.setIcon(icon);
        formaliteStatus.setIsCloseState(isCloseState);
        formaliteStatus.setIsOpenState(isOpenState);
        formaliteStatus.setAggregateStatus(aggregateLabel);
        addOrUpdateFormaliteStatus(formaliteStatus);
    }

    protected void setSuccessor(String code, String code2) throws OsirisException {
        FormaliteStatus sourceStatus = getFormaliteStatusByCode(code);
        FormaliteStatus targetStatus = getFormaliteStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Simple provision " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getSuccessors() == null)
            sourceStatus.setSuccessors(new ArrayList<FormaliteStatus>());

        for (FormaliteStatus status : sourceStatus.getSuccessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getSuccessors().add(targetStatus);
        addOrUpdateFormaliteStatus(sourceStatus);
    }

    protected void setPredecessor(String code, String code2) throws OsirisException {
        FormaliteStatus sourceStatus = getFormaliteStatusByCode(code);
        FormaliteStatus targetStatus = getFormaliteStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Simple provision status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<FormaliteStatus>());

        for (FormaliteStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateFormaliteStatus(sourceStatus);
    }
}
