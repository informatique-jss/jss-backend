package com.jss.osiris.libs;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.profile.service.EmployeeService;

@Service
public class ActiveDirectoryHelper {
    @Value("${dev.mode}")
    private Boolean devMode;

    @Value("${ldap.server.host}")
    private String ldapServerHost;

    @Value("${ldap.dc.level.0}")
    private String ldapDcLevel0;

    @Value("${ldap.dc.level.1}")
    private String ldapDcLevel1;

    @Value("${ldap.server.port}")
    private String ldapServerPort;

    @Value("${ldap.manager.login}")
    private String ldapManagerLogin;

    @Value("${ldap.manager.password}")
    private String ldapManagerPassword;

    @Value("${ldap.group.jss.users}")
    private String ldapGroupJssUsers;

    @Autowired
    EmployeeService employeeService;

    public static final String OSIRIS_USERS = "@activeDirectoryHelper.isUserHasGroup('"
            + ActiveDirectoryHelper.OSIRIS_USERS_GROUP + "')";
    public static final String ADMINISTRATEUR = "@activeDirectoryHelper.isUserHasGroup('"
            + ActiveDirectoryHelper.ADMINISTRATEUR_GROUP + "')";
    public static final String ACCOUNTING = "@activeDirectoryHelper.isUserHasGroup('"
            + ActiveDirectoryHelper.ACCOUNTING_GROUP + "')";
    public static final String ACCOUNTING_RESPONSIBLE = "@activeDirectoryHelper.isUserHasGroup('"
            + ActiveDirectoryHelper.ACCOUNTING_RESPONSIBLE_GROUP + "')";
    public static final String ADMINISTRATEUR_GROUP = "ROLE_OSIRIS_ADMINISTRATEURS";
    public static final String OSIRIS_USERS_GROUP = "ROLE_OSIRIS_UTILISATEURS";
    public static final String ACCOUNTING_GROUP = "ROLE_OSIRIS_COMPTABILITÉ";
    public static final String ACCOUNTING_RESPONSIBLE_GROUP = "ROLE_OSIRIS_RESPONSABLE_COMPTABILITÉ";

    public String getCurrentUsername() {
        if (devMode)
            if (SecurityContextHolder.getContext() == null
                    || SecurityContextHolder.getContext().getAuthentication() == null
                    || SecurityContextHolder.getContext().getAuthentication().getName() == null
                    || SecurityContextHolder.getContext().getAuthentication().getName().toUpperCase()
                            .equals("ANONYMOUSUSER"))
                return "COANET";
        if (SecurityContextHolder.getContext() == null
                || SecurityContextHolder.getContext().getAuthentication() == null
                || SecurityContextHolder.getContext().getAuthentication().getName() == null)
            return "OSIRIS";
        return SecurityContextHolder.getContext().getAuthentication().getName().toUpperCase();
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
        if (SecurityContextHolder.getContext().getAuthentication() != null)
            return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        return null;
    }

    public boolean isUserHasGroup(String groupNeeded) {
        if (devMode) {
            return true;
        }
        if (getUserRoles() != null)
            for (GrantedAuthority s : getUserRoles()) {
                if (s.getAuthority().equals(groupNeeded) || s.getAuthority().equals("ROLE_OSIRIS_ADMINISTRATEURS")) {
                    return true;
                }
            }
        return false;
    }

    public List<Employee> getActiveDirectoryEmployees() {
        // Using Java 8 lambda expressions
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl("ldap://" + ldapServerHost + ":" + ldapServerPort);
        contextSource.setBase("OU=" + ldapGroupJssUsers + ",DC=" + ldapDcLevel1 + ",DC=" + ldapDcLevel0);
        contextSource.setUserDn(ldapManagerLogin);
        contextSource.setPassword(ldapManagerPassword);
        contextSource.afterPropertiesSet();

        return new LdapTemplate(contextSource).search(query().where("objectclass").is("user"), new Employee());
    }
}
