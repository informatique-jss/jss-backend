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
import com.jss.osiris.modules.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.quotation.repository.SimpleProvisonStatusRepository;

@Service
public class SimpleProvisionStatusServiceImpl implements SimpleProvisionStatusService {

    @Autowired
    SimpleProvisonStatusRepository simpleProvisonStatusRepository;

    @Override
    @Cacheable(value = "simpleProvisonStatusList", key = "#root.methodName")
    public List<SimpleProvisionStatus> getSimpleProvisionStatus() {
        return IterableUtils.toList(simpleProvisonStatusRepository.findAll());
    }

    @Override
    @Cacheable(value = "simpleProvisonStatus", key = "#id")
    public SimpleProvisionStatus getSimpleProvisonStatus(Integer id) {
        Optional<SimpleProvisionStatus> simpleProvisonStatus = simpleProvisonStatusRepository.findById(id);
        if (simpleProvisonStatus.isPresent())
            return simpleProvisonStatus.get();
        return null;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "simpleProvisonStatusList", allEntries = true),
            @CacheEvict(value = "simpleProvisonStatus", key = "#simpleProvisonStatus.id")
    })
    @Transactional(rollbackFor = Exception.class)
    public SimpleProvisionStatus addOrUpdateSimpleProvisonStatus(
            SimpleProvisionStatus simpleProvisonStatus) {
        return simpleProvisonStatusRepository.save(simpleProvisonStatus);
    }

    @Override
    public SimpleProvisionStatus getSimpleProvisionStatusByCode(String code) {
        return simpleProvisonStatusRepository.findByCode(code);
    }

    @Override
    public void updateStatusReferential() throws OsirisException {
        updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_NEW, "Nouveau", "auto_awesome", true, false);
        updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS, "En cours", "autorenew", false, false);
        updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT, "En attente de documents",
                "hourglass_top", false,
                false);
        updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY,
                "En attente de l'autorité compétente", "pending",
                false, false);
        updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_DONE, "Terminé", "check_small", false, true);

        setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_NEW, SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS);
        setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS,
                SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT);
        setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS,
                SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY);
        setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS, SimpleProvisionStatus.SIMPLE_PROVISION_DONE);
        setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT,
                SimpleProvisionStatus.SIMPLE_PROVISION_DONE);
        setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY,
                SimpleProvisionStatus.SIMPLE_PROVISION_DONE);

        setPredecessor(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY,
                SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS);
        setPredecessor(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT,
                SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS);

    }

    protected void updateStatus(String code, String label, String icon, boolean isOpenState, boolean isCloseState) {
        SimpleProvisionStatus simpleProvisionStatus = getSimpleProvisionStatusByCode(code);
        if (getSimpleProvisionStatusByCode(code) == null)
            simpleProvisionStatus = new SimpleProvisionStatus();
        simpleProvisionStatus.setPredecessors(null);
        simpleProvisionStatus.setSuccessors(null);
        simpleProvisionStatus.setCode(code);
        simpleProvisionStatus.setLabel(label);
        simpleProvisionStatus.setIcon(icon);
        simpleProvisionStatus.setIsCloseState(isCloseState);
        simpleProvisionStatus.setIsOpenState(isOpenState);
        addOrUpdateSimpleProvisonStatus(simpleProvisionStatus);
    }

    protected void setSuccessor(String code, String code2) throws OsirisException {
        SimpleProvisionStatus sourceStatus = getSimpleProvisionStatusByCode(code);
        SimpleProvisionStatus targetStatus = getSimpleProvisionStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Simple provision " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getSuccessors() == null)
            sourceStatus.setSuccessors(new ArrayList<SimpleProvisionStatus>());

        for (SimpleProvisionStatus status : sourceStatus.getSuccessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getSuccessors().add(targetStatus);
        addOrUpdateSimpleProvisonStatus(sourceStatus);
    }

    protected void setPredecessor(String code, String code2) throws OsirisException {
        SimpleProvisionStatus sourceStatus = getSimpleProvisionStatusByCode(code);
        SimpleProvisionStatus targetStatus = getSimpleProvisionStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Simple provision status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<SimpleProvisionStatus>());

        for (SimpleProvisionStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateSimpleProvisonStatus(sourceStatus);
    }
}
