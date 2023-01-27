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
import com.jss.osiris.modules.quotation.model.DomiciliationStatus;
import com.jss.osiris.modules.quotation.repository.DomiciliationStatusRepository;

@Service
public class DomiciliationStatusServiceImpl implements DomiciliationStatusService {

    @Autowired
    DomiciliationStatusRepository domiciliationStatusRepository;

    @Override
    @Cacheable(value = "domiciliationStatusList", key = "#root.methodName")
    public List<DomiciliationStatus> getDomiciliationStatus() {
        return IterableUtils.toList(domiciliationStatusRepository.findAll());
    }

    @Override
    @Cacheable(value = "domiciliationStatus", key = "#id")
    public DomiciliationStatus getDomiciliationStatus(Integer id) {
        Optional<DomiciliationStatus> domiciliationStatus = domiciliationStatusRepository.findById(id);
        if (domiciliationStatus.isPresent())
            return domiciliationStatus.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "domiciliationStatusList", allEntries = true),
            @CacheEvict(value = "domiciliationStatus", key = "#domiciliationStatus.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public DomiciliationStatus addOrUpdateDomiciliationStatus(
            DomiciliationStatus domiciliationStatus) {
        return domiciliationStatusRepository.save(domiciliationStatus);
    }

    @Override
    public DomiciliationStatus getDomiciliationStatusByCode(String code) {
        return domiciliationStatusRepository.findByCode(code);
    }

    @Override
    public void updateStatusReferential() throws OsirisException {
        Integer priority = 1;
        updateStatus(DomiciliationStatus.DOMICILIATION_NEW, "Nouveau", "auto_awesome", true, false, priority++, "Ouvert");
        updateStatus(DomiciliationStatus.DOMICILIATION_IN_PROGRESS, "En cours", "autorenew", false, false, priority++);
        updateStatus(DomiciliationStatus.DOMICILIATION_WAITING_FOR_DOCUMENTS, "En attente de documents",
                "hourglass_top", false, false, priority++);
        updateStatus(DomiciliationStatus.DOMICILIATION_DONE, "Terminé", "check_small", false, true, priority++);

        setSuccessor(DomiciliationStatus.DOMICILIATION_NEW, DomiciliationStatus.DOMICILIATION_IN_PROGRESS);
        setSuccessor(DomiciliationStatus.DOMICILIATION_IN_PROGRESS,
                DomiciliationStatus.DOMICILIATION_WAITING_FOR_DOCUMENTS);
        setSuccessor(DomiciliationStatus.DOMICILIATION_IN_PROGRESS, DomiciliationStatus.DOMICILIATION_DONE);

        setPredecessor(DomiciliationStatus.DOMICILIATION_IN_PROGRESS, DomiciliationStatus.DOMICILIATION_NEW);
        setPredecessor(DomiciliationStatus.DOMICILIATION_WAITING_FOR_DOCUMENTS,
                DomiciliationStatus.DOMICILIATION_IN_PROGRESS);
        setPredecessor(DomiciliationStatus.DOMICILIATION_DONE, DomiciliationStatus.DOMICILIATION_IN_PROGRESS);
    }

protected void updateStatus(String code, String label, String icon, boolean isOpenState, boolean isCloseState, 
    Integer aggregatePriority) {
updateStatus(code, label, icon, isOpenState, isCloseState, aggregatePriority, label);
}
protected void updateStatus(String code, String label, String icon, boolean isOpenState, boolean isCloseState, 
                                Integer aggregatePriority, String aggregateLabel) {
DomiciliationStatus domiciliationStatus = getDomiciliationStatusByCode(code);
        if (getDomiciliationStatusByCode(code) == null)
            domiciliationStatus = new DomiciliationStatus();
        domiciliationStatus.setPredecessors(null);
        domiciliationStatus.setSuccessors(null);
        domiciliationStatus.setCode(code);
        domiciliationStatus.setLabel(label);
        domiciliationStatus.setIcon(icon);
        domiciliationStatus.setIsCloseState(isCloseState);
        domiciliationStatus.setIsOpenState(isOpenState);
        domiciliationStatus.setAggregateLabel(aggregateLabel);
        domiciliationStatus.setAggregatePriority(aggregatePriority);
        addOrUpdateDomiciliationStatus(domiciliationStatus);
    }

    protected void setSuccessor(String code, String code2) throws OsirisException {
        DomiciliationStatus sourceStatus = getDomiciliationStatusByCode(code);
        DomiciliationStatus targetStatus = getDomiciliationStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getSuccessors() == null)
            sourceStatus.setSuccessors(new ArrayList<DomiciliationStatus>());

        for (DomiciliationStatus status : sourceStatus.getSuccessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getSuccessors().add(targetStatus);
        addOrUpdateDomiciliationStatus(sourceStatus);
    }

    protected void setPredecessor(String code, String code2) throws OsirisException {
        DomiciliationStatus sourceStatus = getDomiciliationStatusByCode(code);
        DomiciliationStatus targetStatus = getDomiciliationStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Domiciliation status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<DomiciliationStatus>());

        for (DomiciliationStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateDomiciliationStatus(sourceStatus);
    }
}
