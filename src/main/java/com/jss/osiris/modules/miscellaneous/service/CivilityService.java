package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Civility;

public interface CivilityService {
    public List<Civility> getCivilities();

    public Civility getCivility(Integer id);
	
	 public Civility addOrUpdateCivility(Civility civility);
}
