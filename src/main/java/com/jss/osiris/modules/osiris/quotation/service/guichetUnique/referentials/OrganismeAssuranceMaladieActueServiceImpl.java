package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.OrganismeAssuranceMaladieActue;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.OrganismeAssuranceMaladieActueRepository;

@Service
public class OrganismeAssuranceMaladieActueServiceImpl implements OrganismeAssuranceMaladieActueService {

    @Autowired
    OrganismeAssuranceMaladieActueRepository OrganismeAssuranceMaladieActueRepository;

    @Override
    public List<OrganismeAssuranceMaladieActue> getOrganismeAssuranceMaladieActue() {
        return IterableUtils.toList(OrganismeAssuranceMaladieActueRepository.findAll());
    }
}
