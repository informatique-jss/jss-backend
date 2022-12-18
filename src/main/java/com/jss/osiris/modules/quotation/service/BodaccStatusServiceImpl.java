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
import com.jss.osiris.modules.quotation.model.BodaccStatus;
import com.jss.osiris.modules.quotation.repository.BodaccStatusRepository;

@Service
public class BodaccStatusServiceImpl implements BodaccStatusService {

    @Autowired
    BodaccStatusRepository bodaccStatusRepository;

    @Override
    @Cacheable(value = "bodaccStatusList", key = "#root.methodName")
    public List<BodaccStatus> getBodaccStatus() {
        return IterableUtils.toList(bodaccStatusRepository.findAll());
    }

    @Override
    @Cacheable(value = "bodaccStatus", key = "#id")
    public BodaccStatus getBodaccStatus(Integer id) {
        Optional<BodaccStatus> bodaccStatus = bodaccStatusRepository.findById(id);
        if (bodaccStatus.isPresent())
            return bodaccStatus.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "bodaccStatusList", allEntries = true),
            @CacheEvict(value = "bodaccStatus", key = "#bodaccStatus.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public BodaccStatus addOrUpdateBodaccStatus(
            BodaccStatus bodaccStatus) {
        return bodaccStatusRepository.save(bodaccStatus);
    }

    @Override
    public BodaccStatus getBodaccStatusByCode(String code) {
        return bodaccStatusRepository.findByCode(code);
    }

    @Override
    public void updateStatusReferential() throws OsirisException {
        updateStatus(BodaccStatus.BODACC_NEW, "Nouveau", "auto_awesome", true, false);
        updateStatus(BodaccStatus.BODACC_IN_PROGRESS, "En cours", "autorenew", false, false);
        updateStatus(BodaccStatus.BODACC_WAITING_DOCUMENT, "En attente de documents", "hourglass_top", false, false);
        updateStatus(BodaccStatus.BODACC_WAITING_DOCUMENT_BODACC, "En attente de documents du BODACC", "hourglass_top",
                false, false);
        updateStatus(BodaccStatus.BODACC_WAITING_PUBLICATION, "En attente de parution", "new_releases", false, false);
        updateStatus(BodaccStatus.BODACC_PUBLISHED, "Publié", "fact_check", false, false);
        updateStatus(BodaccStatus.BODACC_DONE, "Terminé", "check_small", false, true);

        setSuccessor(BodaccStatus.BODACC_NEW, BodaccStatus.BODACC_IN_PROGRESS);
        setSuccessor(BodaccStatus.BODACC_IN_PROGRESS, BodaccStatus.BODACC_WAITING_PUBLICATION);
        setSuccessor(BodaccStatus.BODACC_IN_PROGRESS, BodaccStatus.BODACC_WAITING_DOCUMENT);
        setSuccessor(BodaccStatus.BODACC_WAITING_DOCUMENT, BodaccStatus.BODACC_WAITING_PUBLICATION);
        setSuccessor(BodaccStatus.BODACC_WAITING_PUBLICATION, BodaccStatus.BODACC_PUBLISHED);
        setSuccessor(BodaccStatus.BODACC_PUBLISHED, BodaccStatus.BODACC_WAITING_DOCUMENT_BODACC);
        setSuccessor(BodaccStatus.BODACC_WAITING_DOCUMENT_BODACC, BodaccStatus.BODACC_DONE);
        setSuccessor(BodaccStatus.BODACC_PUBLISHED, BodaccStatus.BODACC_DONE);

        setPredecessor(BodaccStatus.BODACC_DONE, BodaccStatus.BODACC_PUBLISHED);
        setPredecessor(BodaccStatus.BODACC_WAITING_DOCUMENT, BodaccStatus.BODACC_IN_PROGRESS);
        setPredecessor(BodaccStatus.BODACC_PUBLISHED, BodaccStatus.BODACC_WAITING_PUBLICATION);
        setPredecessor(BodaccStatus.BODACC_WAITING_PUBLICATION, BodaccStatus.BODACC_IN_PROGRESS);
        setPredecessor(BodaccStatus.BODACC_PUBLISHED, BodaccStatus.BODACC_WAITING_PUBLICATION);
        setPredecessor(BodaccStatus.BODACC_IN_PROGRESS, BodaccStatus.BODACC_NEW);
    }

    protected void updateStatus(String code, String label, String icon, boolean isOpenState, boolean isCloseState) {
        BodaccStatus bodaccStatus = getBodaccStatusByCode(code);
        if (getBodaccStatusByCode(code) == null)
            bodaccStatus = new BodaccStatus();
        bodaccStatus.setPredecessors(null);
        bodaccStatus.setSuccessors(null);
        bodaccStatus.setCode(code);
        bodaccStatus.setLabel(label);
        bodaccStatus.setIcon(icon);
        bodaccStatus.setIsCloseState(isCloseState);
        bodaccStatus.setIsOpenState(isOpenState);
        addOrUpdateBodaccStatus(bodaccStatus);
    }

    protected void setSuccessor(String code, String code2) throws OsirisException {
        BodaccStatus sourceStatus = getBodaccStatusByCode(code);
        BodaccStatus targetStatus = getBodaccStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getSuccessors() == null)
            sourceStatus.setSuccessors(new ArrayList<BodaccStatus>());

        for (BodaccStatus status : sourceStatus.getSuccessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getSuccessors().add(targetStatus);
        addOrUpdateBodaccStatus(sourceStatus);
    }

    protected void setPredecessor(String code, String code2) throws OsirisException {
        BodaccStatus sourceStatus = getBodaccStatusByCode(code);
        BodaccStatus targetStatus = getBodaccStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Bodacc status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<BodaccStatus>());

        for (BodaccStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateBodaccStatus(sourceStatus);
    }
}