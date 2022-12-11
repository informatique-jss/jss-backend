package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.SimpleProvision;

public interface SimpleProvisionService {
    public List<SimpleProvision> getSimpleProvisions();

    public SimpleProvision getSimpleProvision(Integer id);
	
	 public SimpleProvision addOrUpdateSimpleProvision(SimpleProvision simpleProvision);
}
