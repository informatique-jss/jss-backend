package com.jss.osiris.modules.osiris.accounting.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.accounting.model.AccountingBalanceBilan;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalanceViewItem;
import com.jss.osiris.modules.osiris.accounting.model.AccountingBalanceViewTitle;

@Service
public class AccountingBalanceHelper {

        @Value("${brut.capital.souscrit.non.appele}")
        private String brut_capital_souscrit_non_appele;
        @Value("${brut.immobilisations.incorporelles.frais.etablissement}")
        private String brut_immobilisations_incorporelles_frais_etablissement;
        @Value("${brut.immobilisations.incorporelles.frais.de.recherche}")
        private String brut_immobilisations_incorporelles_frais_de_recherche;
        @Value("${brut.immobilisations.incorporelles.concessions.brevets}")
        private String brut_immobilisations_incorporelles_concessions_brevets;
        @Value("${brut.immobilisations.incorporelles.fonds.commercial}")
        private String brut_immobilisations_incorporelles_fonds_commercial;
        @Value("${brut.immobilisations.incorporelles.autres}")
        private String brut_immobilisations_incorporelles_autres;
        @Value("${brut.immobilisations.incorporelles.avances.et.acomptes}")
        private String brut_immobilisations_incorporelles_avances_et_acomptes;
        @Value("${brut.immobilisations.corporelles.terrains}")
        private String brut_immobilisations_corporelles_terrains;
        @Value("${brut.immobilisations.corporelles.constructions}")
        private String brut_immobilisations_corporelles_constructions;
        @Value("${brut.immobilisations.corporelles.installations.techniques.materiel}")
        private String brut_immobilisations_corporelles_installations_techniques_materiel;
        @Value("${brut.immobilisations.corporelles.autres}")
        private String brut_immobilisations_corporelles_autres;
        @Value("${brut.immobilisations.corporelles.immobilisations.corporelles.en.cours}")
        private String brut_immobilisations_corporelles_immobilisations_corporelles_en_cours;
        @Value("${brut.immobilisations.corporelles.avances.et.acomptes}")
        private String brut_immobilisations_corporelles_avances_et_acomptes;
        @Value("${brut.immobilisations.financieres.participations}")
        private String brut_immobilisations_financieres_participations;
        @Value("${brut.immobilisations.financieres.creances.rattachees.a.des.participations}")
        private String brut_immobilisations_financieres_creances_rattachees_a_des_participations;
        @Value("${brut.immobilisations.financieres.autres.titres.immobilises}")
        private String brut_immobilisations_financieres_autres_titres_immobilises;
        @Value("${brut.immobilisations.financieres.prets}")
        private String brut_immobilisations_financieres_prets;
        @Value("${brut.immobilisations.financieres.autres}")
        private String brut_immobilisations_financieres_autres;
        @Value("${brut.stocks.et.en-cours.matieres.premieres.et.autres.approvisionnements}")
        private String brut_stocks_et_en_cours_matieres_premieres_et_autres_approvisionnements;
        @Value("${brut.stocks.et.en-cours.en-cours.de.production}")
        private String brut_stocks_et_en_cours_en_cours_de_production;
        @Value("${brut.stocks.et.en-cours.produits.intermediaires.et.finis}")
        private String brut_stocks_et_en_cours_produits_intermediaires_et_finis;
        @Value("${brut.stocks.et.en-cours.marchandises}")
        private String brut_stocks_et_en_cours_marchandises;
        @Value("${brut.stocks.et.en-cours.avances.et.acomptes}")
        private String brut_stocks_et_en_cours_avances_et_acomptes;
        @Value("${brut.creances.creances.clients.et.comptes.rattaches}")
        private String brut_creances_creances_clients_et_comptes_rattaches;
        @Value("${brut.creances.autres}")
        private String brut_creances_autres;
        @Value("${brut.creances.capital.souscrit.appele.non.verse}")
        private String brut_creances_capital_souscrit_appele_non_verse;
        @Value("${brut.valeurs.mobilieres.de.placement.actions.propres}")
        private String brut_valeurs_mobilieres_de_placement_actions_propres;
        @Value("${brut.valeurs.mobilieres.de.placement.autres.titres}")
        private String brut_valeurs_mobilieres_de_placement_autres_titres;
        @Value("${brut.valeurs.mobilieres.de.placement.instruments.de.tresorerie}")
        private String brut_valeurs_mobilieres_de_placement_instruments_de_tresorerie;
        @Value("${brut.valeurs.mobilieres.de.placement.disponibilites}")
        private String brut_valeurs_mobilieres_de_placement_disponibilites;
        @Value("${brut.valeurs.mobilieres.de.placement.charges.constatees.avance}")
        private String brut_valeurs_mobilieres_de_placement_charges_constatees_avance;
        @Value("${provision.immobilisations.incorporelles.frais.etablissement}")
        private String provision_immobilisations_incorporelles_frais_etablissement;
        @Value("${provision.immobilisations.incorporelles.frais.de.recherche}")
        private String provision_immobilisations_incorporelles_frais_de_recherche;
        @Value("${provision.immobilisations.incorporelles.concessions.brevets}")
        private String provision_immobilisations_incorporelles_concessions_brevets;
        @Value("${provision.immobilisations.incorporelles.fonds.commercial}")
        private String provision_immobilisations_incorporelles_fonds_commercial;
        @Value("${provision.immobilisations.incorporelles.autres}")
        private String provision_immobilisations_incorporelles_autres;
        @Value("${provision.immobilisations.corporelles.terrains}")
        private String provision_immobilisations_corporelles_terrains;
        @Value("${provision.immobilisations.corporelles.constructions}")
        private String provision_immobilisations_corporelles_constructions;
        @Value("${provision.immobilisations.corporelles.installations.techniques.materiel}")
        private String provision_immobilisations_corporelles_installations_techniques_materiel;
        @Value("${provision.immobilisations.corporelles.autres}")
        private String provision_immobilisations_corporelles_autres;
        @Value("${provision.immobilisations.corporelles.immobilisations.corporelles.en.cours}")
        private String provision_immobilisations_corporelles_immobilisations_corporelles_en_cours;
        @Value("${provision.immobilisations.financieres.participations}")
        private String provision_immobilisations_financieres_participations;
        @Value("${provision.immobilisations.financieres.creances.rattachees.a.des.participations}")
        private String provision_immobilisations_financieres_creances_rattachees_a_des_participations;
        @Value("${provision.immobilisations.financieres.autres.titres.immobilises}")
        private String provision_immobilisations_financieres_autres_titres_immobilises;
        @Value("${provision.immobilisations.financieres.prets}")
        private String provision_immobilisations_financieres_prets;
        @Value("${provision.immobilisations.financieres.autres}")
        private String provision_immobilisations_financieres_autres;
        @Value("${provision.stocks.et.en-cours.matieres.premieres.et.autres.approvisionnements}")
        private String provision_stocks_et_en_cours_matieres_premieres_et_autres_approvisionnements;
        @Value("${provision.stocks.et.en-cours.en-cours.de.production}")
        private String provision_stocks_et_en_cours_en_cours_de_production;
        @Value("${provision.stocks.et.en-cours.produits.intermediaires.et.finis}")
        private String provision_stocks_et_en_cours_produits_intermediaires_et_finis;
        @Value("${provision.stocks.et.en-cours.marchandises}")
        private String provision_stocks_et_en_cours_marchandises;
        @Value("${provision.creances.creances.clients.et.comptes.rattaches}")
        private String provision_creances_creances_clients_et_comptes_rattaches;
        @Value("${provision.creances.autres}")
        private String provision_creances_autres;
        @Value("${provision.valeurs.mobilieres.de.placement.autres.titres}")
        private String provision_valeurs_mobilieres_de_placement_autres_titres;
        @Value("${charges.a.repartir.sur.plusieurs.exercices}")
        private String charges_a_repartir_sur_plusieurs_exercices;
        @Value("${primes.de.remboursement.des.obligations}")
        private String primes_de_remboursement_des_obligations;
        @Value("${ecarts.de.conversion.actif}")
        private String ecarts_de_conversion_actif;
        @Value("${capital}")
        private String capital;
        @Value("${primes.emission.fusion.apport}")
        private String primes_emission_fusion_apport;
        @Value("${ecarts.de.reevaluation}")
        private String ecarts_de_reevaluation;
        @Value("${ecarts.equivalence}")
        private String ecarts_equivalence;
        @Value("${reserve.legale}")
        private String reserve_legale;
        @Value("${reserves.statutaires.ou.contractuelles}")
        private String reserves_statutaires_ou_contractuelles;
        @Value("${reserves.reglementees}")
        private String reserves_reglementees;
        @Value("${autres}")
        private String autres;
        @Value("${report.a.nouveau}")
        private String report_a_nouveau;
        @Value("${resultat.exercice}")
        private String resultat_exercice;
        @Value("${subventions.investissement}")
        private String subventions_investissement;
        @Value("${provisions.reglementees}")
        private String provisions_reglementees;
        @Value("${produit.des.emissions.de.titres.participatifs}")
        private String produit_des_emissions_de_titres_participatifs;
        @Value("${avances.conditionnees}")
        private String avances_conditionnees;
        @Value("${provisions.pour.risques}")
        private String provisions_pour_risques;
        @Value("${provisions.pour.charges}")
        private String provisions_pour_charges;
        @Value("${emprunts.obligataires.convertibles}")
        private String emprunts_obligataires_convertibles;
        @Value("${autres.emprunts.obligataires}")
        private String autres_emprunts_obligataires;
        @Value("${emprunts.et.dettes.aupres.des.etablissements.de.credit}")
        private String emprunts_et_dettes_aupres_des_etablissements_de_credit;
        @Value("${emprunts.et.dettes.financieres.divers}")
        private String emprunts_et_dettes_financieres_divers;
        @Value("${avances.et.acomptes.recus.sur.commandes.en.cours}")
        private String avances_et_acomptes_recus_sur_commandes_en_cours;
        @Value("${dettes.fournisseurs.et.comptes.rattaches}")
        private String dettes_fournisseurs_et_comptes_rattaches;
        @Value("${dettes.fiscales.et.sociales}")
        private String dettes_fiscales_et_sociales;
        @Value("${dettes.sur.immobilisations.et.comptes.rattaches}")
        private String dettes_sur_immobilisations_et_comptes_rattaches;
        @Value("${autres.dettes}")
        private String autres_dettes;
        @Value("${produits.constates.avance}")
        private String produits_constates_avance;
        @Value("${ecarts.de.conversion.passif}")
        private String ecarts_de_conversion_passif;
        @Value("${achats.de.marchandises}")
        private String achats_de_marchandises;
        @Value("${variation.de.stocks.de.marchandises}")
        private String variation_de_stocks_de_marchandises;
        @Value("${achats.stockes.matiere.premieres}")
        private String achats_stockes_matiere_premieres;
        @Value("${achats.stockes.autres.approv}")
        private String achats_stockes_autres_approv;
        @Value("${variation.de.stock.approv}")
        private String variation_de_stock_approv;
        @Value("${achats.de.sous.traitance}")
        private String achats_de_sous_traitance;
        @Value("${achats.non.stockes.de.matieres.et.fourn}")
        private String achats_non_stockes_de_matieres_et_fourn;
        @Value("${sce.exterieurs.personnel.interim}")
        private String sce_exterieurs_personnel_interim;
        @Value("${sce.exterieurs.loyers.credit.bail}")
        private String sce_exterieurs_loyers_credit_bail;
        @Value("${sce.exterieurs.autres}")
        private String sce_exterieurs_autres;
        @Value("${impots.sur.remunerations}")
        private String impots_sur_remunerations;
        @Value("${impots.autres}")
        private String impots_autres;
        @Value("${salaires.et.traitements}")
        private String salaires_et_traitements;
        @Value("${charges.sociales}")
        private String charges_sociales;
        @Value("${sur.immo.amortissements}")
        private String sur_immo_amortissements;
        @Value("${sur.immo.provisions}")
        private String sur_immo_provisions;
        @Value("${sur.actif.circulant.provisions}")
        private String sur_actif_circulant_provisions;
        @Value("${pour.risques.et.charges.provisions}")
        private String pour_risques_et_charges_provisions;
        @Value("${autres.charges}")
        private String autres_charges;
        @Value("${dotations.aux.amort.et.provisions}")
        private String dotations_aux_amort_et_provisions;
        @Value("${interets.et.charges.assimiles}")
        private String interets_et_charges_assimiles;
        @Value("${difference.negatives.de.change}")
        private String difference_negatives_de_change;
        @Value("${charges.nettes.sur.cessions.de.vmp}")
        private String charges_nettes_sur_cessions_de_vmp;
        @Value("${charges.sur.operation.de.gestion}")
        private String charges_sur_operation_de_gestion;
        @Value("${sur.operation.en.capital.valeur.comptale.des.elements.cedes}")
        private String sur_operation_en_capital_valeur_comptale_des_elements_cedes;
        @Value("${sur.operation.en.capital.autres}")
        private String sur_operation_en_capital_autres;
        @Value("${dotations.aux.amortissements.et.aux.provisions}")
        private String dotations_aux_amortissements_et_aux_provisions;
        @Value("${impot.sur.les.benefices}")
        private String impot_sur_les_benefices;
        @Value("${vente.des.marchandises.vendues}")
        private String vente_des_marchandises_vendues;
        @Value("${ventes}")
        private String ventes;
        @Value("${travaux}")
        private String travaux;
        @Value("${prestations.de.services}")
        private String prestations_de_services;
        @Value("${en.cours.de.production.de.biens}")
        private String en_cours_de_production_de_biens;
        @Value("${en.cours.de.production.de.services}")
        private String en_cours_de_production_de_services;
        @Value("${produits}")
        private String produits;
        @Value("${produtction.immobilisee}")
        private String produtction_immobilisee;
        @Value("${subventions.exploitation}")
        private String subventions_exploitation;
        @Value("${reprise.sur.amort.et.prov}")
        private String reprise_sur_amort_et_prov;
        @Value("${transfert.de.charges}")
        private String transfert_de_charges;
        @Value("${autres.produits}")
        private String autres_produits;
        @Value("${des.participations}")
        private String des_participations;
        @Value("${des.vmp.et.creances}")
        private String des_vmp_et_creances;
        @Value("${autres.interets.et.produits}")
        private String autres_interets_et_produits;
        @Value("${reprise.sur.prov.et.transferts.charges}")
        private String reprise_sur_prov_et_transferts_charges;
        @Value("${difference.positive.de.change}")
        private String difference_positive_de_change;
        @Value("${produits.nets.sur.cession.vmp}")
        private String produits_nets_sur_cession_vmp;
        @Value("${produit.sur.operation.de.gestion}")
        private String produit_sur_operation_de_gestion;
        @Value("${produit.de.cession.elements.actifs}")
        private String produit_de_cession_elements_actifs;
        @Value("${subventions.investissement.virees}")
        private String subventions_investissement_virees;
        @Value("${produits.autres}")
        private String produit_autres;
        @Value("${neutralisation.amortissements}")
        private String neutralisation_amortissements;
        @Value("${reprise.sur.prov.et.transferts.de.charges}")
        private String reprise_sur_prov_et_transferts_de_charges;
        @Value("${produits.net.partiels.operations.long.terme}")
        private String produits_net_partiels_operations_long_terme;
        @Value("${provision.pour.impots}")
        private String provision_pour_impots;
        @Value("${participation.salaries}")
        private String participation_salaries;

        private Float creditN = 0f;
        private Float amortissementN = 0f;
        private Float creditN1 = 0f;
        private Float amortissementN1 = 0f;

        private Float creditNbis = 0f;
        private Float amortissementNbis = 0f;
        private Float creditN1bis = 0f;
        private Float amortissementN1bis = 0f;

        public AccountingBalanceViewTitle getBilanActif(List<AccountingBalanceBilan> accountingRecords,
                        List<AccountingBalanceBilan> accountingRecordsN1) {

                creditN = 0f;
                amortissementN = 0f;
                creditN1 = 0f;
                amortissementN1 = 0f;

                creditNbis = 0f;
                amortissementNbis = 0f;
                creditN1bis = 0f;
                amortissementN1bis = 0f;

                AccountingBalanceViewTitle bilanActifTitle = new AccountingBalanceViewTitle();
                bilanActifTitle.setLabel("Actif");
                bilanActifTitle.setActifBilan(true);
                bilanActifTitle.setSubTitles(new ArrayList<AccountingBalanceViewTitle>());
                bilanActifTitle.setItems(new ArrayList<AccountingBalanceViewItem>());

                bilanActifTitle.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Capital souscrit non appelé",
                                                this.brut_capital_souscrit_non_appele, null,
                                                accountingRecords, accountingRecordsN1, false));

                // Title Immobilisations incorporelles
                AccountingBalanceViewTitle immobilisationsIncorporelles = new AccountingBalanceViewTitle();
                immobilisationsIncorporelles.setLabel("Immobilisations incorporelles");
                immobilisationsIncorporelles.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanActifTitle.getSubTitles().add(immobilisationsIncorporelles);

                immobilisationsIncorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Frais d'établissement",
                                                this.brut_immobilisations_incorporelles_frais_etablissement,
                                                this.provision_immobilisations_incorporelles_frais_etablissement,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsIncorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Frais de recherche et de développement",
                                                this.brut_immobilisations_incorporelles_frais_de_recherche,
                                                this.provision_immobilisations_incorporelles_frais_de_recherche,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsIncorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan(
                                                "Concessions, brevets, licences, marques, procédés, droits et valeurs similaires",
                                                this.brut_immobilisations_incorporelles_concessions_brevets,
                                                this.provision_immobilisations_incorporelles_concessions_brevets,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsIncorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Fonds commercial (dont droit au bail)",
                                                this.brut_immobilisations_incorporelles_fonds_commercial,
                                                this.provision_immobilisations_incorporelles_fonds_commercial,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsIncorporelles.getItems().add(getAccountingBalanceViewItemForActifBilan("Autres",
                                this.brut_immobilisations_incorporelles_autres,
                                this.provision_immobilisations_incorporelles_autres,
                                accountingRecords, accountingRecordsN1, false));

                immobilisationsIncorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Avances et acomptes",
                                                this.brut_immobilisations_incorporelles_avances_et_acomptes, null,
                                                accountingRecords, accountingRecordsN1, false));

                // Title Immobilisations corporelles
                AccountingBalanceViewTitle immobilisationsCorporelles = new AccountingBalanceViewTitle();
                immobilisationsCorporelles.setLabel("Immobilisations corporelles");
                immobilisationsCorporelles.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanActifTitle.getSubTitles().add(immobilisationsCorporelles);

                immobilisationsCorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Terrains",
                                                this.brut_immobilisations_corporelles_terrains,
                                                this.provision_immobilisations_corporelles_terrains,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsCorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Constructions",
                                                this.brut_immobilisations_corporelles_constructions,
                                                this.provision_immobilisations_corporelles_constructions,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsCorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan(
                                                "Installations techniques, matériel et outillage industriels",
                                                this.brut_immobilisations_corporelles_installations_techniques_materiel,
                                                this.provision_immobilisations_corporelles_installations_techniques_materiel,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsCorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Autres",
                                                this.brut_immobilisations_corporelles_autres,
                                                this.provision_immobilisations_corporelles_autres,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsCorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Immobilisations corporelles en cours",
                                                this.brut_immobilisations_corporelles_immobilisations_corporelles_en_cours,
                                                this.provision_immobilisations_corporelles_immobilisations_corporelles_en_cours,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsCorporelles.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Avances et acomptes",
                                                this.brut_immobilisations_corporelles_avances_et_acomptes, null,
                                                accountingRecords, accountingRecordsN1, false));

                // Title Immobilisations financières (dont à moins d'un an)
                AccountingBalanceViewTitle immobilisationsFinancieres = new AccountingBalanceViewTitle();
                immobilisationsFinancieres.setLabel("Immobilisations financières (dont à moins d'un an) ");
                immobilisationsFinancieres.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanActifTitle.getSubTitles().add(immobilisationsFinancieres);

                immobilisationsFinancieres.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Participations",
                                                this.brut_immobilisations_financieres_participations,
                                                this.provision_immobilisations_financieres_participations,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsFinancieres.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan(
                                                "Créances rattachées à des participations",
                                                this.brut_immobilisations_financieres_creances_rattachees_a_des_participations,
                                                this.provision_immobilisations_financieres_creances_rattachees_a_des_participations,
                                                accountingRecords, accountingRecordsN1, false));

                immobilisationsFinancieres.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Autres titres immobilisés",
                                                this.brut_immobilisations_financieres_autres_titres_immobilises,
                                                this.provision_immobilisations_financieres_autres_titres_immobilises,
                                                accountingRecords,
                                                accountingRecordsN1, false));

                bilanActifTitle.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Prêts",
                                                this.brut_immobilisations_financieres_prets,
                                                this.provision_immobilisations_financieres_prets, accountingRecords,
                                                accountingRecordsN1,
                                                false));

                bilanActifTitle.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Autres",
                                                this.brut_immobilisations_financieres_autres,
                                                this.provision_immobilisations_financieres_autres, accountingRecords,
                                                accountingRecordsN1,
                                                false));

                // Title Total 1
                AccountingBalanceViewTitle total = new AccountingBalanceViewTitle();
                total.setLabel("Total I");
                total.setBrutN(creditN);
                total.setBrutN1(creditN1);
                total.setAmortissementN(amortissementN);
                total.setAmortissementN1(amortissementN1);
                total.setSoldeN(creditN - amortissementN);
                total.setSoldeN1(creditN1 - amortissementN1);
                bilanActifTitle.getSubTitles().add(total);

                // Title Stocks et en-cours
                AccountingBalanceViewTitle stocksEtEnCours = new AccountingBalanceViewTitle();
                stocksEtEnCours.setLabel("Stocks et en-cours");
                stocksEtEnCours.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanActifTitle.getSubTitles().add(stocksEtEnCours);

                stocksEtEnCours.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan(
                                                "Matières premières et autres approvisionnements ",
                                                this.brut_stocks_et_en_cours_matieres_premieres_et_autres_approvisionnements,
                                                this.provision_stocks_et_en_cours_matieres_premieres_et_autres_approvisionnements,
                                                accountingRecords, accountingRecordsN1,
                                                true));

                stocksEtEnCours.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan(
                                                "En-cours de production (biens et services)",
                                                this.brut_stocks_et_en_cours_en_cours_de_production,
                                                this.provision_stocks_et_en_cours_en_cours_de_production,
                                                accountingRecords,
                                                accountingRecordsN1,
                                                true));

                stocksEtEnCours.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Produits intermédiaires et finis",
                                                this.brut_stocks_et_en_cours_produits_intermediaires_et_finis,
                                                this.provision_stocks_et_en_cours_produits_intermediaires_et_finis,
                                                accountingRecords,
                                                accountingRecordsN1,
                                                true));

                stocksEtEnCours.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Marchandises",
                                                this.brut_stocks_et_en_cours_marchandises,
                                                this.provision_stocks_et_en_cours_marchandises, accountingRecords,
                                                accountingRecordsN1,
                                                true));

                stocksEtEnCours.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan(
                                                "Avances et acomptes versés sur commandes",
                                                this.brut_stocks_et_en_cours_avances_et_acomptes,
                                                null, accountingRecords, accountingRecordsN1,
                                                true));

                // Title Créances (dont à plus d'un an)
                AccountingBalanceViewTitle creances = new AccountingBalanceViewTitle();
                creances.setLabel("Créances (dont à plus d'un an)");
                creances.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanActifTitle.getSubTitles().add(creances);

                creances.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan(
                                                "Créances clients et comptes rattachés (ventes ou prestations de services)",
                                                this.brut_creances_creances_clients_et_comptes_rattaches,
                                                this.provision_creances_creances_clients_et_comptes_rattaches,
                                                accountingRecords,
                                                accountingRecordsN1,
                                                true));

                creances.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Autres",
                                                this.brut_creances_autres,
                                                this.provision_creances_autres, accountingRecords, accountingRecordsN1,
                                                true));

                creances.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Capital souscrit appelé non versé",
                                                this.brut_creances_capital_souscrit_appele_non_verse,
                                                null, accountingRecords, accountingRecordsN1,
                                                true));

                // Title Valeurs mobilières de placement
                AccountingBalanceViewTitle valeursMobilieres = new AccountingBalanceViewTitle();
                valeursMobilieres.setLabel("Valeurs mobilières de placement");
                valeursMobilieres.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanActifTitle.getSubTitles().add(valeursMobilieres);

                valeursMobilieres.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Actions propres",
                                                this.brut_valeurs_mobilieres_de_placement_actions_propres,
                                                null, accountingRecords, accountingRecordsN1,
                                                true));

                valeursMobilieres.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Autres titres",
                                                this.brut_valeurs_mobilieres_de_placement_autres_titres,
                                                this.provision_valeurs_mobilieres_de_placement_autres_titres,
                                                accountingRecords,
                                                accountingRecordsN1,
                                                true));

                valeursMobilieres.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Instruments de trésorerie",
                                                this.brut_valeurs_mobilieres_de_placement_autres_titres, null,
                                                accountingRecords,
                                                accountingRecordsN1,
                                                true));

                valeursMobilieres.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan("Disponibilités",
                                                this.brut_valeurs_mobilieres_de_placement_disponibilites, null,
                                                accountingRecords,
                                                accountingRecordsN1,
                                                true));

                valeursMobilieres.getItems()
                                .add(getAccountingBalanceViewItemForActifBilan(
                                                "Charges constatées d'avance (dont à plus d'un an)",
                                                this.brut_valeurs_mobilieres_de_placement_charges_constatees_avance,
                                                null, accountingRecords,
                                                accountingRecordsN1,
                                                true));

                // Title Total 2
                AccountingBalanceViewTitle total2 = new AccountingBalanceViewTitle();
                total2.setLabel("Total II");
                total2.setBrutN(creditNbis);
                total2.setBrutN1(creditN1bis);
                total2.setAmortissementN(amortissementNbis);
                total2.setAmortissementN1(amortissementN1bis);
                total2.setSoldeN(creditNbis - amortissementNbis);
                total2.setSoldeN1(creditN1bis - amortissementN1bis);
                bilanActifTitle.getSubTitles().add(total2);

                // Title Total 3 : charges à répartir sur plusieurs exercices
                AccountingBalanceViewTitle total3 = new AccountingBalanceViewTitle();
                total3.setLabel("Total III : charges à répartir sur plusieurs exercices");
                total3.setBrutN(getDebitForAccountRecordsAndAccountList(accountingRecords,
                                this.charges_a_repartir_sur_plusieurs_exercices));
                total3.setBrutN1(getDebitForAccountRecordsAndAccountList(accountingRecordsN1,
                                this.charges_a_repartir_sur_plusieurs_exercices));
                total3.setAmortissementN(0f);
                total3.setAmortissementN1(0f);
                total3.setSoldeN(total3.getBrutN());
                total3.setSoldeN1(total3.getBrutN1());
                bilanActifTitle.getSubTitles().add(total3);

                // Title Total 4 : primes de remboursement des obligations
                AccountingBalanceViewTitle total4 = new AccountingBalanceViewTitle();
                total4.setLabel("Total IV : primes de remboursement des obligations");
                total4.setBrutN(getDebitForAccountRecordsAndAccountList(accountingRecords,
                                this.primes_de_remboursement_des_obligations));
                total4.setBrutN1(getDebitForAccountRecordsAndAccountList(accountingRecordsN1,
                                this.primes_de_remboursement_des_obligations));
                total4.setAmortissementN(0f);
                total4.setAmortissementN1(0f);
                total4.setSoldeN(total4.getBrutN());
                total4.setSoldeN1(total4.getBrutN1());
                bilanActifTitle.getSubTitles().add(total4);

                // Title Total 5 : écarts de conversion Actif
                AccountingBalanceViewTitle total5 = new AccountingBalanceViewTitle();
                total5.setLabel("Total V : écarts de conversion Actif");
                total5.setBrutN(getDebitForAccountRecordsAndAccountList(accountingRecords,
                                this.ecarts_de_conversion_actif));
                total5.setBrutN1(getDebitForAccountRecordsAndAccountList(accountingRecordsN1,
                                this.ecarts_de_conversion_actif));
                total5.setAmortissementN(0f);
                total5.setAmortissementN1(0f);
                total5.setSoldeN(total5.getBrutN());
                total5.setSoldeN1(total5.getBrutN1());
                bilanActifTitle.getSubTitles().add(total5);

                // Title Total général
                AccountingBalanceViewTitle totalGeneral = new AccountingBalanceViewTitle();
                totalGeneral.setLabel("Total général (I+II+III+IV+V)");
                totalGeneral
                                .setBrutN(total.getBrutN() + total2.getBrutN() + total3.getBrutN() + total4.getBrutN()
                                                + total5.getBrutN());
                totalGeneral.setBrutN1(
                                total.getBrutN1() + total2.getBrutN1() + total3.getBrutN1() + total4.getBrutN1()
                                                + total5.getBrutN1());
                totalGeneral.setAmortissementN(total.getAmortissementN() + total2.getAmortissementN()
                                + total3.getAmortissementN() + total4.getAmortissementN() + total5.getAmortissementN());
                totalGeneral.setAmortissementN1(total.getAmortissementN1() + total2.getAmortissementN1()
                                + total3.getAmortissementN1() + total4.getAmortissementN1()
                                + total5.getAmortissementN1());
                totalGeneral.setSoldeN(
                                total.getSoldeN() + total2.getSoldeN() + total3.getSoldeN() + total4.getSoldeN()
                                                + total5.getSoldeN());
                totalGeneral.setSoldeN1(
                                total.getSoldeN1() + total2.getSoldeN1() + total3.getSoldeN1() + total4.getSoldeN1()
                                                + total5.getSoldeN1());
                bilanActifTitle.getSubTitles().add(totalGeneral);

                return bilanActifTitle;
        }

        private AccountingBalanceViewItem getAccountingBalanceViewItemForActifBilan(String label, String paramBrut,
                        String paramImmo,
                        List<AccountingBalanceBilan> accountingRecords,
                        List<AccountingBalanceBilan> accountingRecordsN1,
                        boolean isTotal2) {
                AccountingBalanceViewItem item = new AccountingBalanceViewItem();
                item.setBrutN(
                                getDebitForAccountRecordsAndAccountList(accountingRecords, paramBrut));
                item.setBrutN1(
                                getDebitForAccountRecordsAndAccountList(accountingRecordsN1,
                                                this.brut_immobilisations_financieres_autres));
                if (paramImmo != null) {
                        item.setAmortissementN(getDebitForAccountRecordsAndAccountList(accountingRecords, paramImmo));
                        item.setAmortissementN1(
                                        getDebitForAccountRecordsAndAccountList(accountingRecordsN1, paramImmo));
                } else {
                        item.setAmortissementN(0f);
                        item.setAmortissementN1(0f);
                }
                item.setSoldeN(item.getBrutN() - item.getAmortissementN());
                item.setSoldeN1(item.getBrutN1() - item.getAmortissementN1());

                if (!isTotal2) {
                        if (item.getBrutN() != null)
                                creditN += item.getBrutN();
                        if (item.getBrutN1() != null)
                                creditN1 += item.getBrutN1();
                        if (item.getAmortissementN() != null)
                                amortissementN += item.getAmortissementN();
                        if (item.getAmortissementN1() != null)
                                amortissementN1 += item.getAmortissementN1();
                } else {
                        if (item.getBrutN() != null)
                                creditNbis += item.getBrutN();
                        if (item.getBrutN1() != null)
                                creditN1bis += item.getBrutN1();
                        if (item.getAmortissementN() != null)
                                amortissementNbis += item.getAmortissementN();
                        if (item.getAmortissementN1() != null)
                                amortissementN1bis += item.getAmortissementN1();
                }
                item.setLabel(label);
                return item;
        }

        public AccountingBalanceViewTitle getBilanPassif(List<AccountingBalanceBilan> accountingRecords,
                        List<AccountingBalanceBilan> accountingRecordsN1) {

                AccountingBalanceViewTitle bilanPassifTitle = new AccountingBalanceViewTitle();
                bilanPassifTitle.setLabel("Passif");
                bilanPassifTitle.setActifBilan(false);
                bilanPassifTitle.setSubTitles(new ArrayList<AccountingBalanceViewTitle>());
                bilanPassifTitle.setItems(new ArrayList<AccountingBalanceViewItem>());

                // Title Total 1
                AccountingBalanceViewTitle total1 = new AccountingBalanceViewTitle();
                total1.setLabel("Total I");
                total1.setAmortissementN(0f);
                total1.setAmortissementN1(0f);

                bilanPassifTitle.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Capital souscrit non appelé",
                                                this.brut_capital_souscrit_non_appele, null,
                                                accountingRecords, accountingRecordsN1, total1));

                bilanPassifTitle.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Capital (dont versé)",
                                                this.capital, null, accountingRecords, accountingRecordsN1, total1));

                bilanPassifTitle.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan(
                                                "Primes d'émission, de fusion, d'apport",
                                                this.primes_emission_fusion_apport, null, accountingRecords,
                                                accountingRecordsN1, total1));

                bilanPassifTitle.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Ecarts de réévaluation",
                                                this.ecarts_de_reevaluation, null, accountingRecords,
                                                accountingRecordsN1, total1));

                bilanPassifTitle.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Ecarts d'équivalence",
                                                this.ecarts_equivalence, null, accountingRecords, accountingRecordsN1,
                                                total1));

                // Title Réserves
                AccountingBalanceViewTitle reserves = new AccountingBalanceViewTitle();
                reserves.setLabel("Réserves");
                reserves.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanPassifTitle.getSubTitles().add(reserves);

                reserves.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Réserve légale",
                                                this.reserve_legale, null, accountingRecords, accountingRecordsN1,
                                                total1));

                reserves.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan(
                                                "Réserves statutaires ou contractuelles",
                                                this.reserves_statutaires_ou_contractuelles, null, accountingRecords,
                                                accountingRecordsN1,
                                                total1));

                reserves.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Réserves réglementées",
                                                this.reserves_reglementees, null, accountingRecords,
                                                accountingRecordsN1, total1));

                reserves.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Autres",
                                                this.autres, null, accountingRecords, accountingRecordsN1, total1));

                // Title empty
                AccountingBalanceViewTitle empty1 = new AccountingBalanceViewTitle();
                empty1.setLabel("");
                empty1.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanPassifTitle.getSubTitles().add(empty1);

                empty1.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Report à nouveau",
                                                this.report_a_nouveau, null, accountingRecords, accountingRecordsN1,
                                                total1));

                empty1.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan(
                                                "Résultat de l'exercice (bénéfice ou perte)",
                                                this.resultat_exercice, null, accountingRecords, accountingRecordsN1,
                                                total1));

                empty1.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Subventions d'investissement",
                                                this.subventions_investissement, null, accountingRecords,
                                                accountingRecordsN1, total1));

                empty1.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Provisions réglementées",
                                                this.provisions_reglementees, null, accountingRecords,
                                                accountingRecordsN1, total1));

                // End of total 1
                total1.setSoldeN(total1.getBrutN());
                total1.setSoldeN1(total1.getBrutN1());
                bilanPassifTitle.getSubTitles().add(total1);

                // Title Total 1 bis
                AccountingBalanceViewTitle total1bis = new AccountingBalanceViewTitle();
                total1bis.setLabel("Total Ib");
                total1bis.setAmortissementN(0f);
                total1bis.setAmortissementN1(0f);

                // Title empty
                AccountingBalanceViewTitle empty2 = new AccountingBalanceViewTitle();
                empty2.setLabel("");
                empty2.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanPassifTitle.getSubTitles().add(empty2);

                empty2.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan(
                                                "Produit des émissions de titres participatifs",
                                                this.produit_des_emissions_de_titres_participatifs, null,
                                                accountingRecords,
                                                accountingRecordsN1, total1bis));

                empty2.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Avances conditionnées",
                                                this.avances_conditionnees, null, accountingRecords,
                                                accountingRecordsN1, total1bis));

                // End of total 1 bis
                total1bis.setSoldeN(total1bis.getBrutN());
                total1bis.setSoldeN1(total1bis.getBrutN1());
                bilanPassifTitle.getSubTitles().add(total1bis);

                // Title Total 2
                AccountingBalanceViewTitle total2 = new AccountingBalanceViewTitle();
                total2.setLabel("Total II");
                total2.setAmortissementN(0f);
                total2.setAmortissementN1(0f);

                // Title empty
                AccountingBalanceViewTitle empty3 = new AccountingBalanceViewTitle();
                empty3.setLabel("");
                empty3.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanPassifTitle.getSubTitles().add(empty3);

                empty3.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Provisions pour risques",
                                                this.provisions_pour_risques, null, accountingRecords,
                                                accountingRecordsN1, total2));

                empty3.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Avances conditionnées",
                                                this.provisions_pour_charges, null, accountingRecords,
                                                accountingRecordsN1, total2));

                // End of total 2
                total2.setSoldeN(total2.getBrutN());
                total2.setSoldeN1(total2.getBrutN1());
                bilanPassifTitle.getSubTitles().add(total2);

                // Title Total 3
                AccountingBalanceViewTitle total3 = new AccountingBalanceViewTitle();
                total3.setLabel("Total III");
                total3.setAmortissementN(0f);
                total3.setAmortissementN1(0f);

                // Title empty
                AccountingBalanceViewTitle empty4 = new AccountingBalanceViewTitle();
                empty4.setLabel("");
                empty4.setItems(new ArrayList<AccountingBalanceViewItem>());
                bilanPassifTitle.getSubTitles().add(empty4);

                empty4.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Emprunts obligataires convertibles",
                                                this.emprunts_obligataires_convertibles, null, accountingRecords,
                                                accountingRecordsN1, total3));

                empty4.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Autres emprunts obligataires",
                                                this.autres_emprunts_obligataires, null, accountingRecords,
                                                accountingRecordsN1, total3));

                empty4.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan(
                                                "Emprunts et dettes auprès des établissements de crédit",
                                                this.emprunts_et_dettes_aupres_des_etablissements_de_credit, null,
                                                accountingRecords,
                                                accountingRecordsN1, total3));

                empty4.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan(
                                                "Emprunts et dettes financières divers (dont emprunts participatifs)",
                                                this.emprunts_et_dettes_financieres_divers, null, accountingRecords,
                                                accountingRecordsN1,
                                                total3));

                empty4.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan(
                                                "Avances et acomptes reçus sur commandes en cours",
                                                this.avances_et_acomptes_recus_sur_commandes_en_cours, null,
                                                accountingRecords,
                                                accountingRecordsN1, total3));

                empty4.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan(
                                                "Dettes fournisseurs et comptes rattachés",
                                                this.dettes_fournisseurs_et_comptes_rattaches, null, accountingRecords,
                                                accountingRecordsN1,
                                                total3));

                empty4.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Dettes fiscales et sociales",
                                                this.dettes_fiscales_et_sociales, null, accountingRecords,
                                                accountingRecordsN1, total3));

                empty4.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan(
                                                "Dettes sur immobilisations et comptes rattachés",
                                                this.dettes_sur_immobilisations_et_comptes_rattaches, null,
                                                accountingRecords,
                                                accountingRecordsN1, total3));

                empty4.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Autres dettes",
                                                this.autres_dettes, null, accountingRecords, accountingRecordsN1,
                                                total3));

                empty4.getItems()
                                .add(getAccountingBalanceViewItemForPassifBilan("Produits constatés d'avance",
                                                this.produits_constates_avance, null, accountingRecords,
                                                accountingRecordsN1, total3));

                // End of total 3
                total3.setSoldeN(total3.getBrutN());
                total3.setSoldeN1(total3.getBrutN1());
                bilanPassifTitle.getSubTitles().add(total3);

                // Title Total général
                AccountingBalanceViewTitle totalGeneral = new AccountingBalanceViewTitle();
                totalGeneral.setLabel("Total général (I+Ib+II+III)");
                totalGeneral.setBrutN(total1.getBrutN() + total1bis.getBrutN() + total2.getBrutN() + total3.getBrutN());
                totalGeneral.setBrutN1(
                                total1.getBrutN1() + total1bis.getBrutN1() + total2.getBrutN1() + total3.getBrutN1());
                totalGeneral.setAmortissementN(0f);
                totalGeneral.setAmortissementN1(0f);
                totalGeneral.setSoldeN(totalGeneral.getBrutN());
                totalGeneral.setSoldeN1(totalGeneral.getBrutN1());
                bilanPassifTitle.getSubTitles().add(totalGeneral);

                return bilanPassifTitle;

        }

        private AccountingBalanceViewItem getAccountingBalanceViewItemForPassifBilan(String label, String paramBrut,
                        String paramImmo,
                        List<AccountingBalanceBilan> accountingRecords,
                        List<AccountingBalanceBilan> accountingRecordsN1,
                        AccountingBalanceViewTitle totalItem) {
                AccountingBalanceViewItem item = new AccountingBalanceViewItem();
                item.setBrutN(
                                getCreditForAccountRecordsAndAccountList(accountingRecords, paramBrut));
                item.setBrutN1(
                                getCreditForAccountRecordsAndAccountList(accountingRecordsN1,
                                                this.brut_immobilisations_financieres_autres));
                if (paramImmo != null) {
                        item.setAmortissementN(getCreditForAccountRecordsAndAccountList(accountingRecords, paramImmo));
                        item.setAmortissementN1(
                                        getCreditForAccountRecordsAndAccountList(accountingRecordsN1, paramImmo));
                } else {
                        item.setAmortissementN(0f);
                        item.setAmortissementN1(0f);
                }
                item.setSoldeN(item.getBrutN() - item.getAmortissementN());
                item.setSoldeN1(item.getBrutN1() - item.getAmortissementN1());

                if (totalItem.getBrutN() == null)
                        totalItem.setBrutN(0f);

                if (totalItem.getBrutN1() == null)
                        totalItem.setBrutN1(0f);

                if (item.getBrutN() != null)
                        totalItem.setBrutN(totalItem.getBrutN() + item.getBrutN());
                if (item.getBrutN1() != null)
                        totalItem.setBrutN1(totalItem.getBrutN1() + item.getBrutN1());

                item.setLabel(label);
                return item;
        }

        public List<AccountingBalanceViewTitle> getProfitAndLost(List<AccountingBalanceBilan> accountingRecords,
                        List<AccountingBalanceBilan> accountingRecordsN1) {

                List<AccountingBalanceViewTitle> profitAndLostTitles = new ArrayList<AccountingBalanceViewTitle>();

                AccountingBalanceViewTitle profitAndLostProductTitle = new AccountingBalanceViewTitle();
                profitAndLostProductTitle.setLabel("Produits d'exploitations");
                profitAndLostProductTitle.setActifBilan(false);
                profitAndLostProductTitle.setSubTitles(new ArrayList<AccountingBalanceViewTitle>());
                profitAndLostProductTitle.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostTitles.add(profitAndLostProductTitle);

                List<AccountingBalanceViewTitle> profitAndLostTotalTitles = new ArrayList<AccountingBalanceViewTitle>();
                profitAndLostProductTitle.setTotals(profitAndLostTotalTitles);

                // Title Montant net du CA
                AccountingBalanceViewTitle montantNetCa = new AccountingBalanceViewTitle();
                montantNetCa.setLabel("Montant net du CA");
                montantNetCa.setAmortissementN(0f);
                montantNetCa.setAmortissementN1(0f);

                profitAndLostProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Ventes de marchandises",
                                                this.vente_des_marchandises_vendues, accountingRecords,
                                                accountingRecordsN1, montantNetCa));

                // Title Production vendue (biens et services)
                AccountingBalanceViewTitle productionVendueTitle = new AccountingBalanceViewTitle();
                productionVendueTitle.setLabel("Production vendue (biens et services)");
                productionVendueTitle.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostProductTitle.getSubTitles().add(productionVendueTitle);

                productionVendueTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Ventes",
                                                this.ventes, accountingRecords, accountingRecordsN1, montantNetCa));

                productionVendueTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Travaux",
                                                this.travaux, accountingRecords, accountingRecordsN1, montantNetCa));

                productionVendueTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Prestations de services",
                                                this.prestations_de_services, accountingRecords, accountingRecordsN1,
                                                montantNetCa));

                // End of montant CA
                montantNetCa.setSoldeN(montantNetCa.getBrutN());
                montantNetCa.setSoldeN1(montantNetCa.getBrutN1());
                profitAndLostProductTitle.getSubTitles().add(montantNetCa);

                profitAndLostTotalTitles.add(montantNetCa);

                // Title Total 1
                AccountingBalanceViewTitle total1 = new AccountingBalanceViewTitle();
                total1.setLabel("Total I");
                total1.setAmortissementN(0f);
                total1.setAmortissementN1(0f);
                total1.setBrutN(montantNetCa.getBrutN());
                total1.setBrutN1(montantNetCa.getBrutN1());
                total1.setSoldeN(montantNetCa.getBrutN());
                total1.setSoldeN1(montantNetCa.getBrutN1());

                // Title Production stockée
                AccountingBalanceViewTitle productionStockee = new AccountingBalanceViewTitle();
                productionStockee.setLabel("Production stockée");
                productionStockee.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostProductTitle.getSubTitles().add(productionStockee);

                productionStockee.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "En-cours de production de biens",
                                                this.en_cours_de_production_de_biens, accountingRecords,
                                                accountingRecordsN1, total1));

                productionStockee.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "En-cours de production de services",
                                                this.en_cours_de_production_de_services, accountingRecords,
                                                accountingRecordsN1, total1));

                productionStockee.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Produits",
                                                this.produits, accountingRecords, accountingRecordsN1, total1));

                // Title Production immobilisée
                AccountingBalanceViewTitle productionimmobilisee = new AccountingBalanceViewTitle();
                productionimmobilisee.setLabel("Production immobilisée");
                productionimmobilisee.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostProductTitle.getSubTitles().add(productionimmobilisee);

                productionimmobilisee.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Production immobilisée",
                                                this.produtction_immobilisee, accountingRecords, accountingRecordsN1,
                                                total1));

                productionimmobilisee.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Produits nets partiels sur opération à long terme",
                                                this.produits_net_partiels_operations_long_terme, accountingRecords,
                                                accountingRecordsN1,
                                                total1));

                // Title Autres produits
                AccountingBalanceViewTitle autresProduits = new AccountingBalanceViewTitle();
                autresProduits.setLabel("Autres produits");
                autresProduits.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostProductTitle.getSubTitles().add(autresProduits);

                autresProduits.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Subventions d'exploitation",
                                                this.subventions_exploitation, accountingRecords, accountingRecordsN1,
                                                total1));

                autresProduits.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Reprise sur amortissements et provisions",
                                                this.reprise_sur_amort_et_prov, accountingRecords, accountingRecordsN1,
                                                total1));

                autresProduits.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Transfert de charge",
                                                this.transfert_de_charges, accountingRecords, accountingRecordsN1,
                                                total1));

                autresProduits.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Autres produits",
                                                this.produit_autres, accountingRecords, accountingRecordsN1, total1));

                // End of Total 1
                total1.setSoldeN(total1.getBrutN());
                total1.setSoldeN1(total1.getBrutN1());
                profitAndLostProductTitle.getSubTitles().add(total1);

                AccountingBalanceViewTitle profitAndLostChargesTitle = new AccountingBalanceViewTitle();
                profitAndLostChargesTitle.setLabel("Charges d'exploitations");
                profitAndLostChargesTitle.setActifBilan(false);
                profitAndLostChargesTitle.setSubTitles(new ArrayList<AccountingBalanceViewTitle>());
                profitAndLostChargesTitle.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostTitles.add(profitAndLostChargesTitle);

                // Title Total 2
                AccountingBalanceViewTitle total2 = new AccountingBalanceViewTitle();
                total2.setLabel("Total II");
                total2.setAmortissementN(0f);
                total2.setAmortissementN1(0f);

                profitAndLostChargesTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge("Achats de marchandises",
                                                this.achats_de_marchandises, accountingRecords, accountingRecordsN1,
                                                total2));

                profitAndLostChargesTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge("Variations de stocks",
                                                this.variation_de_stocks_de_marchandises, accountingRecords,
                                                accountingRecordsN1, total2));

                // Title Autres achats
                AccountingBalanceViewTitle autresAchats = new AccountingBalanceViewTitle();
                autresAchats.setLabel("Autres achats et approvisionnements");
                autresAchats.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostChargesTitle.getSubTitles().add(autresAchats);

                autresAchats.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Achats stockés de matières premières",
                                                this.achats_stockes_matiere_premieres, accountingRecords,
                                                accountingRecordsN1, total2));

                autresAchats.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Achats stockés pour d'autres approvisionnements",
                                                this.achats_stockes_autres_approv, accountingRecords,
                                                accountingRecordsN1, total2));

                autresAchats.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Variation de stock d'approvisionnements",
                                                this.variation_de_stock_approv, accountingRecords, accountingRecordsN1,
                                                total2));

                autresAchats.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge("Achats de sous-traitance",
                                                this.achats_de_sous_traitance, accountingRecords, accountingRecordsN1,
                                                total2));

                autresAchats.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Achats non stockés de matières et fournitures",
                                                this.achats_non_stockes_de_matieres_et_fourn, accountingRecords,
                                                accountingRecordsN1, total2));

                autresAchats.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Sociétés extérieures : personnel interimaire",
                                                this.sce_exterieurs_personnel_interim, accountingRecords,
                                                accountingRecordsN1, total2));

                autresAchats.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Sociétés extérieures : loyers crédit-bail",
                                                this.sce_exterieurs_loyers_credit_bail, accountingRecords,
                                                accountingRecordsN1, total2));

                autresAchats.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge("Sociétés extérieures : autres",
                                                this.sce_exterieurs_autres, accountingRecords, accountingRecordsN1,
                                                total2));

                // Title Impôts et taxes
                AccountingBalanceViewTitle impots = new AccountingBalanceViewTitle();
                impots.setLabel("Impôts et taxes");
                impots.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostChargesTitle.getSubTitles().add(impots);

                impots.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge("Sur rémunérations",
                                                this.impots_sur_remunerations, accountingRecords, accountingRecordsN1,
                                                total2));

                impots.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Autres",
                                                this.impots_autres, accountingRecords, accountingRecordsN1, total2));

                // Title Charges de personnel
                AccountingBalanceViewTitle chargePersonnel = new AccountingBalanceViewTitle();
                chargePersonnel.setLabel("Charges de personnel");
                chargePersonnel.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostChargesTitle.getSubTitles().add(chargePersonnel);

                chargePersonnel.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge("Salaires et traitements",
                                                this.salaires_et_traitements, accountingRecords, accountingRecordsN1,
                                                total2));

                chargePersonnel.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge("Charges sociales",
                                                this.charges_sociales,
                                                accountingRecords, accountingRecordsN1, total2));

                // Title Dotations aux amortissements
                AccountingBalanceViewTitle dotationsAmortissments = new AccountingBalanceViewTitle();
                dotationsAmortissments.setLabel("Dotations aux amortissements");
                dotationsAmortissments.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostChargesTitle.getSubTitles().add(dotationsAmortissments);

                dotationsAmortissments.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Sur immobilisations : amortissements",
                                                this.sur_immo_amortissements, accountingRecords, accountingRecordsN1,
                                                total2));

                dotationsAmortissments.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Sur immobilisations : provisions",
                                                this.sur_immo_provisions, accountingRecords, accountingRecordsN1,
                                                total2));

                dotationsAmortissments.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Sur actif circulant : provisions",
                                                this.sur_actif_circulant_provisions, accountingRecords,
                                                accountingRecordsN1, total2));

                dotationsAmortissments.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Pour risques et charges : provisions",
                                                this.pour_risques_et_charges_provisions, accountingRecords,
                                                accountingRecordsN1, total2));

                // Title Autres charges
                AccountingBalanceViewTitle autresCharges = new AccountingBalanceViewTitle();
                autresCharges.setLabel("Autres charges");
                autresCharges.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostChargesTitle.getSubTitles().add(autresCharges);

                autresCharges.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge("Autres charges",
                                                this.autres_charges, accountingRecords, accountingRecordsN1, total2));

                // End of Total 2
                total2.setSoldeN(total2.getBrutN());
                total2.setSoldeN1(total2.getBrutN1());
                profitAndLostChargesTitle.getSubTitles().add(total2);

                AccountingBalanceViewTitle totalResultatExploitation = new AccountingBalanceViewTitle();
                totalResultatExploitation.setLabel("Résultat d'exploitation (I-II)");
                totalResultatExploitation.setBrutN(total1.getBrutN() - total2.getBrutN());
                totalResultatExploitation.setBrutN1(total1.getBrutN1() - total2.getBrutN1());
                totalResultatExploitation.setSoldeN(total1.getSoldeN() - total2.getSoldeN());
                totalResultatExploitation.setSoldeN1(total1.getSoldeN1() - total2.getSoldeN1());
                profitAndLostTotalTitles.add(totalResultatExploitation);

                AccountingBalanceViewTitle profitAndLostFinancialProductTitle = new AccountingBalanceViewTitle();
                profitAndLostFinancialProductTitle.setLabel("Produits financiers");
                profitAndLostFinancialProductTitle.setActifBilan(false);
                profitAndLostFinancialProductTitle.setSubTitles(new ArrayList<AccountingBalanceViewTitle>());
                profitAndLostFinancialProductTitle.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostTitles.add(profitAndLostFinancialProductTitle);

                // Title Total 3
                AccountingBalanceViewTitle total3 = new AccountingBalanceViewTitle();
                total3.setLabel("Total III");
                total3.setAmortissementN(0f);
                total3.setAmortissementN1(0f);

                profitAndLostFinancialProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Produits de participations",
                                                this.des_participations, accountingRecords, accountingRecordsN1,
                                                total3));

                profitAndLostFinancialProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Produits d'autres valeurs mobilières et créances de l'actif immobilisé",
                                                this.des_vmp_et_creances, accountingRecords, accountingRecordsN1,
                                                total3));

                profitAndLostFinancialProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Autres intérêts et produits assimilés",
                                                this.autres_interets_et_produits, accountingRecords,
                                                accountingRecordsN1, total3));

                profitAndLostFinancialProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Reprises sur provisions et transferts de charges",
                                                this.reprise_sur_prov_et_transferts_charges, accountingRecords,
                                                accountingRecordsN1, total3));

                profitAndLostFinancialProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Différence positive de change",
                                                this.difference_positive_de_change, accountingRecords,
                                                accountingRecordsN1, total3));

                profitAndLostFinancialProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Produits nets sur cessions de valeurs mobilières de placement",
                                                this.produits_nets_sur_cession_vmp, accountingRecords,
                                                accountingRecordsN1, total3));

                // End of Total 3
                total3.setSoldeN(total3.getBrutN());
                total3.setSoldeN1(total3.getBrutN1());
                profitAndLostFinancialProductTitle.getSubTitles().add(total3);

                AccountingBalanceViewTitle profitAndLostFinancialChargeTitle = new AccountingBalanceViewTitle();
                profitAndLostFinancialChargeTitle.setLabel("Charges financières");
                profitAndLostFinancialChargeTitle.setActifBilan(false);
                profitAndLostFinancialChargeTitle.setSubTitles(new ArrayList<AccountingBalanceViewTitle>());
                profitAndLostFinancialChargeTitle.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostTitles.add(profitAndLostFinancialChargeTitle);

                // Title Total 4
                AccountingBalanceViewTitle total4 = new AccountingBalanceViewTitle();
                total4.setLabel("Total IV");
                total4.setAmortissementN(0f);
                total4.setAmortissementN1(0f);

                profitAndLostFinancialChargeTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Dotations aux amortissements et provisions",
                                                this.dotations_aux_amort_et_provisions, accountingRecords,
                                                accountingRecordsN1, total4));

                profitAndLostFinancialChargeTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge("Intérêts et charges assimilés",
                                                this.interets_et_charges_assimiles, accountingRecords,
                                                accountingRecordsN1, total4));

                profitAndLostFinancialChargeTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Différence négatives de change",
                                                this.difference_negatives_de_change, accountingRecords,
                                                accountingRecordsN1, total4));

                profitAndLostFinancialChargeTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Charges nettes sur cessions de valeurs mobilières de placement",
                                                this.charges_nettes_sur_cessions_de_vmp, accountingRecords,
                                                accountingRecordsN1, total4));

                // End of Total 4
                total4.setSoldeN(total4.getBrutN());
                total4.setSoldeN1(total4.getBrutN1());
                profitAndLostFinancialChargeTitle.getSubTitles().add(total4);

                AccountingBalanceViewTitle totalResultatFinancier = new AccountingBalanceViewTitle();
                totalResultatFinancier.setLabel("Résultat financier (III-IV)");
                totalResultatFinancier.setBrutN(total3.getBrutN() - total4.getBrutN());
                totalResultatFinancier.setBrutN1(total3.getBrutN1() - total4.getBrutN1());
                totalResultatFinancier.setSoldeN(total3.getSoldeN() - total4.getSoldeN());
                totalResultatFinancier.setSoldeN1(total3.getSoldeN1() - total4.getSoldeN1());
                profitAndLostTotalTitles.add(totalResultatFinancier);

                AccountingBalanceViewTitle totalResultatCourantAvantImpots = new AccountingBalanceViewTitle();
                totalResultatCourantAvantImpots.setLabel("Résultat courant avant impôts (I-II+III-IV)");
                totalResultatCourantAvantImpots
                                .setBrutN(total1.getBrutN() - total2.getBrutN() + total3.getBrutN()
                                                - total4.getBrutN());
                totalResultatCourantAvantImpots
                                .setBrutN1(total1.getBrutN1() - total2.getBrutN1() + total3.getBrutN1()
                                                - total4.getBrutN1());
                totalResultatCourantAvantImpots
                                .setSoldeN(total1.getSoldeN() - total2.getSoldeN() + total3.getSoldeN()
                                                - total4.getSoldeN());
                totalResultatCourantAvantImpots
                                .setSoldeN1(total1.getSoldeN1() - total2.getSoldeN1() + total3.getSoldeN1()
                                                - total4.getSoldeN1());
                profitAndLostTotalTitles.add(totalResultatCourantAvantImpots);

                AccountingBalanceViewTitle profitAndLostExceptionnalProductTitle = new AccountingBalanceViewTitle();
                profitAndLostExceptionnalProductTitle.setLabel("Produits exceptionnels");
                profitAndLostExceptionnalProductTitle.setActifBilan(false);
                profitAndLostExceptionnalProductTitle.setSubTitles(new ArrayList<AccountingBalanceViewTitle>());
                profitAndLostExceptionnalProductTitle.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostTitles.add(profitAndLostExceptionnalProductTitle);

                // Title Total 5
                AccountingBalanceViewTitle total5 = new AccountingBalanceViewTitle();
                total5.setLabel("Total V");
                total5.setAmortissementN(0f);
                total5.setAmortissementN1(0f);

                profitAndLostExceptionnalProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct("Sur opération de gestion",
                                                this.produit_sur_operation_de_gestion, accountingRecords,
                                                accountingRecordsN1, total5));

                profitAndLostExceptionnalProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Produit de cession éléments d'actifs",
                                                this.produit_de_cession_elements_actifs, accountingRecords,
                                                accountingRecordsN1, total5));

                profitAndLostExceptionnalProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Subventions d'investissement virées",
                                                this.subventions_investissement_virees, accountingRecords,
                                                accountingRecordsN1, total5));

                profitAndLostExceptionnalProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Neutralisation d'amortissements",
                                                this.neutralisation_amortissements, accountingRecords,
                                                accountingRecordsN1, total5));

                profitAndLostExceptionnalProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Reprise sur provision et transferts de charges",
                                                this.reprise_sur_prov_et_transferts_charges, accountingRecords,
                                                accountingRecordsN1, total5));

                profitAndLostExceptionnalProductTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Autres produits exceptionnels",
                                                this.produit_autres, accountingRecords, accountingRecordsN1, total5));

                // End of Total 5
                total5.setSoldeN(total5.getBrutN());
                total5.setSoldeN1(total5.getBrutN1());
                profitAndLostExceptionnalProductTitle.getSubTitles().add(total5);

                AccountingBalanceViewTitle profitAndLostExceptionnalChargeTitle = new AccountingBalanceViewTitle();
                profitAndLostExceptionnalChargeTitle.setLabel("Charges exceptionnels");
                profitAndLostExceptionnalChargeTitle.setActifBilan(false);
                profitAndLostExceptionnalChargeTitle.setSubTitles(new ArrayList<AccountingBalanceViewTitle>());
                profitAndLostExceptionnalChargeTitle.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostTitles.add(profitAndLostExceptionnalChargeTitle);

                // Title Total 6
                AccountingBalanceViewTitle total6 = new AccountingBalanceViewTitle();
                total6.setLabel("Total VI");
                total6.setAmortissementN(0f);
                total6.setAmortissementN1(0f);

                profitAndLostExceptionnalChargeTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge("Sur opération de gestion",
                                                this.charges_sur_operation_de_gestion, accountingRecords,
                                                accountingRecordsN1, total6));

                profitAndLostExceptionnalChargeTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Sur opération en capital : valeur comptale des éléments cédés",
                                                this.sur_operation_en_capital_valeur_comptale_des_elements_cedes,
                                                accountingRecords,
                                                accountingRecordsN1, total6));

                profitAndLostExceptionnalChargeTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Sur opération en capital : autres",
                                                this.sur_operation_en_capital_autres, accountingRecords,
                                                accountingRecordsN1, total6));

                profitAndLostExceptionnalChargeTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Dotations aux amortissements et aux provisions",
                                                this.dotations_aux_amortissements_et_aux_provisions, accountingRecords,
                                                accountingRecordsN1,
                                                total6));

                // End of Total 6
                total6.setSoldeN(total6.getBrutN());
                total6.setSoldeN1(total6.getBrutN1());
                profitAndLostExceptionnalChargeTitle.getSubTitles().add(total6);

                AccountingBalanceViewTitle totalResultatExceptionnel = new AccountingBalanceViewTitle();
                totalResultatExceptionnel.setLabel("Résultat exceptionnel (V-VI)");
                totalResultatExceptionnel.setBrutN(total5.getBrutN() - total6.getBrutN());
                totalResultatExceptionnel.setBrutN1(total5.getBrutN1() - total6.getBrutN1());
                totalResultatExceptionnel.setSoldeN(total5.getSoldeN() - total6.getSoldeN());
                totalResultatExceptionnel.setSoldeN1(total5.getSoldeN1() - total6.getSoldeN1());
                profitAndLostTotalTitles.add(totalResultatExceptionnel);

                AccountingBalanceViewTitle profitAndLostParticipationTitle = new AccountingBalanceViewTitle();
                profitAndLostParticipationTitle.setLabel("Participation (IX)");
                profitAndLostParticipationTitle.setActifBilan(false);
                profitAndLostParticipationTitle.setSubTitles(new ArrayList<AccountingBalanceViewTitle>());
                profitAndLostParticipationTitle.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostTitles.add(profitAndLostParticipationTitle);

                // Title Total 7
                AccountingBalanceViewTitle total7 = new AccountingBalanceViewTitle();
                total7.setLabel("Total VII");
                total7.setAmortissementN(0f);
                total7.setAmortissementN1(0f);

                profitAndLostParticipationTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Participation des salariés aux résultats",
                                                this.participation_salaries, accountingRecords, accountingRecordsN1,
                                                total7));

                // End of Total 7
                total7.setSoldeN(total7.getBrutN());
                total7.setSoldeN1(total7.getBrutN1());

                AccountingBalanceViewTitle profitAndLostImpotsTitle = new AccountingBalanceViewTitle();
                profitAndLostImpotsTitle.setLabel("Impôts (X)");
                profitAndLostImpotsTitle.setActifBilan(false);
                profitAndLostImpotsTitle.setSubTitles(new ArrayList<AccountingBalanceViewTitle>());
                profitAndLostImpotsTitle.setItems(new ArrayList<AccountingBalanceViewItem>());
                profitAndLostTitles.add(profitAndLostImpotsTitle);

                // Title Total 8
                AccountingBalanceViewTitle total8 = new AccountingBalanceViewTitle();
                total8.setLabel("Total VIII");
                total8.setAmortissementN(0f);
                total8.setAmortissementN1(0f);

                profitAndLostImpotsTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostCharge(
                                                "Impôts",
                                                this.impot_sur_les_benefices, accountingRecords, accountingRecordsN1,
                                                total8));

                profitAndLostImpotsTitle.getItems()
                                .add(getAccountingBalanceViewItemForProfitAndLostProduct(
                                                "Provisions pour impôts",
                                                this.provision_pour_impots, accountingRecords, accountingRecordsN1,
                                                total8));

                // End of Total 8
                total8.setSoldeN(total6.getBrutN());
                total8.setSoldeN1(total6.getBrutN1());
                profitAndLostImpotsTitle.getSubTitles().add(total8);

                AccountingBalanceViewTitle totalResultatProduct = new AccountingBalanceViewTitle();
                totalResultatProduct.setLabel("Total des produits (I+III+V)");
                totalResultatProduct.setBrutN(total1.getBrutN() + total3.getBrutN() + total5.getBrutN());
                totalResultatProduct.setBrutN1(total1.getBrutN1() + total3.getBrutN1() + total5.getBrutN1());
                totalResultatProduct.setSoldeN(total1.getSoldeN() + total3.getSoldeN() + total5.getSoldeN());
                totalResultatProduct.setSoldeN1(total1.getSoldeN1() + total3.getSoldeN1() + total5.getSoldeN1());
                profitAndLostTotalTitles.add(totalResultatProduct);

                AccountingBalanceViewTitle totalResultatCharges = new AccountingBalanceViewTitle();
                totalResultatCharges.setLabel("Total des charges (II+IV+VI+VII+VIII)");
                totalResultatCharges.setBrutN(
                                total2.getBrutN() + total4.getBrutN() + total6.getBrutN() + total7.getBrutN()
                                                + total8.getBrutN());
                totalResultatCharges.setBrutN1(
                                total2.getBrutN1() + total4.getBrutN1() + total6.getBrutN1() + total7.getBrutN1()
                                                + total8.getBrutN1());
                totalResultatCharges.setSoldeN(
                                total2.getSoldeN() + total4.getSoldeN() + total6.getSoldeN() + total7.getSoldeN()
                                                + total8.getSoldeN());
                totalResultatCharges.setSoldeN1(total2.getSoldeN1() + total4.getSoldeN1() + total6.getSoldeN1()
                                + total7.getSoldeN1() + total8.getSoldeN1());
                profitAndLostTotalTitles.add(totalResultatCharges);

                AccountingBalanceViewTitle totalResultatBenefice = new AccountingBalanceViewTitle();
                totalResultatBenefice.setLabel("Bénéfice ou perte");
                totalResultatBenefice.setBrutN(totalResultatProduct.getBrutN() - totalResultatCharges.getBrutN());
                totalResultatBenefice.setBrutN1(totalResultatProduct.getBrutN1() - totalResultatCharges.getBrutN1());
                totalResultatBenefice.setSoldeN(totalResultatProduct.getSoldeN() - totalResultatCharges.getSoldeN());
                totalResultatBenefice.setSoldeN1(totalResultatProduct.getSoldeN1() - totalResultatCharges.getSoldeN1());
                profitAndLostTotalTitles.add(totalResultatBenefice);

                return profitAndLostTitles;
        }

        private AccountingBalanceViewItem getAccountingBalanceViewItemForProfitAndLostCharge(String label,
                        String paramBrut,
                        List<AccountingBalanceBilan> accountingRecords,
                        List<AccountingBalanceBilan> accountingRecordsN1,
                        AccountingBalanceViewTitle totalItem) {
                AccountingBalanceViewItem item = new AccountingBalanceViewItem();
                item.setBrutN(
                                getDebitForAccountRecordsAndAccountList(accountingRecords, paramBrut));
                item.setBrutN1(
                                getDebitForAccountRecordsAndAccountList(accountingRecordsN1,
                                                this.brut_immobilisations_financieres_autres));
                item.setAmortissementN(0f);
                item.setAmortissementN1(0f);
                item.setSoldeN(item.getBrutN());
                item.setSoldeN1(item.getBrutN1());

                if (totalItem != null) {
                        if (totalItem.getBrutN() == null)
                                totalItem.setBrutN(0f);

                        if (totalItem.getBrutN1() == null)
                                totalItem.setBrutN1(0f);

                        if (item.getBrutN() != null)
                                totalItem.setBrutN(totalItem.getBrutN() + item.getBrutN());
                        if (item.getBrutN1() != null)
                                totalItem.setBrutN1(totalItem.getBrutN1() + item.getBrutN1());
                }

                item.setLabel(label);
                return item;
        }

        private AccountingBalanceViewItem getAccountingBalanceViewItemForProfitAndLostProduct(String label,
                        String paramBrut,
                        List<AccountingBalanceBilan> accountingRecords,
                        List<AccountingBalanceBilan> accountingRecordsN1,
                        AccountingBalanceViewTitle totalItem) {
                AccountingBalanceViewItem item = new AccountingBalanceViewItem();
                item.setBrutN(
                                getCreditForAccountRecordsAndAccountList(accountingRecords, paramBrut));
                item.setBrutN1(
                                getCreditForAccountRecordsAndAccountList(accountingRecordsN1,
                                                this.brut_immobilisations_financieres_autres));
                item.setAmortissementN(0f);
                item.setAmortissementN1(0f);
                item.setSoldeN(item.getBrutN());
                item.setSoldeN1(item.getBrutN1());

                if (totalItem != null) {
                        if (totalItem.getBrutN() == null)
                                totalItem.setBrutN(0f);

                        if (totalItem.getBrutN1() == null)
                                totalItem.setBrutN1(0f);

                        if (item.getBrutN() != null)
                                totalItem.setBrutN(totalItem.getBrutN() + item.getBrutN());
                        if (item.getBrutN1() != null)
                                totalItem.setBrutN1(totalItem.getBrutN1() + item.getBrutN1());
                }
                item.setLabel(label);
                return item;
        }

        private Float getCreditForAccountRecordsAndAccountList(List<AccountingBalanceBilan> accountingRecords,
                        String accounts) {
                Float credit = 0f;
                HashSet<String> accountList = null;

                if (accounts == null || accounts.length() == 0)
                        return credit;

                accountList = new HashSet<String>(Arrays.asList(accounts.split(",")));

                if (accountingRecords != null && accountingRecords.size() > 0) {
                        for (AccountingBalanceBilan accountingRecord : accountingRecords) {
                                if (accountingRecord.getCreditAmount() == null
                                                || accountingRecord.getCreditAmount() == 0)
                                        continue;
                                boolean isNotExcluded = false;
                                for (String account : accountList) {
                                        if (!isAccountRecordExcluded(accountingRecord, account.trim(), true)) {
                                                isNotExcluded = true;
                                                break;
                                        }
                                }
                                if (isNotExcluded && accountingRecord.getCreditAmount() != null)
                                        credit += accountingRecord.getCreditAmount();
                                if (isNotExcluded && accountingRecord.getDebitAmount() != null)
                                        credit -= accountingRecord.getDebitAmount();
                        }
                }
                return credit;
        }

        private Float getDebitForAccountRecordsAndAccountList(List<AccountingBalanceBilan> accountingRecords,
                        String accounts) {
                Float debit = 0f;
                HashSet<String> accountList = null;

                if (accounts == null || accounts.length() == 0)
                        return debit;

                accountList = new HashSet<String>(Arrays.asList(accounts.split(",")));

                if (accountingRecords != null && accountingRecords.size() > 0) {
                        for (AccountingBalanceBilan accountingRecord : accountingRecords) {
                                if (accountingRecord.getCreditAmount() == null
                                                || accountingRecord.getCreditAmount() == 0)
                                        continue;
                                boolean isNotExcluded = false;
                                for (String account : accountList) {
                                        if (!isAccountRecordExcluded(accountingRecord, account.trim(), false)) {
                                                isNotExcluded = true;
                                                break;
                                        }
                                }
                                if (isNotExcluded && accountingRecord.getDebitAmount() != null)
                                        debit += accountingRecord.getDebitAmount();
                                if (isNotExcluded && accountingRecord.getCreditAmount() != null)
                                        debit -= accountingRecord.getCreditAmount();
                        }
                }
                return debit;
        }

        private boolean isAccountRecordExcluded(AccountingBalanceBilan accountingRecord, String account,
                        boolean isCredit) {

                // Detect debit / credit
                Boolean isAccountCredit = null;
                if (account.substring(account.length() - 1).equals("D"))
                        isAccountCredit = false;
                if (account.substring(account.length() - 1).equals("C"))
                        isAccountCredit = true;

                // Detect exclusion character
                boolean isExcluded = false;
                if (account.substring(account.length() - 1).equals("*"))
                        isExcluded = true;

                if (accountingRecord.getAccountingAccountNumber().equals(account) && isExcluded)
                        return true;

                if (accountingRecord.getAccountingAccountNumber().startsWith(account) && isExcluded)
                        return true;

                if (accountingRecord.getAccountingAccountNumber().equals(account) && isCredit
                                && isAccountCredit != null && isAccountCredit == false)
                        return true;

                if (accountingRecord.getAccountingAccountNumber().startsWith(account) && isCredit
                                && isAccountCredit != null && isAccountCredit == false)
                        return true;

                if (!accountingRecord.getAccountingAccountNumber().equals(account)
                                && !accountingRecord.getAccountingAccountNumber().startsWith(account))
                        return true;

                return false;
        }

}
