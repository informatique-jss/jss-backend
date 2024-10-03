package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RolePourEntreprise;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.RolePourEntrepriseRepository;

@Service
public class RolePourEntrepriseServiceImpl implements RolePourEntrepriseService {

    @Autowired
    RolePourEntrepriseRepository RolePourEntrepriseRepository;

    @Override
    public List<RolePourEntreprise> getRolePourEntreprise() {
        return IterableUtils.toList(RolePourEntrepriseRepository.findAll());
    }
}
