package com.jss.osiris.modules.quotation.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.DebourDel;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.repository.DebourDelRepository;

import jakarta.transaction.Transactional;

@Service
public class DebourDelServiceImpl implements DebourDelService {

    @Autowired
    DebourDelRepository debourDelRepository;

    @Autowired
    ProvisionService provisionService;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<DebourDel> getDebourByProvision(Provision provision) {
        provision = provisionService.getProvision(provision.getId());
        return debourDelRepository.findByProvision(provision);
    }

    @Override
    public void deleteDuplicateDebourDel() {
        debourDelRepository.deleteDuplicateDebourDel("2024");
    }
}
