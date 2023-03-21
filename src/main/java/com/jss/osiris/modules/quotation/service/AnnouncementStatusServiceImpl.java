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
import com.jss.osiris.modules.quotation.model.AggregateStatus;
import com.jss.osiris.modules.quotation.model.AnnouncementStatus;
import com.jss.osiris.modules.quotation.repository.AnnouncementStatusRepository;

@Service
public class AnnouncementStatusServiceImpl implements AnnouncementStatusService {

        @Autowired
        AnnouncementStatusRepository announcementStatusRepository;

        @Override
        @Cacheable(value = "announcementStatusList", key = "#root.methodName")
        public List<AnnouncementStatus> getAnnouncementStatus() {
                return IterableUtils.toList(announcementStatusRepository.findAll());
        }

        @Override
        @Cacheable(value = "announcementStatus", key = "#id")
        public AnnouncementStatus getAnnouncementStatus(Integer id) {
                Optional<AnnouncementStatus> announcementStatus = announcementStatusRepository.findById(id);
                if (announcementStatus.isPresent())
                        return announcementStatus.get();
                return null;
        }

        @Override
        @Caching(evict = {
                        @CacheEvict(value = "announcementStatusList", allEntries = true),
                        @CacheEvict(value = "announcementStatus", key = "#announcementStatus.id")
        })
        @Transactional(rollbackFor = Exception.class)
        public AnnouncementStatus addOrUpdateAnnouncementStatus(
                        AnnouncementStatus announcementStatus) {
                return announcementStatusRepository.save(announcementStatus);
        }

        @Override
        public AnnouncementStatus getAnnouncementStatusByCode(String code) {
                return announcementStatusRepository.findByCode(code);
        }

        @Override
        public void updateStatusReferential() throws OsirisException {
                updateStatus(AnnouncementStatus.ANNOUNCEMENT_NEW, "Nouveau", "auto_awesome", true, false,
                                AggregateStatus.AGGREGATE_STATUS_NEW);
                updateStatus(AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS, "En cours", "autorenew", false, false,
                                AggregateStatus.AGGREGATE_STATUS_IN_PROGRESS);
                updateStatus(AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT, "En attente de documents",
                                "hourglass_top",
                                false,
                                false,
                                AggregateStatus.AGGREGATE_STATUS_WAITING);
                updateStatus(AnnouncementStatus.ANNOUNCEMENT_WAITING_READ_CUSTOMER, "En attente de relecture client",
                                "local_library", false,
                                false,
                                AggregateStatus.AGGREGATE_STATUS_WAITING);
                updateStatus(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE, "En attente du confrère",
                                "supervisor_account",
                                false, false,
                                AggregateStatus.AGGREGATE_STATUS_WAITING);
                updateStatus(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED,
                                "En attente de publication par le confrère", "supervisor_account",
                                false, false,
                                AggregateStatus.AGGREGATE_STATUS_WAITING);
                updateStatus(AnnouncementStatus.ANNOUNCEMENT_PUBLISHED, "Publié", "fact_check", false, false,
                                AggregateStatus.AGGREGATE_STATUS_IN_PROGRESS);
                updateStatus(AnnouncementStatus.ANNOUNCEMENT_DONE, "Terminé", "check_small", false, true,
                                AggregateStatus.AGGREGATE_STATUS_DONE);

                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_NEW,
                                AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS);
                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_NEW,
                                AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT);
                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_NEW,
                                AnnouncementStatus.ANNOUNCEMENT_WAITING_READ_CUSTOMER);
                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_NEW,
                                AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE);

                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS,
                                AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED);
                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS,
                                AnnouncementStatus.ANNOUNCEMENT_PUBLISHED);

                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_PUBLISHED,
                                AnnouncementStatus.ANNOUNCEMENT_DONE);

                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED,
                                AnnouncementStatus.ANNOUNCEMENT_PUBLISHED);

                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS,
                                AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE);
                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS,
                                AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT);

                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_READ_CUSTOMER,
                                AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS);
                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE,
                                AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS);

                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE,
                                AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED);
                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE,
                                AnnouncementStatus.ANNOUNCEMENT_PUBLISHED);

                setSuccessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT,
                                AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS);

                setPredecessor(AnnouncementStatus.ANNOUNCEMENT_DONE,
                                AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED);
                setPredecessor(AnnouncementStatus.ANNOUNCEMENT_DONE,
                                AnnouncementStatus.ANNOUNCEMENT_PUBLISHED);

                setPredecessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE_PUBLISHED,
                                AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS);

                setPredecessor(AnnouncementStatus.ANNOUNCEMENT_PUBLISHED,
                                AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS);

                setPredecessor(AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS,
                                AnnouncementStatus.ANNOUNCEMENT_NEW);

                setPredecessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE,
                                AnnouncementStatus.ANNOUNCEMENT_NEW);
                setPredecessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_CONFRERE,
                                AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS);

                setPredecessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_DOCUMENT,
                                AnnouncementStatus.ANNOUNCEMENT_IN_PROGRESS);

                setPredecessor(AnnouncementStatus.ANNOUNCEMENT_WAITING_READ_CUSTOMER,
                                AnnouncementStatus.ANNOUNCEMENT_NEW);

        }

        /**
         * Set status values for one status
         * 
         * @param code
         * @param label
         * @param icon
         * @param isOpenState
         * @param isCloseState
         */
        protected void updateStatus(String code, String label, String icon, boolean isOpenState, boolean isCloseState,
                        String aggregateStatus) {
                AnnouncementStatus announcementStatus = getAnnouncementStatusByCode(code);

                if (getAnnouncementStatusByCode(code) == null)
                        announcementStatus = new AnnouncementStatus();
                announcementStatus.setPredecessors(null);
                announcementStatus.setSuccessors(null);
                announcementStatus.setCode(code);
                announcementStatus.setLabel(label);
                announcementStatus.setIcon(icon);
                announcementStatus.setIsCloseState(isCloseState);
                announcementStatus.setIsOpenState(isOpenState);
                announcementStatus.setAggregateStatus(aggregateStatus);
                addOrUpdateAnnouncementStatus(announcementStatus);
        }

        protected void setSuccessor(String code, String code2) throws OsirisException {
                AnnouncementStatus sourceStatus = getAnnouncementStatusByCode(code);
                AnnouncementStatus targetStatus = getAnnouncementStatusByCode(code2);
                if (sourceStatus == null || targetStatus == null)
                        throw new OsirisException(null, "Status code " + code + " or " + code2 + " do not exist");

                if (sourceStatus.getSuccessors() == null)
                        sourceStatus.setSuccessors(new ArrayList<AnnouncementStatus>());

                for (AnnouncementStatus status : sourceStatus.getSuccessors())
                        if (status.getCode().equals(targetStatus.getCode()))
                                return;

                sourceStatus.getSuccessors().add(targetStatus);
                addOrUpdateAnnouncementStatus(sourceStatus);
        }

        protected void setPredecessor(String code, String code2) throws OsirisException {
                AnnouncementStatus sourceStatus = getAnnouncementStatusByCode(code);
                AnnouncementStatus targetStatus = getAnnouncementStatusByCode(code2);
                if (sourceStatus == null || targetStatus == null)
                        throw new OsirisException(null,
                                        "Announcement status code " + code + " or " + code2 + " do not exist");

                if (sourceStatus.getPredecessors() == null)
                        sourceStatus.setPredecessors(new ArrayList<AnnouncementStatus>());

                for (AnnouncementStatus status : sourceStatus.getPredecessors())
                        if (status.getCode().equals(targetStatus.getCode()))
                                return;

                sourceStatus.getPredecessors().add(targetStatus);
                addOrUpdateAnnouncementStatus(sourceStatus);
        }
}
