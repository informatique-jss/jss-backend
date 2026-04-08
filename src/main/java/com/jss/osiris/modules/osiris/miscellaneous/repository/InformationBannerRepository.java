package com.jss.osiris.modules.osiris.miscellaneous.repository;

import java.util.Optional;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.miscellaneous.model.InformationBanner;

public interface InformationBannerRepository extends QueryCacheCrudRepository<InformationBanner, Integer> {

    Optional<InformationBanner> findByIsActive(Boolean true1);
}