package com.jss.jssbackend.modules.quotation.service;

import java.util.Optional;

import com.jss.jssbackend.modules.quotation.model.Provision;
import com.jss.jssbackend.modules.quotation.repository.ProvisionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProvisionServiceImpl implements ProvisionService {

    @Autowired
    ProvisionRepository provisionRepository;

    @Override
    public Provision getProvision(Integer id) {
        Optional<Provision> provision = provisionRepository.findById(id);
        if (!provision.isEmpty())
            return provision.get();
        return null;
    }
}
