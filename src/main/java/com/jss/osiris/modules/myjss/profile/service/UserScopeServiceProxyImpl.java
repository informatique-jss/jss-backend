package com.jss.osiris.modules.myjss.profile.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.myjss.profile.model.UserScope;
import com.jss.osiris.modules.myjss.profile.repository.UserScopeRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

@Service
public class UserScopeServiceProxyImpl {

    @Autowired
    UserScopeRepository userScopeRepository;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    UserScopeService userScopeService;

    @Cacheable(value = "potential-user-scope", key = "#responsable.id")
    public List<Responsable> getPotentialUserScopeForResponsable(Responsable responsable) {
        if (responsable != null) {
            List<Integer> potentialUserScopeIds = userScopeRepository
                    .getPotentialUserScope(responsable.getMail().getId());
            if (potentialUserScopeIds != null) {
                List<Responsable> potentialUserScope = new ArrayList<Responsable>();
                for (Integer potentialUserScopeId : potentialUserScopeIds)
                    potentialUserScope.add(responsableService.getResponsable(potentialUserScopeId));
                return potentialUserScope;
            }
            return Arrays.asList(responsable);
        }
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    @Cacheable(value = "user-scope", key = "#responsable.id")
    public List<UserScope> getUserScopeForResponsable(Responsable responsable) {
        if (responsable != null) {
            checkUserScope(responsable);
            List<UserScope> userScope = userScopeRepository.findByResponsable(responsable);
            if (userScope != null && userScope.size() > 0)
                return userScope;

            UserScope defaultScope = new UserScope();
            defaultScope.setResponsable(responsable);
            defaultScope.setResponsableViewed(responsable);

            return Arrays.asList(userScopeService.addOrUpdateUserScope(defaultScope));
        }
        return null;
    }

    private void checkUserScope(Responsable responsable) {
        List<Responsable> potentialScope = getPotentialUserScopeForResponsable(responsable);
        if (potentialScope == null || potentialScope.size() <= 1)
            return;

        List<UserScope> currentScope = userScopeRepository.findByResponsable(responsable);
        if (currentScope != null && currentScope.size() > 0)
            for (UserScope currentScopeResponsable : currentScope) {
                boolean found = false;
                for (Responsable authorizedScopeResponsable : potentialScope) {
                    if (currentScopeResponsable.getResponsableViewed().getId()
                            .equals(authorizedScopeResponsable.getId())) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    userScopeRepository.delete(currentScopeResponsable);
            }
    }

    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
            @CacheEvict(value = "potential-user-scope", key = "#userScope.responsable.id"),
            @CacheEvict(value = "user-scope", key = "#userScope.responsable.id")
    })
    public void addResponsableToUserScope(List<Responsable> responsablesToAdd, Responsable responsable) {
        if (responsable != null) {
            List<UserScope> existingScope = userScopeRepository.findByResponsable(responsable);
            userScopeRepository.deleteAll(existingScope);

            if (responsablesToAdd != null && responsablesToAdd.size() > 0)
                for (Responsable responsableToAdd : responsablesToAdd) {
                    List<Responsable> potentialScope = getPotentialUserScopeForResponsable(responsable);
                    for (Responsable potentialResponsable : potentialScope)
                        if (potentialResponsable.getId().equals(responsableToAdd.getId())) {
                            UserScope newScope = new UserScope();
                            newScope.setResponsable(responsable);
                            newScope.setResponsableViewed(responsableToAdd);
                            userScopeService.addOrUpdateUserScope(newScope);
                        }
                }
        }
    }

}
