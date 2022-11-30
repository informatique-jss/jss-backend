package com.jss.osiris;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.exception.OsirisException;

@Service
public class ApplicationPropertiesAudit {
    @Autowired
    private Environment env;

    @PostConstruct
    public void checkApplicationProperty() throws OsirisException {
        boolean isOk = true;
        isOk = isOk && checkProperty("accounting.account.number.customer");
        isOk = isOk && checkProperty("accounting.account.number.deposit");
        isOk = isOk && checkProperty("accounting.account.number.provider");
        isOk = isOk && checkProperty("accounting.account.number.product");
        isOk = isOk && checkProperty("accounting.account.number.charge");
        isOk = isOk && checkProperty("accounting.account.number.bank");
        isOk = isOk && checkProperty("accounting.account.number.waiting");
        isOk = isOk && checkProperty("accounting.account.number.profit");
        isOk = isOk && checkProperty("accounting.account.number.lost");
        isOk = isOk && checkProperty("accounting.account.number.bank");
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
        isOk = isOk && checkProperty("schedulling.payment.grab");
        isOk = isOk && checkProperty("schedulling.mail.sender");
        isOk = isOk && checkProperty("schedulling.notification.purge");
        isOk = isOk && checkProperty("schedulling.log.osiris.quotation.reminder");
        isOk = isOk && checkProperty("schedulling.log.osiris.customerOrder.deposit.reminder");
        isOk = isOk && checkProperty("schedulling.log.osiris.customerOrder.invoice.reminder");
        isOk = isOk && checkProperty("ldap.dc.level.0");
        isOk = isOk && checkProperty("ldap.dc.level.1");
        isOk = isOk && checkProperty("ldap.ou.osiris");
        isOk = isOk && checkProperty("ldap.group.osiris.users");
        isOk = isOk && checkProperty("ldap.group.jss.users");
        isOk = isOk && checkProperty("ldap.server.host");
        isOk = isOk && checkProperty("ldap.server.port");
        isOk = isOk && checkProperty("ldap.manager.login");
        isOk = isOk && checkProperty("ldap.manager.password");
        isOk = isOk && checkProperty("server.servlet.session.timeout");
        isOk = isOk && checkProperty("invoicing.payment.limit.refund.euros");
        isOk = isOk && checkProperty("payment.cb.entry.point");
        isOk = isOk && checkProperty("payment.cb.redirect.quotation.deposit.entry.point");
        isOk = isOk && checkProperty("payment.cb.redirect.deposit.entry.point");
        isOk = isOk && checkProperty("payment.cb.redirect.invoice.entry.point");
        isOk = isOk && checkProperty("central.pay.entrypoint");
        isOk = isOk && checkProperty("central.pay.api.key");
        isOk = isOk && checkProperty("central.pay.api.password");

        isOk = isOk && checkAccountingParams();

        if (!isOk)
            System.exit(-1);
    }

    private boolean checkProperty(String propertyName) throws OsirisException {
        String property = env.getProperty(propertyName);
        if (property == null) {
            throw new OsirisException("Unable to find " + propertyName + " property in app properties");
        }
        return true;
    }

    private boolean checkAccountingParams() throws OsirisException {
        boolean isOk = true;
        isOk = isOk && checkProperty("brut.capital.souscrit.non.appele");
        isOk = isOk && checkProperty("brut.immobilisations.incorporelles.frais.etablissement");
        isOk = isOk && checkProperty("brut.immobilisations.incorporelles.frais.de.recherche");
        isOk = isOk && checkProperty("brut.immobilisations.incorporelles.concessions.brevets");
        isOk = isOk && checkProperty("brut.immobilisations.incorporelles.fonds.commercial");
        isOk = isOk && checkProperty("brut.immobilisations.incorporelles.autres");
        isOk = isOk && checkProperty("brut.immobilisations.incorporelles.avances.et.acomptes");
        isOk = isOk && checkProperty("brut.immobilisations.corporelles.terrains");
        isOk = isOk && checkProperty("brut.immobilisations.corporelles.constructions");
        isOk = isOk && checkProperty("brut.immobilisations.corporelles.installations.techniques.materiel");
        isOk = isOk && checkProperty("brut.immobilisations.corporelles.autres");
        isOk = isOk && checkProperty("brut.immobilisations.corporelles.immobilisations.corporelles.en.cours");
        isOk = isOk && checkProperty("brut.immobilisations.corporelles.avances.et.acomptes");
        isOk = isOk && checkProperty("brut.immobilisations.financieres.participations");
        isOk = isOk && checkProperty("brut.immobilisations.financieres.creances.rattachees.a.des.participations");
        isOk = isOk && checkProperty("brut.immobilisations.financieres.autres.titres.immobilises");
        isOk = isOk && checkProperty("brut.immobilisations.financieres.prets");
        isOk = isOk && checkProperty("brut.immobilisations.financieres.autres");
        isOk = isOk && checkProperty("brut.stocks.et.en-cours.matieres.premieres.et.autres.approvisionnements");
        isOk = isOk && checkProperty("brut.stocks.et.en-cours.en-cours.de.production");
        isOk = isOk && checkProperty("brut.stocks.et.en-cours.produits.intermediaires.et.finis");
        isOk = isOk && checkProperty("brut.stocks.et.en-cours.marchandises");
        isOk = isOk && checkProperty("brut.stocks.et.en-cours.avances.et.acomptes");
        isOk = isOk && checkProperty("brut.creances.creances.clients.et.comptes.rattaches");
        isOk = isOk && checkProperty("brut.creances.autres");
        isOk = isOk && checkProperty("brut.creances.capital.souscrit.appele.non.verse");
        isOk = isOk && checkProperty("brut.valeurs.mobilieres.de.placement.actions.propres");
        isOk = isOk && checkProperty("brut.valeurs.mobilieres.de.placement.autres.titres");
        isOk = isOk && checkProperty("brut.valeurs.mobilieres.de.placement.instruments.de.tresorerie");
        isOk = isOk && checkProperty("brut.valeurs.mobilieres.de.placement.disponibilites");
        isOk = isOk && checkProperty("brut.valeurs.mobilieres.de.placement.charges.constatees.avance");
        isOk = isOk && checkProperty("provision.immobilisations.incorporelles.frais.etablissement");
        isOk = isOk && checkProperty("provision.immobilisations.incorporelles.frais.de.recherche");
        isOk = isOk && checkProperty("provision.immobilisations.incorporelles.concessions.brevets");
        isOk = isOk && checkProperty("provision.immobilisations.incorporelles.fonds.commercial");
        isOk = isOk && checkProperty("provision.immobilisations.incorporelles.autres");
        isOk = isOk && checkProperty("provision.immobilisations.corporelles.terrains");
        isOk = isOk && checkProperty("provision.immobilisations.corporelles.constructions");
        isOk = isOk && checkProperty("provision.immobilisations.corporelles.installations.techniques.materiel");
        isOk = isOk && checkProperty("provision.immobilisations.corporelles.autres");
        isOk = isOk && checkProperty("provision.immobilisations.corporelles.immobilisations.corporelles.en.cours");
        isOk = isOk && checkProperty("provision.immobilisations.financieres.participations");
        isOk = isOk && checkProperty("provision.immobilisations.financieres.creances.rattachees.a.des.participations");
        isOk = isOk && checkProperty("provision.immobilisations.financieres.autres.titres.immobilises");
        isOk = isOk && checkProperty("provision.immobilisations.financieres.prets");
        isOk = isOk && checkProperty("provision.immobilisations.financieres.autres");
        isOk = isOk && checkProperty("provision.stocks.et.en-cours.matieres.premieres.et.autres.approvisionnements");
        isOk = isOk && checkProperty("provision.stocks.et.en-cours.en-cours.de.production");
        isOk = isOk && checkProperty("provision.stocks.et.en-cours.produits.intermediaires.et.finis");
        isOk = isOk && checkProperty("provision.stocks.et.en-cours.marchandises");
        isOk = isOk && checkProperty("provision.creances.creances.clients.et.comptes.rattaches");
        isOk = isOk && checkProperty("provision.creances.autres");
        isOk = isOk && checkProperty("provision.valeurs.mobilieres.de.placement.autres.titres");
        isOk = isOk && checkProperty("charges.a.repartir.sur.plusieurs.exercices");
        isOk = isOk && checkProperty("primes.de.remboursement.des.obligations");
        isOk = isOk && checkProperty("ecarts.de.conversion.actif");
        isOk = isOk && checkProperty("capital");
        isOk = isOk && checkProperty("primes.emission.fusion.apport");
        isOk = isOk && checkProperty("ecarts.de.reevaluation");
        isOk = isOk && checkProperty("ecarts.equivalence");
        isOk = isOk && checkProperty("reserve.legale");
        isOk = isOk && checkProperty("reserves.statutaires.ou.contractuelles");
        isOk = isOk && checkProperty("reserves.reglementees");
        isOk = isOk && checkProperty("autres");
        isOk = isOk && checkProperty("report.a.nouveau");
        isOk = isOk && checkProperty("resultat.exercice");
        isOk = isOk && checkProperty("subventions.investissement");
        isOk = isOk && checkProperty("provisions.reglementees");
        isOk = isOk && checkProperty("produit.des.emissions.de.titres.participatifs");
        isOk = isOk && checkProperty("avances.conditionnees");
        isOk = isOk && checkProperty("provisions.pour.risques");
        isOk = isOk && checkProperty("provisions.pour.charges");
        isOk = isOk && checkProperty("emprunts.obligataires.convertibles");
        isOk = isOk && checkProperty("autres.emprunts.obligataires");
        isOk = isOk && checkProperty("emprunts.et.dettes.aupres.des.etablissements.de.credit");
        isOk = isOk && checkProperty("emprunts.et.dettes.financieres.divers");
        isOk = isOk && checkProperty("avances.et.acomptes.recus.sur.commandes.en.cours");
        isOk = isOk && checkProperty("dettes.fournisseurs.et.comptes.rattaches");
        isOk = isOk && checkProperty("dettes.fiscales.et.sociales");
        isOk = isOk && checkProperty("dettes.sur.immobilisations.et.comptes.rattaches");
        isOk = isOk && checkProperty("autres.dettes");
        isOk = isOk && checkProperty("produits.constates.avance");
        isOk = isOk && checkProperty("ecarts.de.conversion.passif");
        isOk = isOk && checkProperty("achats.de.marchandises");
        isOk = isOk && checkProperty("variation.de.stocks.de.marchandises");
        isOk = isOk && checkProperty("achats.stockes.matiere.premieres");
        isOk = isOk && checkProperty("achats.stockes.autres.approv");
        isOk = isOk && checkProperty("variation.de.stock.approv");
        isOk = isOk && checkProperty("achats.de.sous.traitance");
        isOk = isOk && checkProperty("achats.non.stockes.de.matieres.et.fourn");
        isOk = isOk && checkProperty("sce.exterieurs.personnel.interim");
        isOk = isOk && checkProperty("sce.exterieurs.loyers.credit.bail");
        isOk = isOk && checkProperty("sce.exterieurs.autres");
        isOk = isOk && checkProperty("impots.sur.remunerations");
        isOk = isOk && checkProperty("impots.autres");
        isOk = isOk && checkProperty("salaires.et.traitements");
        isOk = isOk && checkProperty("charges.sociales");
        isOk = isOk && checkProperty("sur.immo.amortissements");
        isOk = isOk && checkProperty("sur.immo.provisions");
        isOk = isOk && checkProperty("sur.actif.circulant.provisions");
        isOk = isOk && checkProperty("pour.risques.et.charges.provisions");
        isOk = isOk && checkProperty("autres.charges");
        isOk = isOk && checkProperty("dotations.aux.amort.et.provisions");
        isOk = isOk && checkProperty("interets.et.charges.assimiles");
        isOk = isOk && checkProperty("difference.negatives.de.change");
        isOk = isOk && checkProperty("charges.nettes.sur.cessions.de.vmp");
        isOk = isOk && checkProperty("charges.sur.operation.de.gestion");
        isOk = isOk && checkProperty("sur.operation.en.capital.valeur.comptale.des.elements.cedes");
        isOk = isOk && checkProperty("sur.operation.en.capital.autres");
        isOk = isOk && checkProperty("dotations.aux.amortissements.et.aux.provisions");
        isOk = isOk && checkProperty("impot.sur.les.benefices");
        isOk = isOk && checkProperty("vente.des.marchandises.vendues");
        isOk = isOk && checkProperty("ventes");
        isOk = isOk && checkProperty("travaux");
        isOk = isOk && checkProperty("prestations.de.services");
        isOk = isOk && checkProperty("en.cours.de.production.de.biens");
        isOk = isOk && checkProperty("en.cours.de.production.de.services");
        isOk = isOk && checkProperty("produits");
        isOk = isOk && checkProperty("produtction.immobilisee");
        isOk = isOk && checkProperty("subventions.exploitation");
        isOk = isOk && checkProperty("reprise.sur.amort.et.prov");
        isOk = isOk && checkProperty("transfert.de.charges");
        isOk = isOk && checkProperty("autres.produits");
        isOk = isOk && checkProperty("des.participations");
        isOk = isOk && checkProperty("des.vmp.et.creances");
        isOk = isOk && checkProperty("autres.interets.et.produits");
        isOk = isOk && checkProperty("reprise.sur.prov.et.transferts.charges");
        isOk = isOk && checkProperty("difference.positive.de.change");
        isOk = isOk && checkProperty("produits.nets.sur.cession.vmp");
        isOk = isOk && checkProperty("produit.sur.operation.de.gestion");
        isOk = isOk && checkProperty("produit.de.cession.elements.actifs");
        isOk = isOk && checkProperty("subventions.investissement.virees");
        isOk = isOk && checkProperty("produits.autres");
        isOk = isOk && checkProperty("neutralisation.amortissements");
        isOk = isOk && checkProperty("reprise.sur.prov.et.transferts.de.charges");
        isOk = isOk && checkProperty("produits.net.partiels.operations.long.terme");
        isOk = isOk && checkProperty("participation.salaries");
        isOk = isOk && checkProperty("provision.pour.impots");

        return isOk;
    }

}
