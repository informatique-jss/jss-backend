package com.jss.osiris.modules.myjss.wordpress.repository;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.myjss.wordpress.model.AssoProvisionPostNewspaper;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface AssoProvisionPostNewspaperRepository
                extends QueryCacheCrudRepository<AssoProvisionPostNewspaper, Integer> {

        AssoProvisionPostNewspaper findByProvision(Provision provision);
}
