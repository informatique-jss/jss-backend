package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.RoleContrat;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.RoleContratRepository;

@Service
public class RoleContratServiceImpl implements RoleContratService {

    @Autowired
    RoleContratRepository RoleContratRepository;

    @Override
    public List<RoleContrat> getRoleContrat() {
        return IterableUtils.toList(RoleContratRepository.findAll());
    }
}
