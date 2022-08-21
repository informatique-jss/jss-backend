package com.jss.osiris.libs;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.profile.service.EmployeeService;

@Service
public class ActiveDirectoryHelper {
    @Autowired
    EmployeeService employeeService;

    @Value("${dev.mode}")
    private Boolean devMode;

    public static final String ADMINISTRATEUR = "@activeDirectoryHelper.isUserHasGroup('ROLE_OSIRIS_ADMINISTRATEURS')";

    public String getCurrentUsername() {
        if (devMode)
            if (SecurityContextHolder.getContext() == null
                    || SecurityContextHolder.getContext().getAuthentication() == null
                    || SecurityContextHolder.getContext().getAuthentication() == null
                    || SecurityContextHolder.getContext().getAuthentication().getName() == null)
                return "gapin";
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Collection<? extends GrantedAuthority> getUserRoles() {
        if (devMode)
            if (SecurityContextHolder.getContext() == null
                    || SecurityContextHolder.getContext().getAuthentication() == null
                    || SecurityContextHolder.getContext().getAuthentication() == null
                    || SecurityContextHolder.getContext().getAuthentication().getName() == null
                    || SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
                GrantedAuthority authority = new GrantedAuthority() {
                    @Override
                    public String getAuthority() {
                        return "ROLE_OSIRIS_ADMINISTRATEURS";
                    }
                };
                return Arrays.asList(authority);
            }
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

    public boolean isUserHasGroup(String groupNeeded) {
        if (devMode) {
            return true;
        }
        for (GrantedAuthority s : getUserRoles()) {
            if (s.getAuthority().equals(groupNeeded) || s.getAuthority().equals("ROLE_OSIRIS_ADMINISTRATEURS")) {
                return true;
            }
        }
        return false;
    }

}
