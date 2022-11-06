package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Role;
import com.jss.osiris.modules.quotation.repository.guichetUnique.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository RoleRepository;

    @Override
    @Cacheable(value = "roleList", key = "#root.methodName")
    public List<Role> getRole() {
        return IterableUtils.toList(RoleRepository.findAll());
    }
}
