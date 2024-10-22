package com.jss.osiris.modules.myjss.profile.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.profile.model.UserScope;
import com.jss.osiris.modules.myjss.profile.repository.UserScopeRepository;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

@Service
public class UserScopeServiceImpl implements UserScopeService {

    @Autowired
    UserScopeRepository userScopeRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    UserScopeServiceProxyImpl userScopeServiceProxyImpl;

    @Override

    @Caching(evict = {
            @CacheEvict(value = "potential-user-scope", key = "#userScope.responsable.id"),
            @CacheEvict(value = "user-scope", key = "#userScope.responsable.id")
    })
    public UserScope addOrUpdateUserScope(UserScope userScope) {
        return userScopeRepository.save(userScope);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addResponsableToCurrentUserScope(List<Responsable> responsablesToAdd) {
        Responsable currentUser = employeeService.getCurrentMyJssUser();
        userScopeServiceProxyImpl.addResponsableToUserScope(responsablesToAdd, currentUser);
    }

    @Override
    public List<Responsable> getPotentialUserScope() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        return userScopeServiceProxyImpl.getPotentialUserScopeForResponsable(responsable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserScope> getUserScope() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        return userScopeServiceProxyImpl.getUserScopeForResponsable(responsable);
    }

    @Override
    public List<Responsable> getUserCurrentScopeResponsables() {
        List<UserScope> currentScopes = getUserScope();
        List<Responsable> responsables = new ArrayList<Responsable>();
        for (UserScope userScope : currentScopes)
            responsables.add(userScope.getResponsableViewed());

        return responsables;
    }
}
