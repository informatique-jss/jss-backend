package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RoleEntreprise;
import com.jss.osiris.modules.quotation.repository.guichetUnique.RoleEntrepriseRepository;

@Service
public class RoleEntrepriseServiceImpl implements RoleEntrepriseService {

    @Autowired
    RoleEntrepriseRepository RoleEntrepriseRepository;

    @Override
    @Cacheable(value = "roleEntrepriseList", key = "#root.methodName")
    public List<RoleEntreprise> getRoleEntreprise() {
        return IterableUtils.toList(RoleEntrepriseRepository.findAll());
    }
}
