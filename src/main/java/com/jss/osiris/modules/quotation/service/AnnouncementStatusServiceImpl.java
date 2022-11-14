package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @PostConstruct
    public void updateStatusReferential() throws Exception {
        updateStatus(AnnouncementStatus.ANNOUNCEMENT_NEW, "Nouveau", "auto_awesome", true, false);
        updateStatus(AnnouncementStatus.ANNOUNCEMENT_TODO, "A faire", "pending", false, false);
        updateStatus(AnnouncementStatus.ANNOUNCEMENT_DONE, "Terminé", "check_small", false, true);

        setSuccessor(AnnouncementStatus.ANNOUNCEMENT_NEW, AnnouncementStatus.ANNOUNCEMENT_TODO);
        setSuccessor(AnnouncementStatus.ANNOUNCEMENT_TODO, AnnouncementStatus.ANNOUNCEMENT_DONE);
        setPredecessor(AnnouncementStatus.ANNOUNCEMENT_TODO, AnnouncementStatus.ANNOUNCEMENT_NEW);
        setPredecessor(AnnouncementStatus.ANNOUNCEMENT_DONE, AnnouncementStatus.ANNOUNCEMENT_TODO);
    }

    protected void updateStatus(String code, String label, String icon, boolean isOpenState, boolean isCloseState) {
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
        addOrUpdateAnnouncementStatus(announcementStatus);
    }

    protected void setSuccessor(String code, String code2) throws Exception {
        AnnouncementStatus sourceStatus = getAnnouncementStatusByCode(code);
        AnnouncementStatus targetStatus = getAnnouncementStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new Exception("Status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getSuccessors() == null)
            sourceStatus.setSuccessors(new ArrayList<AnnouncementStatus>());

        for (AnnouncementStatus status : sourceStatus.getSuccessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getSuccessors().add(targetStatus);
        addOrUpdateAnnouncementStatus(sourceStatus);
    }

    protected void setPredecessor(String code, String code2) throws Exception {
        AnnouncementStatus sourceStatus = getAnnouncementStatusByCode(code);
        AnnouncementStatus targetStatus = getAnnouncementStatusByCode(code2);
        if (sourceStatus == null || targetStatus == null)
            throw new Exception("Announcement status code " + code + " or " + code2 + " do not exist");

        if (sourceStatus.getPredecessors() == null)
            sourceStatus.setPredecessors(new ArrayList<AnnouncementStatus>());

        for (AnnouncementStatus status : sourceStatus.getPredecessors())
            if (status.getCode().equals(targetStatus.getCode()))
                return;

        sourceStatus.getPredecessors().add(targetStatus);
        addOrUpdateAnnouncementStatus(sourceStatus);
    }
}
