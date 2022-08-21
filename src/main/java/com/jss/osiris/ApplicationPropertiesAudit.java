package com.jss.osiris;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class ApplicationPropertiesAudit {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationPropertiesAudit.class);

    @Autowired
    private Environment env;

    @PostConstruct
    public void checkApplicationProperty() {
        boolean isOk = true;
        isOk = isOk && checkProperty("accounting.vat.code.twenty");
        isOk = isOk && checkProperty("accounting.vat.code.eight");
        isOk = isOk && checkProperty("miscellaneous.country.code.france");
        isOk = isOk && checkProperty("miscellaneous.country.code.monaco");
        isOk = isOk && checkProperty("miscellaneous.department.code.martinique");
        isOk = isOk && checkProperty("miscellaneous.department.code.guadeloupe");
        isOk = isOk && checkProperty("miscellaneous.department.code.reunion");
        isOk = isOk && checkProperty("accounting.account.number.customer");
        isOk = isOk && checkProperty("accounting.account.number.provider");
        isOk = isOk && checkProperty("accounting.account.number.product");
        isOk = isOk && checkProperty("miscellaneous.document.code.billing");
        isOk = isOk && checkProperty("miscellaneous.document.billing.label.type.customer.code");
        isOk = isOk && checkProperty("miscellaneous.document.billing.label.type.affaire.code");
        isOk = isOk && checkProperty("miscellaneous.document.billing.label.type.other.code");
        isOk = isOk && checkProperty("accounting.journal.code.sales");
        isOk = isOk && checkProperty("schedulling.account.daily.close");
        isOk = isOk && checkProperty("ldap.dc.level.0");
        isOk = isOk && checkProperty("ldap.dc.level.1");
        isOk = isOk && checkProperty("ldap.ou.osiris");
        isOk = isOk && checkProperty("ldap.group.osiris.users");
        isOk = isOk && checkProperty("ldap.server.host");
        isOk = isOk && checkProperty("ldap.server.port");
        isOk = isOk && checkProperty("ldap.manager.login");
        isOk = isOk && checkProperty("ldap.manager.password");
        isOk = isOk && checkProperty("server.servlet.session.timeout");

        if (!isOk)
            System.exit(-1);
    }

    private boolean checkProperty(String propertyName) {
        String property = env.getProperty(propertyName);
        if (property == null) {
            logger.error(
                    "Unable to find " + propertyName + " property in app properties");
            return false;
        }
        return true;
    }

}
