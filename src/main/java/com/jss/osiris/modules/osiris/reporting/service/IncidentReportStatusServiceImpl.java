package com.jss.osiris.modules.osiris.reporting.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.reporting.model.IncidentReportStatus;
import com.jss.osiris.modules.osiris.reporting.repository.IncidentReportStatusRepository;

@Service
public class IncidentReportStatusServiceImpl implements IncidentReportStatusService {

    @Autowired
    IncidentReportStatusRepository incidentReportStatusRepository;

    @Override
    public List<IncidentReportStatus> getIncidentReportStatusList() {
        return IterableUtils.toList(incidentReportStatusRepository.findAll());
    }

    @Override
    public IncidentReportStatus getIncidentReportStatus(Integer id) {
        Optional<IncidentReportStatus> incidentReportStatus = incidentReportStatusRepository.findById(id);
        if (incidentReportStatus.isPresent())
            return incidentReportStatus.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IncidentReportStatus addOrUpdateIncidentReportStatus(
            IncidentReportStatus incidentReportStatus) {
        return incidentReportStatusRepository.save(incidentReportStatus);
    }

    @Override
    public IncidentReportStatus getIncidentReportStatusByCode(String code) {
        return incidentReportStatusRepository.findByCode(code);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatusReferential() throws OsirisException {
        updateStatus(IncidentReportStatus.TO_COMPLETE, "A compléter", "auto_awesome");
        updateStatus(IncidentReportStatus.TO_ANALYSE, "A analyser", "search");
        updateStatus(IncidentReportStatus.ACTIONS_IN_PROGRESS, "Actions en cours", "pending");
        updateStatus(IncidentReportStatus.FINALIZED, "Terminé", "verified");
        updateStatus(IncidentReportStatus.ABANDONED, "Abandonné", "block");

        setSuccessor(IncidentReportStatus.TO_COMPLETE, IncidentReportStatus.TO_ANALYSE);
        setSuccessor(IncidentReportStatus.TO_ANALYSE, IncidentReportStatus.ACTIONS_IN_PROGRESS);
        setSuccessor(IncidentReportStatus.TO_ANALYSE, IncidentReportStatus.FINALIZED);

        setPredecessor(IncidentReportStatus.FINALIZED, IncidentReportStatus.ACTIONS_IN_PROGRESS);
        setPredecessor(IncidentReportStatus.FINALIZED, IncidentReportStatus.TO_ANALYSE);
        setPredecessor(IncidentReportStatus.FINALIZED, IncidentReportStatus.TO_COMPLETE);

        setPredecessor(IncidentReportStatus.ACTIONS_IN_PROGRESS, IncidentReportStatus.TO_ANALYSE);
        setPredecessor(IncidentReportStatus.ACTIONS_IN_PROGRESS, IncidentReportStatus.TO_COMPLETE);

        setPredecessor(IncidentReportStatus.TO_ANALYSE, IncidentReportStatus.TO_COMPLETE);

        // All cancelled
        setSuccessor(IncidentReportStatus.TO_COMPLETE, IncidentReportStatus.ABANDONED);
        setSuccessor(IncidentReportStatus.TO_ANALYSE, IncidentReportStatus.ABANDONED);
        setSuccessor(IncidentReportStatus.ACTIONS_IN_PROGRESS, IncidentReportStatus.ABANDONED);
        setSuccessor(IncidentReportStatus.FINALIZED, IncidentReportStatus.ABANDONED);
    }

    protected void updateStatus(String code, String label, String icon) {
        IncidentReportStatus incidentReportStatus = getIncidentReportStatusByCode(code);
        if (getIncidentReportStatusByCode(code) == null)
            incidentReportStatus = new IncidentReportStatus();
        incidentReportStatus.setPredecessors(null);
        incidentReportStatus.setSuccessors(null);
        incidentReportStatus.setCode(code);
        incidentReportStatus.setLabel(label);
        incidentReportStatus.setIcon(icon);
        addOrUpdateIncidentReportStatus(incidentReportStatus);
    }

    protected void setSuccessor(String code, String code2) throws OsirisException {
        IncidentReportStatus sourceStatus = getIncidentReportStatusByCode(code);
        IncidentReportStatus targetStatus = getIncidentReportStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getSuccessors() == null)
            sourceStatus.setSuccessors(new ArrayList<IncidentReportStatus>());

        for (IncidentReportStatus status : sourceStatus.getSuccessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getSuccessors().add(targetStatus);
        addOrUpdateIncidentReportStatus(sourceStatus);
    }

    protected void setPredecessor(String code, String code2) throws OsirisException {
        IncidentReportStatus sourceStatus = getIncidentReportStatusByCode(code);
        IncidentReportStatus targetStatus = getIncidentReportStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new OsirisException(null, "Quotation status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<IncidentReportStatus>());

        for (IncidentReportStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateIncidentReportStatus(sourceStatus);
    }
}
