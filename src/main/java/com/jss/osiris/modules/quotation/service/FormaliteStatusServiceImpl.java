package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.FormaliteStatus;
import com.jss.osiris.modules.quotation.model.ProvisionBoardResult;
import com.jss.osiris.modules.quotation.repository.FormaliteStatusRepository;

@Service
public class FormaliteStatusServiceImpl implements FormaliteStatusService {

    @Autowired
    FormaliteStatusRepository formaliteStatusRepository;

    @Override
    @Cacheable(value = "formaliteStatusList", key = "#root.methodName")
    public List<FormaliteStatus> getFormaliteStatus() {
        return IterableUtils.toList(formaliteStatusRepository.findAll());
    }

    @Override
    @Cacheable(value = "formaliteStatus", key = "#id")
    public FormaliteStatus getFormaliteStatus(Integer id) {
        Optional<FormaliteStatus> formaliteStatus = formaliteStatusRepository.findById(id);
        if (formaliteStatus.isPresent())
            return formaliteStatus.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "formaliteStatusList", allEntries = true),
            @CacheEvict(value = "formaliteStatus", key = "#formaliteStatus.id")
    })
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
                1, ProvisionBoardResult.STATUS_NEW);
        updateStatus(FormaliteStatus.FORMALITE_IN_PROGRESS, "En cours", "autorenew", false, false, 
                2, ProvisionBoardResult.STATUS_IN_PROGRESS);
        updateStatus(FormaliteStatus.FORMALITE_WAITING_DOCUMENT, "En attente de documents", "hourglass_top", false,
                false, 
                3, ProvisionBoardResult.STATUS_WAITING);
        updateStatus(FormaliteStatus.FORMALITE_SENT, "Envoyé au greffe", "outgoing_mail", false, false, 
                3, ProvisionBoardResult.STATUS_WAITING_GREFFE);
        updateStatus(FormaliteStatus.FORMALITE_VALIDATE, "Validé par le greffe", "approval", false, false, 
                4, ProvisionBoardResult.STATUS_VALIDATE_GREFFE);
        updateStatus(FormaliteStatus.FORMALITE_REFUSED, "Refusé par le greffe", "block", false, false, 
                4, ProvisionBoardResult.STATUS_REFUSED_GREFFE);
        updateStatus(FormaliteStatus.FORMALITE_WAITING_DOCUMENT_GREFFE, "En attente de documents du greffe", "pending",
                false, false, 
                4, ProvisionBoardResult.STATUS_VALIDATE_GREFFE);
        updateStatus(FormaliteStatus.FORMALITE_DONE, "Terminé", "check_small", false, true, 
                7, ProvisionBoardResult.STATUS_DONE);

        setSuccessor(FormaliteStatus.FORMALITE_NEW, FormaliteStatus.FORMALITE_IN_PROGRESS);
        setSuccessor(FormaliteStatus.FORMALITE_IN_PROGRESS, FormaliteStatus.FORMALITE_SENT);
        setSuccessor(FormaliteStatus.FORMALITE_SENT, FormaliteStatus.FORMALITE_VALIDATE);
        setSuccessor(FormaliteStatus.FORMALITE_VALIDATE, FormaliteStatus.FORMALITE_DONE);

        setSuccessor(FormaliteStatus.FORMALITE_IN_PROGRESS, FormaliteStatus.FORMALITE_WAITING_DOCUMENT);
        setSuccessor(FormaliteStatus.FORMALITE_WAITING_DOCUMENT, FormaliteStatus.FORMALITE_SENT);
        setSuccessor(FormaliteStatus.FORMALITE_VALIDATE, FormaliteStatus.FORMALITE_WAITING_DOCUMENT_GREFFE);
        setSuccessor(FormaliteStatus.FORMALITE_WAITING_DOCUMENT_GREFFE, FormaliteStatus.FORMALITE_DONE);

        setPredecessor(FormaliteStatus.FORMALITE_DONE, FormaliteStatus.FORMALITE_VALIDATE);
        setPredecessor(FormaliteStatus.FORMALITE_REFUSED, FormaliteStatus.FORMALITE_IN_PROGRESS);
        setPredecessor(FormaliteStatus.FORMALITE_IN_PROGRESS, FormaliteStatus.FORMALITE_NEW);
        setPredecessor(FormaliteStatus.FORMALITE_WAITING_DOCUMENT, FormaliteStatus.FORMALITE_IN_PROGRESS);
        setPredecessor(FormaliteStatus.FORMALITE_WAITING_DOCUMENT_GREFFE, FormaliteStatus.FORMALITE_VALIDATE);

    }

    protected void updateStatus(String code, String label, String icon, boolean isOpenState, boolean isCloseState, 
                                    Integer aggregatePriority, String aggregateLabel) {
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
        formaliteStatus.setAggregateLabel(aggregateLabel);
        formaliteStatus.setAggregatePriority(aggregatePriority);
        addOrUpdateFormaliteStatus(formaliteStatus);
    }

    protected void setSuccessor(String code, String code2) throws OsirisException {
        FormaliteStatus sourceStatus = getFormaliteStatusByCode(code);
        FormaliteStatus targetStatus = getFormaliteStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Status code " + code + " or " + code2 + " do not exist");

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
            throw new OsirisException(null, "Formalite status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<FormaliteStatus>());

        for (FormaliteStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateFormaliteStatus(sourceStatus);
    }
}
