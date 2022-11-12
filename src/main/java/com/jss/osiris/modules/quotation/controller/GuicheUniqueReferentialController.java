package com.jss.osiris.modules.quotation.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ActiviteReguliere;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CapaciteEngagement;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeEEEPays;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeInseePays;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeInseePaysNaissance;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeNationalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodePays;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodeRolePersonneQualifiee;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.CodifNorme;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ConditionVersementTVA;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Destination;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DestinationEtablissement;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DestinationLocationGeranceMand;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DeviseCapital;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DiffusionINSEE;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.DocumentExtension;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Events;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ExerciceActivite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeExercice;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeExerciceActivitePrincipal;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeSociale;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Genre;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.JeuneAgriculteur;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.LieuDeLiquidation;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MineurLienParente;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MineurNationalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MineurSexe;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ModalitesDeControle;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.ModeExercice;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifCessation;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifDisparition;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifFinEirl;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifRejetCma;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifRejetGreffe;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifRejetMsa;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifTrasnfert;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureCessation;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureDesActivite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureDomaine;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureGerance;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureVoletSocial;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OptionEirl;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OptionJQPA;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OptionParticuliereRegimeBenefi;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OrganismeAssuranceMaladieActue;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.OrigineFusionScission;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Perimetre;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.PeriodiciteEtOptionsParticulie;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.PeriodiciteVersement;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.PrecisionActivite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.QualiteDeNonSedentarite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.QualiteNonSedentaire;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegimeImpositionBenefices;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegimeImpositionBenefices2;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegimeImpositionTVA;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegistreEirl;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegistreEirlDeLancienneEirl;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Role;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RoleConjoint;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RoleContrat;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RoleEntreprise;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RolePourEntreprise;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SecondRoleEntreprise;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SituationMatrimoniale;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SituationVisAVisMsa;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Status;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutContrat;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutDomaine;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutExerciceActiviteSimultan;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPourFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPourLaFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPourLaFormaliteBlocRe;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutPraticien;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.StatutVisAVisFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.SuccursaleOuFiliale;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TaciteReconduction;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TotalitePartie;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TutelleCuratelle;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDePersonne;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDeStatuts;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDissolution;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeExploitation;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeFormalite;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeLiasse;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeLocataireGerantMandataire;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeOrigine;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonne;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonneAncienExploitant;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonneBlocPreneurBail;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypePersonneContractante;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeRepresentant;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeVoie;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.ActiviteReguliereService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.CapaciteEngagementService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.CodeEEEPaysService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.CodeInseePaysNaissanceService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.CodeInseePaysService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.CodeNationaliteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.CodePaysService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.CodeRolePersonneQualifieeService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.CodifNormeService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.ConditionVersementTVAService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.DestinationEtablissementService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.DestinationLocationGeranceMandService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.DestinationService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.DeviseCapitalService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.DiffusionINSEEService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.DocumentExtensionService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.EventsService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.ExerciceActiviteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.FormeExerciceActivitePrincipalService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.FormeExerciceService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.FormeJuridiqueService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.FormeSocialeService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.GenreService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.JeuneAgriculteurService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.LieuDeLiquidationService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.MineurLienParenteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.MineurNationaliteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.MineurSexeService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.ModalitesDeControleService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.ModeExerciceService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.MotifCessationService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.MotifDisparitionService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.MotifFinEirlService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.MotifRejetCmaService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.MotifRejetGreffeService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.MotifRejetMsaService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.MotifTrasnfertService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.NatureCessationService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.NatureDesActiviteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.NatureDomaineService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.NatureGeranceService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.NatureVoletSocialService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.OptionEirlService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.OptionJQPAService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.OptionParticuliereRegimeBenefiService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.OrganismeAssuranceMaladieActueService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.OrigineFusionScissionService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.PerimetreService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.PeriodiciteEtOptionsParticulieService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.PeriodiciteVersementService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.PrecisionActiviteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.QualiteDeNonSedentariteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.QualiteNonSedentaireService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.RegimeImpositionBenefices2Service;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.RegimeImpositionBeneficesService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.RegimeImpositionTVAService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.RegistreEirlDeLancienneEirlService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.RegistreEirlService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.RoleConjointService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.RoleContratService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.RoleEntrepriseService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.RolePourEntrepriseService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.RoleService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.SecondRoleEntrepriseService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.SituationMatrimonialeService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.SituationVisAVisMsaService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatusService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatutContratService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatutDomaineService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatutExerciceActiviteSimultanService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatutFormaliteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatutPourFormaliteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatutPourLaFormaliteBlocReService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatutPourLaFormaliteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatutPraticienService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.StatutVisAVisFormaliteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.SuccursaleOuFilialeService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TaciteReconductionService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TotalitePartieService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TutelleCuratelleService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeDePersonneService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeDeStatutsService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeDissolutionService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeDocumentService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeExploitationService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeFormaliteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeLiasseService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeLocataireGerantMandataireService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeOrigineService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypePersonneAncienExploitantService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypePersonneBlocPreneurBailService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypePersonneContractanteService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypePersonneService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeRepresentantService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeVoieService;

@RestController
public class GuicheUniqueReferentialController {

    private static final String inputEntryPoint = "/quotation/guichet-unique";

    private static final Logger logger = LoggerFactory.getLogger(GuicheUniqueReferentialController.class);

    @Autowired
    TypeDocumentService typeDocumentService;

    @GetMapping(inputEntryPoint + "/type-document")
    public ResponseEntity<List<TypeDocument>> getTypeDocument() {
        List<TypeDocument> typeDocument = null;
        try {
            typeDocument = typeDocumentService.getTypeDocument();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeDocument", e);
            return new ResponseEntity<List<TypeDocument>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeDocument", e);
            return new ResponseEntity<List<TypeDocument>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeDocument>>(typeDocument, HttpStatus.OK);
    }

    @Autowired
    MotifRejetCmaService motifRejetCmaService;

    @GetMapping(inputEntryPoint + "/motif-rejet-cma")
    public ResponseEntity<List<MotifRejetCma>> getMotifRejetCma() {
        List<MotifRejetCma> motifRejetCma = null;
        try {
            motifRejetCma = motifRejetCmaService.getMotifRejetCma();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching motifRejetCma", e);
            return new ResponseEntity<List<MotifRejetCma>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching motifRejetCma", e);
            return new ResponseEntity<List<MotifRejetCma>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<MotifRejetCma>>(motifRejetCma, HttpStatus.OK);
    }

    @Autowired
    MotifRejetGreffeService motifRejetGreffeService;

    @GetMapping(inputEntryPoint + "/motif-rejet-greffe")
    public ResponseEntity<List<MotifRejetGreffe>> getMotifRejetGreffe() {
        List<MotifRejetGreffe> motifRejetGreffe = null;
        try {
            motifRejetGreffe = motifRejetGreffeService.getMotifRejetGreffe();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching motifRejetGreffe", e);
            return new ResponseEntity<List<MotifRejetGreffe>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching motifRejetGreffe", e);
            return new ResponseEntity<List<MotifRejetGreffe>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<MotifRejetGreffe>>(motifRejetGreffe, HttpStatus.OK);
    }

    @Autowired
    MotifRejetMsaService motifRejetMsaService;

    @GetMapping(inputEntryPoint + "/motif-rejet-msa")
    public ResponseEntity<List<MotifRejetMsa>> getMotifRejetMsa() {
        List<MotifRejetMsa> motifRejetMsa = null;
        try {
            motifRejetMsa = motifRejetMsaService.getMotifRejetMsa();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching motifRejetMsa", e);
            return new ResponseEntity<List<MotifRejetMsa>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching motifRejetMsa", e);
            return new ResponseEntity<List<MotifRejetMsa>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<MotifRejetMsa>>(motifRejetMsa, HttpStatus.OK);
    }

    @Autowired
    MotifTrasnfertService motifTrasnfertService;

    @GetMapping(inputEntryPoint + "/motif-trasnfert")
    public ResponseEntity<List<MotifTrasnfert>> getMotifTrasnfert() {
        List<MotifTrasnfert> motifTrasnfert = null;
        try {
            motifTrasnfert = motifTrasnfertService.getMotifTrasnfert();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching motifTrasnfert", e);
            return new ResponseEntity<List<MotifTrasnfert>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching motifTrasnfert", e);
            return new ResponseEntity<List<MotifTrasnfert>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<MotifTrasnfert>>(motifTrasnfert, HttpStatus.OK);
    }

    @Autowired
    ActiviteReguliereService activiteReguliereService;

    @GetMapping(inputEntryPoint + "/activite-reguliere")
    public ResponseEntity<List<ActiviteReguliere>> getActiviteReguliere() {
        List<ActiviteReguliere> activiteReguliere = null;
        try {
            activiteReguliere = activiteReguliereService.getActiviteReguliere();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching activiteReguliere", e);
            return new ResponseEntity<List<ActiviteReguliere>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching activiteReguliere", e);
            return new ResponseEntity<List<ActiviteReguliere>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<ActiviteReguliere>>(activiteReguliere, HttpStatus.OK);
    }

    @Autowired
    CapaciteEngagementService capaciteEngagementService;

    @GetMapping(inputEntryPoint + "/capacite-engagement")
    public ResponseEntity<List<CapaciteEngagement>> getCapaciteEngagement() {
        List<CapaciteEngagement> capaciteEngagement = null;
        try {
            capaciteEngagement = capaciteEngagementService.getCapaciteEngagement();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching capaciteEngagement", e);
            return new ResponseEntity<List<CapaciteEngagement>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching capaciteEngagement", e);
            return new ResponseEntity<List<CapaciteEngagement>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CapaciteEngagement>>(capaciteEngagement, HttpStatus.OK);
    }

    @Autowired
    CodeEEEPaysService codeEEEPaysService;

    @GetMapping(inputEntryPoint + "/code-eee-pays")
    public ResponseEntity<List<CodeEEEPays>> getCodeEEEPays() {
        List<CodeEEEPays> codeEEEPays = null;
        try {
            codeEEEPays = codeEEEPaysService.getCodeEEEPays();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching codeEEEPays", e);
            return new ResponseEntity<List<CodeEEEPays>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching codeEEEPays", e);
            return new ResponseEntity<List<CodeEEEPays>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CodeEEEPays>>(codeEEEPays, HttpStatus.OK);
    }

    @Autowired
    CodeInseePaysService codeInseePaysService;

    @GetMapping(inputEntryPoint + "/code-insee-pays")
    public ResponseEntity<List<CodeInseePays>> getCodeInseePays() {
        List<CodeInseePays> codeInseePays = null;
        try {
            codeInseePays = codeInseePaysService.getCodeInseePays();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching codeInseePays", e);
            return new ResponseEntity<List<CodeInseePays>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching codeInseePays", e);
            return new ResponseEntity<List<CodeInseePays>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CodeInseePays>>(codeInseePays, HttpStatus.OK);
    }

    @Autowired
    CodeInseePaysNaissanceService codeInseePaysNaissanceService;

    @GetMapping(inputEntryPoint + "/code-insee-pays-naissance")
    public ResponseEntity<List<CodeInseePaysNaissance>> getCodeInseePaysNaissance() {
        List<CodeInseePaysNaissance> codeInseePaysNaissance = null;
        try {
            codeInseePaysNaissance = codeInseePaysNaissanceService.getCodeInseePaysNaissance();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching codeInseePaysNaissance", e);
            return new ResponseEntity<List<CodeInseePaysNaissance>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching codeInseePaysNaissance", e);
            return new ResponseEntity<List<CodeInseePaysNaissance>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CodeInseePaysNaissance>>(codeInseePaysNaissance, HttpStatus.OK);
    }

    @Autowired
    CodeNationaliteService codeNationaliteService;

    @GetMapping(inputEntryPoint + "/code-nationalite")
    public ResponseEntity<List<CodeNationalite>> getCodeNationalite() {
        List<CodeNationalite> codeNationalite = null;
        try {
            codeNationalite = codeNationaliteService.getCodeNationalite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching codeNationalite", e);
            return new ResponseEntity<List<CodeNationalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching codeNationalite", e);
            return new ResponseEntity<List<CodeNationalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CodeNationalite>>(codeNationalite, HttpStatus.OK);
    }

    @Autowired
    CodePaysService codePaysService;

    @GetMapping(inputEntryPoint + "/code-pays")
    public ResponseEntity<List<CodePays>> getCodePays() {
        List<CodePays> codePays = null;
        try {
            codePays = codePaysService.getCodePays();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching codePays", e);
            return new ResponseEntity<List<CodePays>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching codePays", e);
            return new ResponseEntity<List<CodePays>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CodePays>>(codePays, HttpStatus.OK);
    }

    @Autowired
    CodeRolePersonneQualifieeService codeRolePersonneQualifieeService;

    @GetMapping(inputEntryPoint + "/code-role-personne-qualifiee")
    public ResponseEntity<List<CodeRolePersonneQualifiee>> getCodeRolePersonneQualifiee() {
        List<CodeRolePersonneQualifiee> codeRolePersonneQualifiee = null;
        try {
            codeRolePersonneQualifiee = codeRolePersonneQualifieeService.getCodeRolePersonneQualifiee();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching codeRolePersonneQualifiee", e);
            return new ResponseEntity<List<CodeRolePersonneQualifiee>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching codeRolePersonneQualifiee", e);
            return new ResponseEntity<List<CodeRolePersonneQualifiee>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CodeRolePersonneQualifiee>>(codeRolePersonneQualifiee, HttpStatus.OK);
    }

    @Autowired
    CodifNormeService codifNormeService;

    @GetMapping(inputEntryPoint + "/codif-norme")
    public ResponseEntity<List<CodifNorme>> getCodifNorme() {
        List<CodifNorme> codifNorme = null;
        try {
            codifNorme = codifNormeService.getCodifNorme();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching codifNorme", e);
            return new ResponseEntity<List<CodifNorme>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching codifNorme", e);
            return new ResponseEntity<List<CodifNorme>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<CodifNorme>>(codifNorme, HttpStatus.OK);
    }

    @Autowired
    ConditionVersementTVAService conditionVersementTVAService;

    @GetMapping(inputEntryPoint + "/condition-versement-tva")
    public ResponseEntity<List<ConditionVersementTVA>> getConditionVersementTVA() {
        List<ConditionVersementTVA> conditionVersementTVA = null;
        try {
            conditionVersementTVA = conditionVersementTVAService.getConditionVersementTVA();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching conditionVersementTVA", e);
            return new ResponseEntity<List<ConditionVersementTVA>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching conditionVersementTVA", e);
            return new ResponseEntity<List<ConditionVersementTVA>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<ConditionVersementTVA>>(conditionVersementTVA, HttpStatus.OK);
    }

    @Autowired
    DestinationService destinationService;

    @GetMapping(inputEntryPoint + "/destination")
    public ResponseEntity<List<Destination>> getDestination() {
        List<Destination> destination = null;
        try {
            destination = destinationService.getDestination();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching destination", e);
            return new ResponseEntity<List<Destination>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching destination", e);
            return new ResponseEntity<List<Destination>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Destination>>(destination, HttpStatus.OK);
    }

    @Autowired
    DestinationEtablissementService destinationEtablissementService;

    @GetMapping(inputEntryPoint + "/destination-etablissement")
    public ResponseEntity<List<DestinationEtablissement>> getDestinationEtablissement() {
        List<DestinationEtablissement> destinationEtablissement = null;
        try {
            destinationEtablissement = destinationEtablissementService.getDestinationEtablissement();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching destinationEtablissement", e);
            return new ResponseEntity<List<DestinationEtablissement>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching destinationEtablissement", e);
            return new ResponseEntity<List<DestinationEtablissement>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<DestinationEtablissement>>(destinationEtablissement, HttpStatus.OK);
    }

    @Autowired
    DestinationLocationGeranceMandService destinationLocationGeranceMandService;

    @GetMapping(inputEntryPoint + "/destination-location-gerance-mand")
    public ResponseEntity<List<DestinationLocationGeranceMand>> getDestinationLocationGeranceMand() {
        List<DestinationLocationGeranceMand> destinationLocationGeranceMand = null;
        try {
            destinationLocationGeranceMand = destinationLocationGeranceMandService.getDestinationLocationGeranceMand();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching destinationLocationGeranceMand", e);
            return new ResponseEntity<List<DestinationLocationGeranceMand>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching destinationLocationGeranceMand", e);
            return new ResponseEntity<List<DestinationLocationGeranceMand>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<DestinationLocationGeranceMand>>(destinationLocationGeranceMand, HttpStatus.OK);
    }

    @Autowired
    DeviseCapitalService deviseCapitalService;

    @GetMapping(inputEntryPoint + "/devise-capital")
    public ResponseEntity<List<DeviseCapital>> getDeviseCapital() {
        List<DeviseCapital> deviseCapital = null;
        try {
            deviseCapital = deviseCapitalService.getDeviseCapital();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching deviseCapital", e);
            return new ResponseEntity<List<DeviseCapital>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching deviseCapital", e);
            return new ResponseEntity<List<DeviseCapital>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<DeviseCapital>>(deviseCapital, HttpStatus.OK);
    }

    @Autowired
    DiffusionINSEEService diffusionINSEEService;

    @GetMapping(inputEntryPoint + "/diffusion-insee")
    public ResponseEntity<List<DiffusionINSEE>> getDiffusionINSEE() {
        List<DiffusionINSEE> diffusionINSEE = null;
        try {
            diffusionINSEE = diffusionINSEEService.getDiffusionINSEE();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching diffusionINSEE", e);
            return new ResponseEntity<List<DiffusionINSEE>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching diffusionINSEE", e);
            return new ResponseEntity<List<DiffusionINSEE>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<DiffusionINSEE>>(diffusionINSEE, HttpStatus.OK);
    }

    @Autowired
    DocumentExtensionService documentExtensionService;

    @GetMapping(inputEntryPoint + "/document-extension")
    public ResponseEntity<List<DocumentExtension>> getDocumentExtension() {
        List<DocumentExtension> documentExtension = null;
        try {
            documentExtension = documentExtensionService.getDocumentExtension();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching documentExtension", e);
            return new ResponseEntity<List<DocumentExtension>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching documentExtension", e);
            return new ResponseEntity<List<DocumentExtension>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<DocumentExtension>>(documentExtension, HttpStatus.OK);
    }

    @Autowired
    EventsService eventsService;

    @GetMapping(inputEntryPoint + "/events")
    public ResponseEntity<List<Events>> getEvents() {
        List<Events> events = null;
        try {
            events = eventsService.getEvents();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching events", e);
            return new ResponseEntity<List<Events>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching events", e);
            return new ResponseEntity<List<Events>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Events>>(events, HttpStatus.OK);
    }

    @Autowired
    ExerciceActiviteService exerciceActiviteService;

    @GetMapping(inputEntryPoint + "/exercice-activite")
    public ResponseEntity<List<ExerciceActivite>> getExerciceActivite() {
        List<ExerciceActivite> exerciceActivite = null;
        try {
            exerciceActivite = exerciceActiviteService.getExerciceActivite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching exerciceActivite", e);
            return new ResponseEntity<List<ExerciceActivite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching exerciceActivite", e);
            return new ResponseEntity<List<ExerciceActivite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<ExerciceActivite>>(exerciceActivite, HttpStatus.OK);
    }

    @Autowired
    FormeExerciceService formeExerciceService;

    @GetMapping(inputEntryPoint + "/forme-exercice")
    public ResponseEntity<List<FormeExercice>> getFormeExercice() {
        List<FormeExercice> formeExercice = null;
        try {
            formeExercice = formeExerciceService.getFormeExercice();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching formeExercice", e);
            return new ResponseEntity<List<FormeExercice>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching formeExercice", e);
            return new ResponseEntity<List<FormeExercice>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<FormeExercice>>(formeExercice, HttpStatus.OK);
    }

    @Autowired
    FormeExerciceActivitePrincipalService formeExerciceActivitePrincipalService;

    @GetMapping(inputEntryPoint + "/forme-exercice-activite-principal")
    public ResponseEntity<List<FormeExerciceActivitePrincipal>> getFormeExerciceActivitePrincipal() {
        List<FormeExerciceActivitePrincipal> formeExerciceActivitePrincipal = null;
        try {
            formeExerciceActivitePrincipal = formeExerciceActivitePrincipalService.getFormeExerciceActivitePrincipal();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching formeExerciceActivitePrincipal", e);
            return new ResponseEntity<List<FormeExerciceActivitePrincipal>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching formeExerciceActivitePrincipal", e);
            return new ResponseEntity<List<FormeExerciceActivitePrincipal>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<FormeExerciceActivitePrincipal>>(formeExerciceActivitePrincipal, HttpStatus.OK);
    }

    @Autowired
    FormeJuridiqueService formeJuridiqueService;

    @GetMapping(inputEntryPoint + "/forme-juridique")
    public ResponseEntity<List<FormeJuridique>> getFormeJuridique() {
        List<FormeJuridique> formeJuridique = null;
        try {
            formeJuridique = formeJuridiqueService.getFormeJuridique();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching formeJuridique", e);
            return new ResponseEntity<List<FormeJuridique>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching formeJuridique", e);
            return new ResponseEntity<List<FormeJuridique>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<FormeJuridique>>(formeJuridique, HttpStatus.OK);
    }

    @Autowired
    FormeSocialeService formeSocialeService;

    @GetMapping(inputEntryPoint + "/forme-sociale")
    public ResponseEntity<List<FormeSociale>> getFormeSociale() {
        List<FormeSociale> formeSociale = null;
        try {
            formeSociale = formeSocialeService.getFormeSociale();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching formeSociale", e);
            return new ResponseEntity<List<FormeSociale>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching formeSociale", e);
            return new ResponseEntity<List<FormeSociale>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<FormeSociale>>(formeSociale, HttpStatus.OK);
    }

    @Autowired
    GenreService genreService;

    @GetMapping(inputEntryPoint + "/genre")
    public ResponseEntity<List<Genre>> getGenre() {
        List<Genre> genre = null;
        try {
            genre = genreService.getGenre();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching genre", e);
            return new ResponseEntity<List<Genre>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching genre", e);
            return new ResponseEntity<List<Genre>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Genre>>(genre, HttpStatus.OK);
    }

    @Autowired
    JeuneAgriculteurService jeuneAgriculteurService;

    @GetMapping(inputEntryPoint + "/jeune-agriculteur")
    public ResponseEntity<List<JeuneAgriculteur>> getJeuneAgriculteur() {
        List<JeuneAgriculteur> jeuneAgriculteur = null;
        try {
            jeuneAgriculteur = jeuneAgriculteurService.getJeuneAgriculteur();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching jeuneAgriculteur", e);
            return new ResponseEntity<List<JeuneAgriculteur>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching jeuneAgriculteur", e);
            return new ResponseEntity<List<JeuneAgriculteur>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<JeuneAgriculteur>>(jeuneAgriculteur, HttpStatus.OK);
    }

    @Autowired
    LieuDeLiquidationService lieuDeLiquidationService;

    @GetMapping(inputEntryPoint + "/lieu-de-liquidation")
    public ResponseEntity<List<LieuDeLiquidation>> getLieuDeLiquidation() {
        List<LieuDeLiquidation> lieuDeLiquidation = null;
        try {
            lieuDeLiquidation = lieuDeLiquidationService.getLieuDeLiquidation();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching lieuDeLiquidation", e);
            return new ResponseEntity<List<LieuDeLiquidation>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching lieuDeLiquidation", e);
            return new ResponseEntity<List<LieuDeLiquidation>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<LieuDeLiquidation>>(lieuDeLiquidation, HttpStatus.OK);
    }

    @Autowired
    MineurLienParenteService mineurLienParenteService;

    @GetMapping(inputEntryPoint + "/mineur-lien-parente")
    public ResponseEntity<List<MineurLienParente>> getMineurLienParente() {
        List<MineurLienParente> mineurLienParente = null;
        try {
            mineurLienParente = mineurLienParenteService.getMineurLienParente();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching mineurLienParente", e);
            return new ResponseEntity<List<MineurLienParente>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching mineurLienParente", e);
            return new ResponseEntity<List<MineurLienParente>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<MineurLienParente>>(mineurLienParente, HttpStatus.OK);
    }

    @Autowired
    MineurNationaliteService mineurNationaliteService;

    @GetMapping(inputEntryPoint + "/mineur-nationalite")
    public ResponseEntity<List<MineurNationalite>> getMineurNationalite() {
        List<MineurNationalite> mineurNationalite = null;
        try {
            mineurNationalite = mineurNationaliteService.getMineurNationalite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching mineurNationalite", e);
            return new ResponseEntity<List<MineurNationalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching mineurNationalite", e);
            return new ResponseEntity<List<MineurNationalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<MineurNationalite>>(mineurNationalite, HttpStatus.OK);
    }

    @Autowired
    MineurSexeService mineurSexeService;

    @GetMapping(inputEntryPoint + "/mineur-sexe")
    public ResponseEntity<List<MineurSexe>> getMineurSexe() {
        List<MineurSexe> mineurSexe = null;
        try {
            mineurSexe = mineurSexeService.getMineurSexe();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching mineurSexe", e);
            return new ResponseEntity<List<MineurSexe>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching mineurSexe", e);
            return new ResponseEntity<List<MineurSexe>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<MineurSexe>>(mineurSexe, HttpStatus.OK);
    }

    @Autowired
    ModalitesDeControleService modalitesDeControleService;

    @GetMapping(inputEntryPoint + "/modalites-de-controle")
    public ResponseEntity<List<ModalitesDeControle>> getModalitesDeControle() {
        List<ModalitesDeControle> modalitesDeControle = null;
        try {
            modalitesDeControle = modalitesDeControleService.getModalitesDeControle();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching modalitesDeControle", e);
            return new ResponseEntity<List<ModalitesDeControle>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching modalitesDeControle", e);
            return new ResponseEntity<List<ModalitesDeControle>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<ModalitesDeControle>>(modalitesDeControle, HttpStatus.OK);
    }

    @Autowired
    ModeExerciceService modeExerciceService;

    @GetMapping(inputEntryPoint + "/mode-exercice")
    public ResponseEntity<List<ModeExercice>> getModeExercice() {
        List<ModeExercice> modeExercice = null;
        try {
            modeExercice = modeExerciceService.getModeExercice();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching modeExercice", e);
            return new ResponseEntity<List<ModeExercice>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching modeExercice", e);
            return new ResponseEntity<List<ModeExercice>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<ModeExercice>>(modeExercice, HttpStatus.OK);
    }

    @Autowired
    MotifCessationService motifCessationService;

    @GetMapping(inputEntryPoint + "/motif-cessation")
    public ResponseEntity<List<MotifCessation>> getMotifCessation() {
        List<MotifCessation> motifCessation = null;
        try {
            motifCessation = motifCessationService.getMotifCessation();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching motifCessation", e);
            return new ResponseEntity<List<MotifCessation>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching motifCessation", e);
            return new ResponseEntity<List<MotifCessation>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<MotifCessation>>(motifCessation, HttpStatus.OK);
    }

    @Autowired
    MotifDisparitionService motifDisparitionService;

    @GetMapping(inputEntryPoint + "/motif-disparition")
    public ResponseEntity<List<MotifDisparition>> getMotifDisparition() {
        List<MotifDisparition> motifDisparition = null;
        try {
            motifDisparition = motifDisparitionService.getMotifDisparition();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching motifDisparition", e);
            return new ResponseEntity<List<MotifDisparition>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching motifDisparition", e);
            return new ResponseEntity<List<MotifDisparition>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<MotifDisparition>>(motifDisparition, HttpStatus.OK);
    }

    @Autowired
    MotifFinEirlService motifFinEirlService;

    @GetMapping(inputEntryPoint + "/motif-fin-eirl")
    public ResponseEntity<List<MotifFinEirl>> getMotifFinEirl() {
        List<MotifFinEirl> motifFinEirl = null;
        try {
            motifFinEirl = motifFinEirlService.getMotifFinEirl();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching motifFinEirl", e);
            return new ResponseEntity<List<MotifFinEirl>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching motifFinEirl", e);
            return new ResponseEntity<List<MotifFinEirl>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<MotifFinEirl>>(motifFinEirl, HttpStatus.OK);
    }

    @Autowired
    NatureCessationService natureCessationService;

    @GetMapping(inputEntryPoint + "/nature-cessation")
    public ResponseEntity<List<NatureCessation>> getNatureCessation() {
        List<NatureCessation> natureCessation = null;
        try {
            natureCessation = natureCessationService.getNatureCessation();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching natureCessation", e);
            return new ResponseEntity<List<NatureCessation>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching natureCessation", e);
            return new ResponseEntity<List<NatureCessation>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<NatureCessation>>(natureCessation, HttpStatus.OK);
    }

    @Autowired
    NatureDesActiviteService natureDesActiviteService;

    @GetMapping(inputEntryPoint + "/nature-des-activite")
    public ResponseEntity<List<NatureDesActivite>> getNatureDesActivite() {
        List<NatureDesActivite> natureDesActivite = null;
        try {
            natureDesActivite = natureDesActiviteService.getNatureDesActivite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching natureDesActivite", e);
            return new ResponseEntity<List<NatureDesActivite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching natureDesActivite", e);
            return new ResponseEntity<List<NatureDesActivite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<NatureDesActivite>>(natureDesActivite, HttpStatus.OK);
    }

    @Autowired
    NatureDomaineService natureDomaineService;

    @GetMapping(inputEntryPoint + "/nature-domaine")
    public ResponseEntity<List<NatureDomaine>> getNatureDomaine() {
        List<NatureDomaine> natureDomaine = null;
        try {
            natureDomaine = natureDomaineService.getNatureDomaine();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching natureDomaine", e);
            return new ResponseEntity<List<NatureDomaine>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching natureDomaine", e);
            return new ResponseEntity<List<NatureDomaine>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<NatureDomaine>>(natureDomaine, HttpStatus.OK);
    }

    @Autowired
    NatureGeranceService natureGeranceService;

    @GetMapping(inputEntryPoint + "/nature-gerance")
    public ResponseEntity<List<NatureGerance>> getNatureGerance() {
        List<NatureGerance> natureGerance = null;
        try {
            natureGerance = natureGeranceService.getNatureGerance();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching natureGerance", e);
            return new ResponseEntity<List<NatureGerance>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching natureGerance", e);
            return new ResponseEntity<List<NatureGerance>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<NatureGerance>>(natureGerance, HttpStatus.OK);
    }

    @Autowired
    NatureVoletSocialService natureVoletSocialService;

    @GetMapping(inputEntryPoint + "/nature-volet-social")
    public ResponseEntity<List<NatureVoletSocial>> getNatureVoletSocial() {
        List<NatureVoletSocial> natureVoletSocial = null;
        try {
            natureVoletSocial = natureVoletSocialService.getNatureVoletSocial();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching natureVoletSocial", e);
            return new ResponseEntity<List<NatureVoletSocial>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching natureVoletSocial", e);
            return new ResponseEntity<List<NatureVoletSocial>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<NatureVoletSocial>>(natureVoletSocial, HttpStatus.OK);
    }

    @Autowired
    OptionEirlService optionEirlService;

    @GetMapping(inputEntryPoint + "/option-eirl")
    public ResponseEntity<List<OptionEirl>> getOptionEirl() {
        List<OptionEirl> optionEirl = null;
        try {
            optionEirl = optionEirlService.getOptionEirl();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching optionEirl", e);
            return new ResponseEntity<List<OptionEirl>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching optionEirl", e);
            return new ResponseEntity<List<OptionEirl>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<OptionEirl>>(optionEirl, HttpStatus.OK);
    }

    @Autowired
    OptionJQPAService optionJQPAService;

    @GetMapping(inputEntryPoint + "/option-jqpa")
    public ResponseEntity<List<OptionJQPA>> getOptionJQPA() {
        List<OptionJQPA> optionJQPA = null;
        try {
            optionJQPA = optionJQPAService.getOptionJQPA();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching optionJQPA", e);
            return new ResponseEntity<List<OptionJQPA>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching optionJQPA", e);
            return new ResponseEntity<List<OptionJQPA>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<OptionJQPA>>(optionJQPA, HttpStatus.OK);
    }

    @Autowired
    OptionParticuliereRegimeBenefiService optionParticuliereRegimeBenefiService;

    @GetMapping(inputEntryPoint + "/option-particuliere-regime-benefi")
    public ResponseEntity<List<OptionParticuliereRegimeBenefi>> getOptionParticuliereRegimeBenefi() {
        List<OptionParticuliereRegimeBenefi> optionParticuliereRegimeBenefi = null;
        try {
            optionParticuliereRegimeBenefi = optionParticuliereRegimeBenefiService.getOptionParticuliereRegimeBenefi();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching optionParticuliereRegimeBenefi", e);
            return new ResponseEntity<List<OptionParticuliereRegimeBenefi>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching optionParticuliereRegimeBenefi", e);
            return new ResponseEntity<List<OptionParticuliereRegimeBenefi>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<OptionParticuliereRegimeBenefi>>(optionParticuliereRegimeBenefi, HttpStatus.OK);
    }

    @Autowired
    OrganismeAssuranceMaladieActueService organismeAssuranceMaladieActueService;

    @GetMapping(inputEntryPoint + "/organisme-assurance-maladie-actue")
    public ResponseEntity<List<OrganismeAssuranceMaladieActue>> getOrganismeAssuranceMaladieActue() {
        List<OrganismeAssuranceMaladieActue> organismeAssuranceMaladieActue = null;
        try {
            organismeAssuranceMaladieActue = organismeAssuranceMaladieActueService.getOrganismeAssuranceMaladieActue();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching organismeAssuranceMaladieActue", e);
            return new ResponseEntity<List<OrganismeAssuranceMaladieActue>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching organismeAssuranceMaladieActue", e);
            return new ResponseEntity<List<OrganismeAssuranceMaladieActue>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<OrganismeAssuranceMaladieActue>>(organismeAssuranceMaladieActue, HttpStatus.OK);
    }

    @Autowired
    OrigineFusionScissionService origineFusionScissionService;

    @GetMapping(inputEntryPoint + "/origine-fusion-scission")
    public ResponseEntity<List<OrigineFusionScission>> getOrigineFusionScission() {
        List<OrigineFusionScission> origineFusionScission = null;
        try {
            origineFusionScission = origineFusionScissionService.getOrigineFusionScission();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching origineFusionScission", e);
            return new ResponseEntity<List<OrigineFusionScission>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching origineFusionScission", e);
            return new ResponseEntity<List<OrigineFusionScission>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<OrigineFusionScission>>(origineFusionScission, HttpStatus.OK);
    }

    @Autowired
    PerimetreService perimetreService;

    @GetMapping(inputEntryPoint + "/perimetre")
    public ResponseEntity<List<Perimetre>> getPerimetre() {
        List<Perimetre> perimetre = null;
        try {
            perimetre = perimetreService.getPerimetre();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching perimetre", e);
            return new ResponseEntity<List<Perimetre>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching perimetre", e);
            return new ResponseEntity<List<Perimetre>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Perimetre>>(perimetre, HttpStatus.OK);
    }

    @Autowired
    PeriodiciteEtOptionsParticulieService periodiciteEtOptionsParticulieService;

    @GetMapping(inputEntryPoint + "/periodicite-et-options-particulie")
    public ResponseEntity<List<PeriodiciteEtOptionsParticulie>> getPeriodiciteEtOptionsParticulie() {
        List<PeriodiciteEtOptionsParticulie> periodiciteEtOptionsParticulie = null;
        try {
            periodiciteEtOptionsParticulie = periodiciteEtOptionsParticulieService.getPeriodiciteEtOptionsParticulie();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching periodiciteEtOptionsParticulie", e);
            return new ResponseEntity<List<PeriodiciteEtOptionsParticulie>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching periodiciteEtOptionsParticulie", e);
            return new ResponseEntity<List<PeriodiciteEtOptionsParticulie>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<PeriodiciteEtOptionsParticulie>>(periodiciteEtOptionsParticulie, HttpStatus.OK);
    }

    @Autowired
    PeriodiciteVersementService periodiciteVersementService;

    @GetMapping(inputEntryPoint + "/periodicite-versement")
    public ResponseEntity<List<PeriodiciteVersement>> getPeriodiciteVersement() {
        List<PeriodiciteVersement> periodiciteVersement = null;
        try {
            periodiciteVersement = periodiciteVersementService.getPeriodiciteVersement();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching periodiciteVersement", e);
            return new ResponseEntity<List<PeriodiciteVersement>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching periodiciteVersement", e);
            return new ResponseEntity<List<PeriodiciteVersement>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<PeriodiciteVersement>>(periodiciteVersement, HttpStatus.OK);
    }

    @Autowired
    PrecisionActiviteService precisionActiviteService;

    @GetMapping(inputEntryPoint + "/precision-activite")
    public ResponseEntity<List<PrecisionActivite>> getPrecisionActivite() {
        List<PrecisionActivite> precisionActivite = null;
        try {
            precisionActivite = precisionActiviteService.getPrecisionActivite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching precisionActivite", e);
            return new ResponseEntity<List<PrecisionActivite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching precisionActivite", e);
            return new ResponseEntity<List<PrecisionActivite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<PrecisionActivite>>(precisionActivite, HttpStatus.OK);
    }

    @Autowired
    QualiteDeNonSedentariteService qualiteDeNonSedentariteService;

    @GetMapping(inputEntryPoint + "/qualite-de-non-sedentarite")
    public ResponseEntity<List<QualiteDeNonSedentarite>> getQualiteDeNonSedentarite() {
        List<QualiteDeNonSedentarite> qualiteDeNonSedentarite = null;
        try {
            qualiteDeNonSedentarite = qualiteDeNonSedentariteService.getQualiteDeNonSedentarite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching qualiteDeNonSedentarite", e);
            return new ResponseEntity<List<QualiteDeNonSedentarite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching qualiteDeNonSedentarite", e);
            return new ResponseEntity<List<QualiteDeNonSedentarite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<QualiteDeNonSedentarite>>(qualiteDeNonSedentarite, HttpStatus.OK);
    }

    @Autowired
    QualiteNonSedentaireService qualiteNonSedentaireService;

    @GetMapping(inputEntryPoint + "/qualite-non-sedentaire")
    public ResponseEntity<List<QualiteNonSedentaire>> getQualiteNonSedentaire() {
        List<QualiteNonSedentaire> qualiteNonSedentaire = null;
        try {
            qualiteNonSedentaire = qualiteNonSedentaireService.getQualiteNonSedentaire();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching qualiteNonSedentaire", e);
            return new ResponseEntity<List<QualiteNonSedentaire>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching qualiteNonSedentaire", e);
            return new ResponseEntity<List<QualiteNonSedentaire>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<QualiteNonSedentaire>>(qualiteNonSedentaire, HttpStatus.OK);
    }

    @Autowired
    RegimeImpositionBeneficesService regimeImpositionBeneficesService;

    @GetMapping(inputEntryPoint + "/regime-imposition-benefices")
    public ResponseEntity<List<RegimeImpositionBenefices>> getRegimeImpositionBenefices() {
        List<RegimeImpositionBenefices> regimeImpositionBenefices = null;
        try {
            regimeImpositionBenefices = regimeImpositionBeneficesService.getRegimeImpositionBenefices();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching regimeImpositionBenefices", e);
            return new ResponseEntity<List<RegimeImpositionBenefices>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching regimeImpositionBenefices", e);
            return new ResponseEntity<List<RegimeImpositionBenefices>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RegimeImpositionBenefices>>(regimeImpositionBenefices, HttpStatus.OK);
    }

    @Autowired
    RegimeImpositionBenefices2Service regimeImpositionBenefices2Service;

    @GetMapping(inputEntryPoint + "/regime-imposition-benefices2")
    public ResponseEntity<List<RegimeImpositionBenefices2>> getRegimeImpositionBenefices2() {
        List<RegimeImpositionBenefices2> regimeImpositionBenefices2 = null;
        try {
            regimeImpositionBenefices2 = regimeImpositionBenefices2Service.getRegimeImpositionBenefices2();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching regimeImpositionBenefices2", e);
            return new ResponseEntity<List<RegimeImpositionBenefices2>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching regimeImpositionBenefices2", e);
            return new ResponseEntity<List<RegimeImpositionBenefices2>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RegimeImpositionBenefices2>>(regimeImpositionBenefices2, HttpStatus.OK);
    }

    @Autowired
    RegimeImpositionTVAService regimeImpositionTVAService;

    @GetMapping(inputEntryPoint + "/regime-imposition-tva")
    public ResponseEntity<List<RegimeImpositionTVA>> getRegimeImpositionTVA() {
        List<RegimeImpositionTVA> regimeImpositionTVA = null;
        try {
            regimeImpositionTVA = regimeImpositionTVAService.getRegimeImpositionTVA();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching regimeImpositionTVA", e);
            return new ResponseEntity<List<RegimeImpositionTVA>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching regimeImpositionTVA", e);
            return new ResponseEntity<List<RegimeImpositionTVA>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RegimeImpositionTVA>>(regimeImpositionTVA, HttpStatus.OK);
    }

    @Autowired
    RegistreEirlService registreEirlService;

    @GetMapping(inputEntryPoint + "/registre-eirl")
    public ResponseEntity<List<RegistreEirl>> getRegistreEirl() {
        List<RegistreEirl> registreEirl = null;
        try {
            registreEirl = registreEirlService.getRegistreEirl();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching registreEirl", e);
            return new ResponseEntity<List<RegistreEirl>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching registreEirl", e);
            return new ResponseEntity<List<RegistreEirl>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RegistreEirl>>(registreEirl, HttpStatus.OK);
    }

    @Autowired
    RegistreEirlDeLancienneEirlService registreEirlDeLancienneEirlService;

    @GetMapping(inputEntryPoint + "/registre-eirl-de-lancienne-eirl")
    public ResponseEntity<List<RegistreEirlDeLancienneEirl>> getRegistreEirlDeLancienneEirl() {
        List<RegistreEirlDeLancienneEirl> registreEirlDeLancienneEirl = null;
        try {
            registreEirlDeLancienneEirl = registreEirlDeLancienneEirlService.getRegistreEirlDeLancienneEirl();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching registreEirlDeLancienneEirl", e);
            return new ResponseEntity<List<RegistreEirlDeLancienneEirl>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching registreEirlDeLancienneEirl", e);
            return new ResponseEntity<List<RegistreEirlDeLancienneEirl>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RegistreEirlDeLancienneEirl>>(registreEirlDeLancienneEirl, HttpStatus.OK);
    }

    @Autowired
    RoleService roleService;

    @GetMapping(inputEntryPoint + "/role")
    public ResponseEntity<List<Role>> getRole() {
        List<Role> role = null;
        try {
            role = roleService.getRole();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching role", e);
            return new ResponseEntity<List<Role>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching role", e);
            return new ResponseEntity<List<Role>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Role>>(role, HttpStatus.OK);
    }

    @Autowired
    RoleConjointService roleConjointService;

    @GetMapping(inputEntryPoint + "/role-conjoint")
    public ResponseEntity<List<RoleConjoint>> getRoleConjoint() {
        List<RoleConjoint> roleConjoint = null;
        try {
            roleConjoint = roleConjointService.getRoleConjoint();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching roleConjoint", e);
            return new ResponseEntity<List<RoleConjoint>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching roleConjoint", e);
            return new ResponseEntity<List<RoleConjoint>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RoleConjoint>>(roleConjoint, HttpStatus.OK);
    }

    @Autowired
    RoleContratService roleContratService;

    @GetMapping(inputEntryPoint + "/role-contrat")
    public ResponseEntity<List<RoleContrat>> getRoleContrat() {
        List<RoleContrat> roleContrat = null;
        try {
            roleContrat = roleContratService.getRoleContrat();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching roleContrat", e);
            return new ResponseEntity<List<RoleContrat>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching roleContrat", e);
            return new ResponseEntity<List<RoleContrat>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RoleContrat>>(roleContrat, HttpStatus.OK);
    }

    @Autowired
    RoleEntrepriseService roleEntrepriseService;

    @GetMapping(inputEntryPoint + "/role-entreprise")
    public ResponseEntity<List<RoleEntreprise>> getRoleEntreprise() {
        List<RoleEntreprise> roleEntreprise = null;
        try {
            roleEntreprise = roleEntrepriseService.getRoleEntreprise();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching roleEntreprise", e);
            return new ResponseEntity<List<RoleEntreprise>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching roleEntreprise", e);
            return new ResponseEntity<List<RoleEntreprise>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RoleEntreprise>>(roleEntreprise, HttpStatus.OK);
    }

    @Autowired
    RolePourEntrepriseService rolePourEntrepriseService;

    @GetMapping(inputEntryPoint + "/role-pour-entreprise")
    public ResponseEntity<List<RolePourEntreprise>> getRolePourEntreprise() {
        List<RolePourEntreprise> rolePourEntreprise = null;
        try {
            rolePourEntreprise = rolePourEntrepriseService.getRolePourEntreprise();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching rolePourEntreprise", e);
            return new ResponseEntity<List<RolePourEntreprise>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching rolePourEntreprise", e);
            return new ResponseEntity<List<RolePourEntreprise>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<RolePourEntreprise>>(rolePourEntreprise, HttpStatus.OK);
    }

    @Autowired
    SecondRoleEntrepriseService secondRoleEntrepriseService;

    @GetMapping(inputEntryPoint + "/second-role-entreprise")
    public ResponseEntity<List<SecondRoleEntreprise>> getSecondRoleEntreprise() {
        List<SecondRoleEntreprise> secondRoleEntreprise = null;
        try {
            secondRoleEntreprise = secondRoleEntrepriseService.getSecondRoleEntreprise();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching secondRoleEntreprise", e);
            return new ResponseEntity<List<SecondRoleEntreprise>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching secondRoleEntreprise", e);
            return new ResponseEntity<List<SecondRoleEntreprise>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<SecondRoleEntreprise>>(secondRoleEntreprise, HttpStatus.OK);
    }

    @Autowired
    SituationMatrimonialeService situationMatrimonialeService;

    @GetMapping(inputEntryPoint + "/situation-matrimoniale")
    public ResponseEntity<List<SituationMatrimoniale>> getSituationMatrimoniale() {
        List<SituationMatrimoniale> situationMatrimoniale = null;
        try {
            situationMatrimoniale = situationMatrimonialeService.getSituationMatrimoniale();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching situationMatrimoniale", e);
            return new ResponseEntity<List<SituationMatrimoniale>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching situationMatrimoniale", e);
            return new ResponseEntity<List<SituationMatrimoniale>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<SituationMatrimoniale>>(situationMatrimoniale, HttpStatus.OK);
    }

    @Autowired
    SituationVisAVisMsaService situationVisAVisMsaService;

    @GetMapping(inputEntryPoint + "/situation-visavis-msa")
    public ResponseEntity<List<SituationVisAVisMsa>> getSituationVisAVisMsa() {
        List<SituationVisAVisMsa> situationVisAVisMsa = null;
        try {
            situationVisAVisMsa = situationVisAVisMsaService.getSituationVisAVisMsa();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching situationVisAVisMsa", e);
            return new ResponseEntity<List<SituationVisAVisMsa>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching situationVisAVisMsa", e);
            return new ResponseEntity<List<SituationVisAVisMsa>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<SituationVisAVisMsa>>(situationVisAVisMsa, HttpStatus.OK);
    }

    @Autowired
    StatusService statusService;

    @GetMapping(inputEntryPoint + "/status")
    public ResponseEntity<List<Status>> getStatus() {
        List<Status> status = null;
        try {
            status = statusService.getStatus();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching status", e);
            return new ResponseEntity<List<Status>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching status", e);
            return new ResponseEntity<List<Status>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<Status>>(status, HttpStatus.OK);
    }

    @Autowired
    StatutContratService statutContratService;

    @GetMapping(inputEntryPoint + "/statut-contrat")
    public ResponseEntity<List<StatutContrat>> getStatutContrat() {
        List<StatutContrat> statutContrat = null;
        try {
            statutContrat = statutContratService.getStatutContrat();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching statutContrat", e);
            return new ResponseEntity<List<StatutContrat>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching statutContrat", e);
            return new ResponseEntity<List<StatutContrat>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<StatutContrat>>(statutContrat, HttpStatus.OK);
    }

    @Autowired
    StatutDomaineService statutDomaineService;

    @GetMapping(inputEntryPoint + "/statut-domaine")
    public ResponseEntity<List<StatutDomaine>> getStatutDomaine() {
        List<StatutDomaine> statutDomaine = null;
        try {
            statutDomaine = statutDomaineService.getStatutDomaine();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching statutDomaine", e);
            return new ResponseEntity<List<StatutDomaine>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching statutDomaine", e);
            return new ResponseEntity<List<StatutDomaine>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<StatutDomaine>>(statutDomaine, HttpStatus.OK);
    }

    @Autowired
    StatutExerciceActiviteSimultanService statutExerciceActiviteSimultanService;

    @GetMapping(inputEntryPoint + "/statut-exercice-activite-simultan")
    public ResponseEntity<List<StatutExerciceActiviteSimultan>> getStatutExerciceActiviteSimultan() {
        List<StatutExerciceActiviteSimultan> statutExerciceActiviteSimultan = null;
        try {
            statutExerciceActiviteSimultan = statutExerciceActiviteSimultanService.getStatutExerciceActiviteSimultan();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching statutExerciceActiviteSimultan", e);
            return new ResponseEntity<List<StatutExerciceActiviteSimultan>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching statutExerciceActiviteSimultan", e);
            return new ResponseEntity<List<StatutExerciceActiviteSimultan>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<StatutExerciceActiviteSimultan>>(statutExerciceActiviteSimultan, HttpStatus.OK);
    }

    @Autowired
    StatutFormaliteService statutFormaliteService;

    @GetMapping(inputEntryPoint + "/statut-formalite")
    public ResponseEntity<List<StatutFormalite>> getStatutFormalite() {
        List<StatutFormalite> statutFormalite = null;
        try {
            statutFormalite = statutFormaliteService.getStatutFormalite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching statutFormalite", e);
            return new ResponseEntity<List<StatutFormalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching statutFormalite", e);
            return new ResponseEntity<List<StatutFormalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<StatutFormalite>>(statutFormalite, HttpStatus.OK);
    }

    @Autowired
    StatutPourFormaliteService statutPourFormaliteService;

    @GetMapping(inputEntryPoint + "/statut-pour-formalite")
    public ResponseEntity<List<StatutPourFormalite>> getStatutPourFormalite() {
        List<StatutPourFormalite> statutPourFormalite = null;
        try {
            statutPourFormalite = statutPourFormaliteService.getStatutPourFormalite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching statutPourFormalite", e);
            return new ResponseEntity<List<StatutPourFormalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching statutPourFormalite", e);
            return new ResponseEntity<List<StatutPourFormalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<StatutPourFormalite>>(statutPourFormalite, HttpStatus.OK);
    }

    @Autowired
    StatutPourLaFormaliteService statutPourLaFormaliteService;

    @GetMapping(inputEntryPoint + "/statut-pour-la-formalite")
    public ResponseEntity<List<StatutPourLaFormalite>> getStatutPourLaFormalite() {
        List<StatutPourLaFormalite> statutPourLaFormalite = null;
        try {
            statutPourLaFormalite = statutPourLaFormaliteService.getStatutPourLaFormalite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching statutPourLaFormalite", e);
            return new ResponseEntity<List<StatutPourLaFormalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching statutPourLaFormalite", e);
            return new ResponseEntity<List<StatutPourLaFormalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<StatutPourLaFormalite>>(statutPourLaFormalite, HttpStatus.OK);
    }

    @Autowired
    StatutPourLaFormaliteBlocReService statutPourLaFormaliteBlocReService;

    @GetMapping(inputEntryPoint + "/statut-pour-la-formalite-bloc-re")
    public ResponseEntity<List<StatutPourLaFormaliteBlocRe>> getStatutPourLaFormaliteBlocRe() {
        List<StatutPourLaFormaliteBlocRe> statutPourLaFormaliteBlocRe = null;
        try {
            statutPourLaFormaliteBlocRe = statutPourLaFormaliteBlocReService.getStatutPourLaFormaliteBlocRe();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching statutPourLaFormaliteBlocRe", e);
            return new ResponseEntity<List<StatutPourLaFormaliteBlocRe>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching statutPourLaFormaliteBlocRe", e);
            return new ResponseEntity<List<StatutPourLaFormaliteBlocRe>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<StatutPourLaFormaliteBlocRe>>(statutPourLaFormaliteBlocRe, HttpStatus.OK);
    }

    @Autowired
    StatutPraticienService statutPraticienService;

    @GetMapping(inputEntryPoint + "/statut-praticien")
    public ResponseEntity<List<StatutPraticien>> getStatutPraticien() {
        List<StatutPraticien> statutPraticien = null;
        try {
            statutPraticien = statutPraticienService.getStatutPraticien();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching statutPraticien", e);
            return new ResponseEntity<List<StatutPraticien>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching statutPraticien", e);
            return new ResponseEntity<List<StatutPraticien>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<StatutPraticien>>(statutPraticien, HttpStatus.OK);
    }

    @Autowired
    StatutVisAVisFormaliteService statutVisAVisFormaliteService;

    @GetMapping(inputEntryPoint + "/statut-visavis-formalite")
    public ResponseEntity<List<StatutVisAVisFormalite>> getStatutVisAVisFormalite() {
        List<StatutVisAVisFormalite> statutVisAVisFormalite = null;
        try {
            statutVisAVisFormalite = statutVisAVisFormaliteService.getStatutVisAVisFormalite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching statutVisAVisFormalite", e);
            return new ResponseEntity<List<StatutVisAVisFormalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching statutVisAVisFormalite", e);
            return new ResponseEntity<List<StatutVisAVisFormalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<StatutVisAVisFormalite>>(statutVisAVisFormalite, HttpStatus.OK);
    }

    @Autowired
    SuccursaleOuFilialeService succursaleOuFilialeService;

    @GetMapping(inputEntryPoint + "/succursale-ou-filiale")
    public ResponseEntity<List<SuccursaleOuFiliale>> getSuccursaleOuFiliale() {
        List<SuccursaleOuFiliale> succursaleOuFiliale = null;
        try {
            succursaleOuFiliale = succursaleOuFilialeService.getSuccursaleOuFiliale();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching succursaleOuFiliale", e);
            return new ResponseEntity<List<SuccursaleOuFiliale>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching succursaleOuFiliale", e);
            return new ResponseEntity<List<SuccursaleOuFiliale>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<SuccursaleOuFiliale>>(succursaleOuFiliale, HttpStatus.OK);
    }

    @Autowired
    TaciteReconductionService taciteReconductionService;

    @GetMapping(inputEntryPoint + "/tacite-reconduction")
    public ResponseEntity<List<TaciteReconduction>> getTaciteReconduction() {
        List<TaciteReconduction> taciteReconduction = null;
        try {
            taciteReconduction = taciteReconductionService.getTaciteReconduction();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching taciteReconduction", e);
            return new ResponseEntity<List<TaciteReconduction>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching taciteReconduction", e);
            return new ResponseEntity<List<TaciteReconduction>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TaciteReconduction>>(taciteReconduction, HttpStatus.OK);
    }

    @Autowired
    TotalitePartieService totalitePartieService;

    @GetMapping(inputEntryPoint + "/totalite-partie")
    public ResponseEntity<List<TotalitePartie>> getTotalitePartie() {
        List<TotalitePartie> totalitePartie = null;
        try {
            totalitePartie = totalitePartieService.getTotalitePartie();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching totalitePartie", e);
            return new ResponseEntity<List<TotalitePartie>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching totalitePartie", e);
            return new ResponseEntity<List<TotalitePartie>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TotalitePartie>>(totalitePartie, HttpStatus.OK);
    }

    @Autowired
    TutelleCuratelleService tutelleCuratelleService;

    @GetMapping(inputEntryPoint + "/tutelle-curatelle")
    public ResponseEntity<List<TutelleCuratelle>> getTutelleCuratelle() {
        List<TutelleCuratelle> tutelleCuratelle = null;
        try {
            tutelleCuratelle = tutelleCuratelleService.getTutelleCuratelle();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching tutelleCuratelle", e);
            return new ResponseEntity<List<TutelleCuratelle>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching tutelleCuratelle", e);
            return new ResponseEntity<List<TutelleCuratelle>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TutelleCuratelle>>(tutelleCuratelle, HttpStatus.OK);
    }

    @Autowired
    TypeDePersonneService typeDePersonneService;

    @GetMapping(inputEntryPoint + "/type-de-personne")
    public ResponseEntity<List<TypeDePersonne>> getTypeDePersonne() {
        List<TypeDePersonne> typeDePersonne = null;
        try {
            typeDePersonne = typeDePersonneService.getTypeDePersonne();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeDePersonne", e);
            return new ResponseEntity<List<TypeDePersonne>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeDePersonne", e);
            return new ResponseEntity<List<TypeDePersonne>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeDePersonne>>(typeDePersonne, HttpStatus.OK);
    }

    @Autowired
    TypeDeStatutsService typeDeStatutsService;

    @GetMapping(inputEntryPoint + "/type-de-statuts")
    public ResponseEntity<List<TypeDeStatuts>> getTypeDeStatuts() {
        List<TypeDeStatuts> typeDeStatuts = null;
        try {
            typeDeStatuts = typeDeStatutsService.getTypeDeStatuts();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeDeStatuts", e);
            return new ResponseEntity<List<TypeDeStatuts>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeDeStatuts", e);
            return new ResponseEntity<List<TypeDeStatuts>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeDeStatuts>>(typeDeStatuts, HttpStatus.OK);
    }

    @Autowired
    TypeDissolutionService typeDissolutionService;

    @GetMapping(inputEntryPoint + "/type-dissolution")
    public ResponseEntity<List<TypeDissolution>> getTypeDissolution() {
        List<TypeDissolution> typeDissolution = null;
        try {
            typeDissolution = typeDissolutionService.getTypeDissolution();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeDissolution", e);
            return new ResponseEntity<List<TypeDissolution>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeDissolution", e);
            return new ResponseEntity<List<TypeDissolution>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeDissolution>>(typeDissolution, HttpStatus.OK);
    }

    @Autowired
    TypeExploitationService typeExploitationService;

    @GetMapping(inputEntryPoint + "/type-exploitation")
    public ResponseEntity<List<TypeExploitation>> getTypeExploitation() {
        List<TypeExploitation> typeExploitation = null;
        try {
            typeExploitation = typeExploitationService.getTypeExploitation();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeExploitation", e);
            return new ResponseEntity<List<TypeExploitation>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeExploitation", e);
            return new ResponseEntity<List<TypeExploitation>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeExploitation>>(typeExploitation, HttpStatus.OK);
    }

    @Autowired
    TypeFormaliteService typeFormaliteService;

    @GetMapping(inputEntryPoint + "/type-formalite")
    public ResponseEntity<List<TypeFormalite>> getTypeFormalite() {
        List<TypeFormalite> typeFormalite = null;
        try {
            typeFormalite = typeFormaliteService.getTypeFormalite();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeFormalite", e);
            return new ResponseEntity<List<TypeFormalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeFormalite", e);
            return new ResponseEntity<List<TypeFormalite>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeFormalite>>(typeFormalite, HttpStatus.OK);
    }

    @Autowired
    TypeLiasseService typeLiasseService;

    @GetMapping(inputEntryPoint + "/type-liasse")
    public ResponseEntity<List<TypeLiasse>> getTypeLiasse() {
        List<TypeLiasse> typeLiasse = null;
        try {
            typeLiasse = typeLiasseService.getTypeLiasse();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeLiasse", e);
            return new ResponseEntity<List<TypeLiasse>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeLiasse", e);
            return new ResponseEntity<List<TypeLiasse>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeLiasse>>(typeLiasse, HttpStatus.OK);
    }

    @Autowired
    TypeLocataireGerantMandataireService typeLocataireGerantMandataireService;

    @GetMapping(inputEntryPoint + "/type-locataire-gerant-mandataire")
    public ResponseEntity<List<TypeLocataireGerantMandataire>> getTypeLocataireGerantMandataire() {
        List<TypeLocataireGerantMandataire> typeLocataireGerantMandataire = null;
        try {
            typeLocataireGerantMandataire = typeLocataireGerantMandataireService.getTypeLocataireGerantMandataire();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeLocataireGerantMandataire", e);
            return new ResponseEntity<List<TypeLocataireGerantMandataire>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeLocataireGerantMandataire", e);
            return new ResponseEntity<List<TypeLocataireGerantMandataire>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeLocataireGerantMandataire>>(typeLocataireGerantMandataire, HttpStatus.OK);
    }

    @Autowired
    TypeOrigineService typeOrigineService;

    @GetMapping(inputEntryPoint + "/type-origine")
    public ResponseEntity<List<TypeOrigine>> getTypeOrigine() {
        List<TypeOrigine> typeOrigine = null;
        try {
            typeOrigine = typeOrigineService.getTypeOrigine();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeOrigine", e);
            return new ResponseEntity<List<TypeOrigine>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeOrigine", e);
            return new ResponseEntity<List<TypeOrigine>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeOrigine>>(typeOrigine, HttpStatus.OK);
    }

    @Autowired
    TypePersonneService typePersonneService;

    @GetMapping(inputEntryPoint + "/type-personne")
    public ResponseEntity<List<TypePersonne>> getTypePersonne() {
        List<TypePersonne> typePersonne = null;
        try {
            typePersonne = typePersonneService.getTypePersonne();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typePersonne", e);
            return new ResponseEntity<List<TypePersonne>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typePersonne", e);
            return new ResponseEntity<List<TypePersonne>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypePersonne>>(typePersonne, HttpStatus.OK);
    }

    @Autowired
    TypePersonneBlocPreneurBailService typePersonneBlocPreneurBailService;

    @GetMapping(inputEntryPoint + "/type-personne-bloc-preneur-bail")
    public ResponseEntity<List<TypePersonneBlocPreneurBail>> getTypePersonneBlocPreneurBail() {
        List<TypePersonneBlocPreneurBail> typePersonneBlocPreneurBail = null;
        try {
            typePersonneBlocPreneurBail = typePersonneBlocPreneurBailService.getTypePersonneBlocPreneurBail();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typePersonneBlocPreneurBail", e);
            return new ResponseEntity<List<TypePersonneBlocPreneurBail>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typePersonneBlocPreneurBail", e);
            return new ResponseEntity<List<TypePersonneBlocPreneurBail>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypePersonneBlocPreneurBail>>(typePersonneBlocPreneurBail, HttpStatus.OK);
    }

    @Autowired
    TypePersonneAncienExploitantService typePersonneAncienExploitantService;

    @GetMapping(inputEntryPoint + "/type-personne-ancien-exploitant")
    public ResponseEntity<List<TypePersonneAncienExploitant>> getTypePersonneAncienExploitant() {
        List<TypePersonneAncienExploitant> typePersonneAncienExploitant = null;
        try {
            typePersonneAncienExploitant = typePersonneAncienExploitantService.getTypePersonneAncienExploitant();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typePersonneAncienExploitant", e);
            return new ResponseEntity<List<TypePersonneAncienExploitant>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typePersonneAncienExploitant", e);
            return new ResponseEntity<List<TypePersonneAncienExploitant>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypePersonneAncienExploitant>>(typePersonneAncienExploitant, HttpStatus.OK);
    }

    @Autowired
    TypePersonneContractanteService typePersonneContractanteService;

    @GetMapping(inputEntryPoint + "/type-personne-contractante")
    public ResponseEntity<List<TypePersonneContractante>> getTypePersonneContractante() {
        List<TypePersonneContractante> typePersonneContractante = null;
        try {
            typePersonneContractante = typePersonneContractanteService.getTypePersonneContractante();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typePersonneContractante", e);
            return new ResponseEntity<List<TypePersonneContractante>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typePersonneContractante", e);
            return new ResponseEntity<List<TypePersonneContractante>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypePersonneContractante>>(typePersonneContractante, HttpStatus.OK);
    }

    @Autowired
    TypeRepresentantService typeRepresentantService;

    @GetMapping(inputEntryPoint + "/type-representant")
    public ResponseEntity<List<TypeRepresentant>> getTypeRepresentant() {
        List<TypeRepresentant> typeRepresentant = null;
        try {
            typeRepresentant = typeRepresentantService.getTypeRepresentant();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeRepresentant", e);
            return new ResponseEntity<List<TypeRepresentant>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeRepresentant", e);
            return new ResponseEntity<List<TypeRepresentant>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeRepresentant>>(typeRepresentant, HttpStatus.OK);
    }

    @Autowired
    TypeVoieService typeVoieService;

    @GetMapping(inputEntryPoint + "/type-voie")
    public ResponseEntity<List<TypeVoie>> getTypeVoie() {
        List<TypeVoie> typeVoie = null;
        try {
            typeVoie = typeVoieService.getTypeVoie();
        } catch (HttpStatusCodeException e) {
            logger.error("HTTP error when fetching typeVoie", e);
            return new ResponseEntity<List<TypeVoie>>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error when fetching typeVoie", e);
            return new ResponseEntity<List<TypeVoie>>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List<TypeVoie>>(typeVoie, HttpStatus.OK);
    }
}
