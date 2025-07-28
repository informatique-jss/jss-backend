package com.jss.osiris.modules.myjss.profile.service;

import java.util.List;

import com.jss.osiris.modules.osiris.tiers.model.Responsable;

import jakarta.servlet.http.HttpServletRequest;

public interface UserScopeService {

    public List<Responsable> getPotentialUserScope();

    public List<Responsable> getPotentialUserScope(Responsable responsable);

    public void authenticateUser(Responsable responsable, HttpServletRequest request);
}
