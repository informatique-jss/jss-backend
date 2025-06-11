package com.jss.osiris.modules.myjss.wordpress.service;

import com.jss.osiris.modules.myjss.wordpress.model.AssoProvisionPostNewspaper;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface AssoProvisionPostNewspaperService {

    public AssoProvisionPostNewspaper addOrUpdateAssoMailPost(AssoProvisionPostNewspaper assoProvisionPostNewspaper);

    public AssoProvisionPostNewspaper getAssoProvisionPostNewspaperByProvision(Provision provision);

}
