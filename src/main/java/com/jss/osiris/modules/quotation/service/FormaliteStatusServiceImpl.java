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

import com.jss.osiris.modules.quotation.model.FormaliteStatus;
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

    @Autowired
    public void updateStatusReferential() throws Exception {
        updateStatus(FormaliteStatus.FORMALITE_NEW, "Nouveau", "auto_awesome", true, false);
    }

    protected void updateStatus(String code, String label, String icon, boolean isOpenState, boolean isCloseState) {
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
        addOrUpdateFormaliteStatus(formaliteStatus);
    }

    protected void setSuccessor(String code, String code2) throws Exception {
        FormaliteStatus sourceStatus = getFormaliteStatusByCode(code);
        FormaliteStatus targetStatus = getFormaliteStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new Exception("Status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getSuccessors() == null)
            sourceStatus.setSuccessors(new ArrayList<FormaliteStatus>());

        for (FormaliteStatus status : sourceStatus.getSuccessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getSuccessors().add(targetStatus);
        addOrUpdateFormaliteStatus(sourceStatus);
    }

    protected void setPredecessor(String code, String code2) throws Exception {
        FormaliteStatus sourceStatus = getFormaliteStatusByCode(code);
        FormaliteStatus targetStatus = getFormaliteStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new Exception("Formalite status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<FormaliteStatus>());

        for (FormaliteStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateFormaliteStatus(sourceStatus);
    }
}
