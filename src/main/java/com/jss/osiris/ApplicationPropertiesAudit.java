package com.jss.osiris;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;

import jakarta.annotation.PostConstruct;

@Service
public class ApplicationPropertiesAudit {
    @Autowired
    private Environment env;

    @PostConstruct
    public void checkApplicationProperty() throws OsirisException {
        boolean isOk = true;
        isOk = isOk && checkProperty("mail.smtp.host");
        isOk = isOk && checkProperty("mail.smtp.port");
        isOk = isOk && checkProperty("mail.smtp.username");
        isOk = isOk && checkProperty("mail.smtp.password");
        isOk = isOk && checkProperty("mail.smtp.auth");
        isOk = isOk && checkProperty("mail.smtp.starttls.enable");
        isOk = isOk && checkProperty("schedulling.enabled");
        isOk = isOk && checkProperty("schedulling.pool.size");
        isOk = isOk && checkProperty("schedulling.account.daily.close");
        isOk = isOk && checkProperty("schedulling.active.directory.user.update");
        isOk = isOk && checkProperty("schedulling.mail.sender");
        isOk = isOk && checkProperty("schedulling.notification.purge");
        isOk = isOk && checkProperty("schedulling.log.osiris.quotation.reminder");
        isOk = isOk && checkProperty("schedulling.log.osiris.customerOrder.deposit.reminder");
        isOk = isOk && checkProperty("schedulling.log.osiris.customerOrder.invoice.reminder");
        isOk = isOk && checkProperty("schedulling.account.receipt.generation.sender");
        isOk = isOk && checkProperty("schedulling.announcement.publish.actu.legale");
        isOk = isOk && checkProperty("schedulling.announcement.publication.flag");
        isOk = isOk && checkProperty("schedulling.competant.authorities.update");
        isOk = isOk && checkProperty("schedulling.log.osiris.announcement.confrere.query.reminder");
        isOk = isOk && checkProperty("schedulling.log.osiris.customer.proof.reading.reminder");
        isOk = isOk && checkProperty("schedulling.customer.order.recurring.generation");
        isOk = isOk && checkProperty("schedulling.log.osiris.competent.authority.reminder");
        isOk = isOk && checkProperty("schedulling.guichet.unique.refresh.update.last.hour");
        isOk = isOk && checkProperty("schedulling.guichet.unique.refresh.opened");
        isOk = isOk && checkProperty("schedulling.central.pay.payment.request.validation.check");
        isOk = isOk && checkProperty("azure.form.recognizer.invoice.check");
        isOk = isOk && checkProperty("ldap.dc.level.0");
        isOk = isOk && checkProperty("ldap.dc.level.1");
        isOk = isOk && checkProperty("ldap.ou.osiris");
        isOk = isOk && checkProperty("ldap.group.osiris.users");
        isOk = isOk && checkProperty("ldap.group.jss.users");
        isOk = isOk && checkProperty("ldap.server.host");
        isOk = isOk && checkProperty("ldap.server.port");
        isOk = isOk && checkProperty("ldap.manager.login");
        isOk = isOk && checkProperty("ldap.manager.password");
        isOk = isOk && checkProperty("spring.session.store-type");
        isOk = isOk && checkProperty("spring.session.jdbc.initialize-schema");
        isOk = isOk && checkProperty("spring.session.jdbc.schema");
        isOk = isOk && checkProperty("server.servlet.session.timeout");
        isOk = isOk && checkProperty("server.servlet.session.timeout");
        isOk = isOk && checkProperty("invoicing.payment.limit.refund.euros");
        isOk = isOk && checkProperty("payment.cb.entry.point");
        isOk = isOk && checkProperty("central.pay.entrypoint");
        isOk = isOk && checkProperty("central.pay.api.key");
        isOk = isOk && checkProperty("central.pay.api.password");
        isOk = isOk && checkProperty("actu.legale.auth.entry.point");
        isOk = isOk && checkProperty("actu.legale.is.test");
        isOk = isOk && checkProperty("actu.legale.auth.username");
        isOk = isOk && checkProperty("actu.legale.auth.password");
        isOk = isOk && checkProperty("actu.legale.auth.token");
        isOk = isOk && checkProperty("actu.legale.publish.entry.point");
        isOk = isOk && checkProperty("actu.legale.publish.newpapper.id");
        isOk = isOk && checkProperty("fr.gouv.etablissements.publics.api.entry.point");
        isOk = isOk && checkProperty("fr.gouv.geo.api.entry.point");
        isOk = isOk && checkProperty("printer.label.ip");
        isOk = isOk && checkProperty("printer.label.second.ip");
        isOk = isOk && checkProperty("printer.label.port");
        isOk = isOk && checkProperty("jss.sepa.identification");
        isOk = isOk && checkProperty("jss.bic");
        isOk = isOk && checkProperty("jss.iban");
        isOk = isOk && checkProperty("guichet.unique.entry.point");
        isOk = isOk && checkProperty("guichet.unique.login");
        isOk = isOk && checkProperty("guichet.unique.password");
        isOk = isOk && checkProperty("guichet.unique.rne.login");
        isOk = isOk && checkProperty("guichet.unique.rne.password");
        isOk = isOk && checkProperty("guichet.unique.signature.path.jar");
        isOk = isOk && checkProperty("guichet.unique.signature.path.configuration");
        isOk = isOk && checkProperty("guichet.unique.signature.path.input");
        isOk = isOk && checkProperty("guichet.unique.signature.path.output");
        isOk = isOk && checkProperty("guichet.unique.wallet.login");
        isOk = isOk && checkProperty("guichet.unique.wallet.password");
        isOk = isOk && checkProperty("schedulling.payment.automatch");
        isOk = isOk && checkProperty("azure.form.recognizer.endpoint");
        isOk = isOk && checkProperty("azure.form.recognizer.api.key");
        isOk = isOk && checkProperty("azure.form.recognizer.model.invoices.name");
        isOk = isOk && checkProperty("azure.form.recognizer.model.receipts.name");
        isOk = isOk && checkProperty("azure.form.recognizer.confidence.threshold");
        isOk = isOk && checkProperty("azure.translator.api.key");
        isOk = isOk && checkProperty("azure.translator.api.endpoint");
        isOk = isOk && checkProperty("azure.translator.api.route");
        isOk = isOk && checkProperty("azure.translator.api.region");
        isOk = isOk && checkProperty("schedulling.audit.clean");
        isOk = isOk && checkProperty("schedulling.log.purge");
        isOk = isOk && checkProperty("schedulling.batch.purge");
        isOk = isOk && checkProperty("schedulling.node.priority");
        isOk = isOk && checkProperty("mail.temporized.temporization.seconds");
        isOk = isOk && checkProperty("schedulling.log.osiris.customer.bilan.publication.reminder");
        isOk = isOk && checkProperty("schedulling.log.osiris.customer.missing.attachment.queries");
        isOk = isOk && checkProperty("infogreffe.auth.entry.point");
        isOk = isOk && checkProperty("infogreffe.auth.login");
        isOk = isOk && checkProperty("infogreffe.auth.password");
        isOk = isOk && checkProperty("infogreffe.auth.client.id");
        isOk = isOk && checkProperty("infogreffe.auth.grant.type");
        isOk = isOk && checkProperty("infogreffe.url");
        isOk = isOk && checkProperty("schedulling.infogreffe.refresh.all");
        isOk = isOk && checkProperty("schedulling.infogreffe.refresh.last.day");
        isOk = isOk && checkProperty("login.token.entry.point");
        isOk = isOk && checkProperty("mail.imap.host");
        isOk = isOk && checkProperty("mail.imap.port");
        isOk = isOk && checkProperty("mail.imap.username");
        isOk = isOk && checkProperty("mail.imap.password");
        isOk = isOk && checkProperty("mail.imap.auth");
        isOk = isOk && checkProperty("mail.imap.ssl.enable");
        isOk = isOk && checkProperty("mail.imap.auth.mechanisms");
        isOk = isOk && checkProperty("mail.imaps.sasl.mechanisms");
        isOk = isOk && checkProperty("mail.imap.app.id");
        isOk = isOk && checkProperty("mail.imap.tenant.id");
        isOk = isOk && checkProperty("mail.imap.secret.value");
        isOk = isOk && checkProperty("mail.imap.secret.id");
        isOk = isOk && checkProperty("mail.imap.tls.version");
        isOk = isOk && checkProperty("schedulling.mail.purge.indexation");
        isOk = isOk && checkProperty("schedulling.indicator.compute");

        isOk = isOk && checkProperty("mail.imap.host");
        isOk = isOk && checkProperty("mail.imap.port");
        isOk = isOk && checkProperty("mail.imap.username");
        isOk = isOk && checkProperty("mail.imap.password");
        isOk = isOk && checkProperty("mail.imap.auth");
        isOk = isOk && checkProperty("mail.imap.ssl.enable");
        isOk = isOk && checkProperty("mail.imap.auth.mechanisms");
        isOk = isOk && checkProperty("mail.imap.app.id");
        isOk = isOk && checkProperty("mail.imap.tenant.id");
        isOk = isOk && checkProperty("mail.imap.secret.value");
        isOk = isOk && checkProperty("mail.imap.secret.id");
        isOk = isOk && checkProperty("schedulling.mail.automatic.indexation");
        isOk = isOk && checkProperty("outlook.default.url");
        isOk = isOk && checkProperty("microsoft.host");

        if (!isOk)
            System.exit(-1);
    }

    private boolean checkProperty(String propertyName) throws OsirisException {
        String property = env.getProperty(propertyName);
        if (property == null) {
            throw new OsirisException(null, "Unable to find " + propertyName + " property in app properties");
        }
        return true;
    }
}
