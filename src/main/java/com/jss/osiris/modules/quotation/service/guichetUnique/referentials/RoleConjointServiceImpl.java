package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RoleConjoint;
import com.jss.osiris.modules.quotation.repository.guichetUnique.RoleConjointRepository;

@Service
public class RoleConjointServiceImpl implements RoleConjointService {

    @Autowired
    RoleConjointRepository RoleConjointRepository;

    @Override
    @Cacheable(value = "roleConjointList", key = "#root.methodName")
    public List<RoleConjoint> getRoleConjoint() {
        return IterableUtils.toList(RoleConjointRepository.findAll());
    }
}
