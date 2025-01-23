package com.jss.osiris.modules.myjss.profile.service;

import java.util.List;

import com.jss.osiris.modules.myjss.profile.model.UserScope;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface UserScopeService {

    public UserScope addOrUpdateUserScope(UserScope userScope);

    public List<Responsable> getPotentialUserScope();

    public List<UserScope> getUserScope();

    public List<Responsable> getUserCurrentScopeResponsables();

    public void addResponsableToCurrentUserScope(List<Responsable> responsablesToAdd);
}
