package com.jss.osiris.modules.myjss.profile.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.modules.myjss.profile.model.UserScope;
import com.jss.osiris.modules.myjss.profile.repository.UserScopeRepository;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class UserScopeServiceImpl implements UserScopeService {

    @Autowired
    UserScopeRepository userScopeRepository;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    ResponsableService responsableService;

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
        Responsable responsable = employeeService.getCurrentMyJssUser();
        if (responsable != null) {
            List<UserScope> existingScope = userScopeRepository.findByResponsable(responsable);
            userScopeRepository.deleteAll(existingScope);

            if (responsablesToAdd != null && responsablesToAdd.size() > 0)
                for (Responsable responsableToAdd : responsablesToAdd) {
                    List<Responsable> potentialScope = getPotentialUserScope();
                    for (Responsable potentialResponsable : potentialScope)
                        if (potentialResponsable.getId().equals(responsableToAdd.getId())) {
                            UserScope newScope = new UserScope();
                            newScope.setResponsable(responsable);
                            newScope.setResponsableViewed(responsableToAdd);
                            addOrUpdateUserScope(newScope);
                        }
                }
        }
    }

    @Override
    public List<Responsable> getPotentialUserScope() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<UserScope> getUserScope() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        if (responsable != null) {
            checkUserScope(responsable);
            List<UserScope> userScope = userScopeRepository.findByResponsable(responsable);
            if (userScope != null && userScope.size() > 0)
                return userScope;

            UserScope defaultScope = new UserScope();
            defaultScope.setResponsable(responsable);
            defaultScope.setResponsableViewed(responsable);

            return Arrays.asList(addOrUpdateUserScope(defaultScope));
        }
        return null;
    }

    private void checkUserScope(Responsable responsable) {
        List<Responsable> potentialScope = getPotentialUserScope();
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

    @Override
    public List<Responsable> getUserCurrentScopeResponsables() {
        List<UserScope> currentScopes = getUserScope();
        List<Responsable> responsables = new ArrayList<Responsable>();
        for (UserScope userScope : currentScopes)
            responsables.add(userScope.getResponsableViewed());

        return responsables;
    }

    @Override
    public void authenticateUser(Responsable responsable, HttpServletRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(responsable.getId(), null,
                AuthorityUtils.createAuthorityList(ActiveDirectoryHelper.MYJSS_USER_GROUP));

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        responsable.setLoginTokenExpirationDateTime(LocalDateTime.now().minusSeconds(1));
        responsableService.addOrUpdateResponsable(responsable);
    }
}
