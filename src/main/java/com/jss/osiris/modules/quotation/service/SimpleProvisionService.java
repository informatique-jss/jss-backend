package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.modules.quotation.model.SimpleProvision;

public interface SimpleProvisionService {
    public SimpleProvision getSimpleProvision(Integer id);

    public SimpleProvision addOrUpdateSimpleProvision(SimpleProvision simpleProvision);
}
