package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RoleEntreprise;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.RoleEntrepriseRepository;

@Service
public class RoleEntrepriseServiceImpl implements RoleEntrepriseService {

    @Autowired
    RoleEntrepriseRepository RoleEntrepriseRepository;

    @Override
    public List<RoleEntreprise> getRoleEntreprise() {
        return IterableUtils.toList(RoleEntrepriseRepository.findAll());
    }
}
