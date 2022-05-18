package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Civility;

public interface CivilityService {
    public List<Civility> getCivilities();

    public Civility getCivility(Integer id);
}
