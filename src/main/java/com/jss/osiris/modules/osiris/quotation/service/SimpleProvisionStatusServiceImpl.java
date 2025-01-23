package com.jss.osiris.modules.osiris.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.AggregateStatus;
import com.jss.osiris.modules.osiris.quotation.model.SimpleProvisionStatus;
import com.jss.osiris.modules.osiris.quotation.repository.SimpleProvisonStatusRepository;

@Service
public class SimpleProvisionStatusServiceImpl implements SimpleProvisionStatusService {

        @Autowired
        SimpleProvisonStatusRepository simpleProvisonStatusRepository;

        @Override
        public List<SimpleProvisionStatus> getSimpleProvisionStatus() {
                return IterableUtils.toList(simpleProvisonStatusRepository.findAll());
        }

        @Override
        public SimpleProvisionStatus getSimpleProvisonStatus(Integer id) {
                Optional<SimpleProvisionStatus> simpleProvisonStatus = simpleProvisonStatusRepository.findById(id);
                if (simpleProvisonStatus.isPresent())
                        return simpleProvisonStatus.get();
                return null;
        }

        @Override
        public SimpleProvisionStatus addOrUpdateSimpleProvisonStatus(
                        SimpleProvisionStatus simpleProvisonStatus) {
                return simpleProvisonStatusRepository.save(simpleProvisonStatus);
        }

        @Override
        public SimpleProvisionStatus getSimpleProvisionStatusByCode(String code) {
                return simpleProvisonStatusRepository.findByCode(code);
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public void updateStatusReferential() throws OsirisException {
                updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_NEW, "Nouveau", "auto_awesome", true, false,
                                AggregateStatus.AGGREGATE_STATUS_NEW, 2);
                updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS, "En cours", "autorenew", false, false,
                                AggregateStatus.AGGREGATE_STATUS_IN_PROGRESS, 5);
                updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT, "En attente de documents",
                                "hourglass_top", false,
                                false,
                                AggregateStatus.AGGREGATE_STATUS_WAITING, 20);
                updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_LINKED_PROVISION,
                                "En attente de prestation liée",
                                "link", false,
                                false,
                                AggregateStatus.AGGREGATE_STATUS_WAITING, 6);
                updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY,
                                "En attente de l'autorité compétente", "pending",
                                false, false,
                                AggregateStatus.AGGREGATE_STATUS_WAITING, 15);
                updateStatus(SimpleProvisionStatus.SIMPLE_PROVISION_DONE, "Terminé", "check_small", false, true,
                                AggregateStatus.AGGREGATE_STATUS_DONE, 1);

                setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_NEW,
                                SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS);
                setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS,
                                SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT);
                setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS,
                                SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY);
                setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS,
                                SimpleProvisionStatus.SIMPLE_PROVISION_DONE);
                setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT,
                                SimpleProvisionStatus.SIMPLE_PROVISION_DONE);
                setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT,
                                SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY);
                setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY,
                                SimpleProvisionStatus.SIMPLE_PROVISION_DONE);
                setSuccessor(SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS,
                                SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_LINKED_PROVISION);

                setPredecessor(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT_AUTHORITY,
                                SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS);
                setPredecessor(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_DOCUMENT,
                                SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS);
                setPredecessor(SimpleProvisionStatus.SIMPLE_PROVISION_DONE,
                                SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS);
                setPredecessor(SimpleProvisionStatus.SIMPLE_PROVISION_WAITING_LINKED_PROVISION,
                                SimpleProvisionStatus.SIMPLE_PROVISION_IN_PROGRESS);

        }

        protected void updateStatus(String code, String label, String icon, boolean isOpenState, boolean isCloseState,
                        String aggregateLabel, Integer servicePriority) {
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
                simpleProvisionStatus.setAggregateStatus(aggregateLabel);
                simpleProvisionStatus.setServicePriority(servicePriority);
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
                        throw new OsirisException(null,
                                        "Simple provision status code " + code + " or " + code2 + " do not exist");

                if (sourceStatus.getPredecessors() == null)
                        sourceStatus.setPredecessors(new ArrayList<SimpleProvisionStatus>());

                for (SimpleProvisionStatus status : sourceStatus.getPredecessors())
                        if (status.getCode().equals(targetStatus.getCode()))
                                return;

                sourceStatus.getPredecessors().add(targetStatus);
                addOrUpdateSimpleProvisonStatus(sourceStatus);
        }
}
