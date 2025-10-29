package com.jss.osiris.modules.myjss.profile.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Service
public class UserScopeServiceImpl implements UserScopeService {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    ResponsableService responsableService;

    @Override
    public List<Responsable> getPotentialUserScope() {
        Responsable responsable = employeeService.getCurrentMyJssUser();
        List<Responsable> responsables = responsableService.getResponsableByMail(responsable.getMail());
        if (responsables != null)
            return responsables.stream().filter(r -> Boolean.TRUE.equals(r.getIsActive())).toList();
        return null;
    }

    @Override
    public List<Responsable> getPotentialUserScope(Responsable responsable) {
        if (responsable != null) {
            List<Integer> potentialUserScopeIds = employeeService.getPotentialUserScope(responsable.getMail().getId());
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
    public void authenticateUser(Responsable responsable, HttpServletRequest request) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(responsable.getId(), null,
                AuthorityUtils.createAuthorityList(ActiveDirectoryHelper.MYJSS_USER_GROUP));

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

        responsable = responsableService.getResponsable(responsable.getId());
        responsable.setLoginTokenExpirationDateTime(LocalDateTime.now().minusSeconds(1));
        responsableService.addOrUpdateResponsable(responsable);
    }
}
