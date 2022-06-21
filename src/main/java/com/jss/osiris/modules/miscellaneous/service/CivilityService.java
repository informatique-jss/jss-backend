package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Civility2;

public interface CivilityService {
    public List<Civility2> getCivilities();

    public Civility2 getCivility(Integer id);
}
