package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.modules.osiris.miscellaneous.model.InformationBanner;
import com.jss.osiris.modules.osiris.miscellaneous.repository.InformationBannerRepository;

@Service
public class InformationBannerServiceImpl implements InformationBannerService {

    @Autowired
    InformationBannerRepository informationBannerRepository;

    @Override
    public List<InformationBanner> getInformationBanners() {
        return IterableUtils.toList(informationBannerRepository.findAll());
    }

    @Override
    public InformationBanner getInformationBanner(Integer id) {
        Optional<InformationBanner> informationBanner = informationBannerRepository.findById(id);
        if (informationBanner.isPresent())
            return informationBanner.get();
        return null;
    }

    @Override
    public InformationBanner getActiveInformationBanner() {
        Optional<InformationBanner> informationBanner = informationBannerRepository.findByIsActive(Boolean.TRUE);
        if (informationBanner.isPresent())
            return informationBanner.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InformationBanner addOrUpdateInformationBanner(
            InformationBanner informationBanner) throws OsirisClientMessageException {
        return informationBannerRepository.save(informationBanner);
    }
}
