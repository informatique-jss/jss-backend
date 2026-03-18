package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.tiers.model.TiersGroup;
import com.jss.osiris.modules.osiris.tiers.repository.TiersGroupRepository;

@Service
public class TiersGroupServiceImpl implements TiersGroupService {

    @Autowired
    TiersGroupRepository tiersGroupRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TiersGroup addOrUpdateTiersGroup(TiersGroup tiersGroup) throws OsirisException, OsirisDuplicateException {
        if (tiersGroup == null)
            throw new OsirisException(null, "Provided tiersGroup is null");

        return tiersGroupRepository.save(tiersGroup);
    }

    @Override
    public List<TiersGroup> getTiersGroups() {
        return IterableUtils.toList(tiersGroupRepository.findAll());
    }

    @Override
    public TiersGroup getTiersGroup(Integer id) {
        Optional<TiersGroup> tiersGroup = tiersGroupRepository.findById(id);
        if (tiersGroup.isPresent())
            return tiersGroup.get();
        return null;
    }
}
