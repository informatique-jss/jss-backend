package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RoleContrat;
import com.jss.osiris.modules.quotation.repository.guichetUnique.RoleContratRepository;

@Service
public class RoleContratServiceImpl implements RoleContratService {

    @Autowired
    RoleContratRepository RoleContratRepository;

    @Override
    @Cacheable(value = "roleContratList", key = "#root.methodName")
    public List<RoleContrat> getRoleContrat() {
        return IterableUtils.toList(RoleContratRepository.findAll());
    }
}
