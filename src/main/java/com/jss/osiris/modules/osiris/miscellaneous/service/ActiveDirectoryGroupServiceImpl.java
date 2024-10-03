package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.ActiveDirectoryGroup;
import com.jss.osiris.modules.osiris.miscellaneous.repository.ActiveDirectoryGroupRepository;

@Service
public class ActiveDirectoryGroupServiceImpl implements ActiveDirectoryGroupService {

    @Autowired
    ActiveDirectoryGroupRepository activeDirectoryGroupRepository;

    @Override
    public List<ActiveDirectoryGroup> getActiveDirectoryGroups() {
        return IterableUtils.toList(activeDirectoryGroupRepository.findAll());
    }

    @Override
    public ActiveDirectoryGroup getActiveDirectoryGroup(Integer id) {
        Optional<ActiveDirectoryGroup> activeDirectoryGroup = activeDirectoryGroupRepository.findById(id);
        if (activeDirectoryGroup.isPresent())
            return activeDirectoryGroup.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActiveDirectoryGroup addOrUpdateActiveDirectoryGroup(
            ActiveDirectoryGroup activeDirectoryGroup) {
        return activeDirectoryGroupRepository.save(activeDirectoryGroup);
    }
}
