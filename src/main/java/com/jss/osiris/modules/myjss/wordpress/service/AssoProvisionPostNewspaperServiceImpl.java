package com.jss.osiris.modules.myjss.wordpress.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.myjss.wordpress.model.AssoProvisionPostNewspaper;
import com.jss.osiris.modules.myjss.wordpress.repository.AssoProvisionPostNewspaperRepository;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

@Service
public class AssoProvisionPostNewspaperServiceImpl implements AssoProvisionPostNewspaperService {

    @Autowired
    private AssoProvisionPostNewspaperRepository assoProvisionPostNewspaperRepository;

    @Override
    public AssoProvisionPostNewspaper addOrUpdateAssoMailPost(AssoProvisionPostNewspaper assoProvisionPostNewspaper) {
        return assoProvisionPostNewspaperRepository.save(assoProvisionPostNewspaper);
    }

    @Override
    public AssoProvisionPostNewspaper getAssoProvisionPostNewspaperByProvision(Provision provision) {
        return assoProvisionPostNewspaperRepository.findByProvision(provision);
    }

}
