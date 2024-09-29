package com.jss.osiris.modules.quotation.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.jss.osiris.libs.ActiveDirectoryHelper;
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
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
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;
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
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.FormaliteGuichetUniqueStatusService;
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
@PreAuthorize(ActiveDirectoryHelper.OSIRIS_USERS)
public class GuicheUniqueReferentialController {

    private static final String inputEntryPoint = "/quotation/guichet-unique";

    @Autowired
    TypeDocumentService typeDocumentService;

    @Autowired
    ValidationHelper validationHelper;

    @GetMapping(inputEntryPoint + "/type-document")
    public ResponseEntity<List<TypeDocument>> getTypeDocument() {
        return new ResponseEntity<List<TypeDocument>>(typeDocumentService.getTypeDocument(), HttpStatus.OK);
    }

    @PreAuthorize(ActiveDirectoryHelper.ADMINISTRATEUR)
    @PostMapping(inputEntryPoint + "/type-document")
    public ResponseEntity<TypeDocument> addOrUpdateTypeDocument(
            @RequestBody TypeDocument attachmentTypes) throws OsirisValidationException, OsirisException {
        validationHelper.validateString(attachmentTypes.getCode(), true, 20, "code");
        validationHelper.validateString(attachmentTypes.getLabel(), true, 100, "label");
        validationHelper.validateString(attachmentTypes.getCustomLabel(), true, 100, "label");
        validationHelper.validateReferential(attachmentTypes.getAttachmentType(),
                attachmentTypes.getIsToDownloadOnProvision(), "attachmentType");

        return new ResponseEntity<TypeDocument>(typeDocumentService.addOrUpdateTypeDocument(attachmentTypes),
                HttpStatus.OK);
    }

    @Autowired
    MotifRejetCmaService motifRejetCmaService;

    @GetMapping(inputEntryPoint + "/motif-rejet-cma")
    public ResponseEntity<List<MotifRejetCma>> getMotifRejetCma() {
        return new ResponseEntity<List<MotifRejetCma>>(motifRejetCmaService.getMotifRejetCma(), HttpStatus.OK);
    }

    @Autowired
    MotifRejetGreffeService motifRejetGreffeService;

    @GetMapping(inputEntryPoint + "/motif-rejet-greffe")
    public ResponseEntity<List<MotifRejetGreffe>> getMotifRejetGreffe() {
        return new ResponseEntity<List<MotifRejetGreffe>>(motifRejetGreffeService.getMotifRejetGreffe(), HttpStatus.OK);
    }

    @Autowired
    MotifRejetMsaService motifRejetMsaService;

    @GetMapping(inputEntryPoint + "/motif-rejet-msa")
    public ResponseEntity<List<MotifRejetMsa>> getMotifRejetMsa() {
        return new ResponseEntity<List<MotifRejetMsa>>(motifRejetMsaService.getMotifRejetMsa(), HttpStatus.OK);
    }

    @Autowired
    MotifTrasnfertService motifTrasnfertService;

    @GetMapping(inputEntryPoint + "/motif-trasnfert")
    public ResponseEntity<List<MotifTrasnfert>> getMotifTrasnfert() {
        return new ResponseEntity<List<MotifTrasnfert>>(motifTrasnfertService.getMotifTrasnfert(), HttpStatus.OK);
    }

    @Autowired
    ActiviteReguliereService activiteReguliereService;

    @GetMapping(inputEntryPoint + "/activite-reguliere")
    public ResponseEntity<List<ActiviteReguliere>> getActiviteReguliere() {
        return new ResponseEntity<List<ActiviteReguliere>>(activiteReguliereService.getActiviteReguliere(),
                HttpStatus.OK);
    }

    @Autowired
    CapaciteEngagementService capaciteEngagementService;

    @GetMapping(inputEntryPoint + "/capacite-engagement")
    public ResponseEntity<List<CapaciteEngagement>> getCapaciteEngagement() {
        return new ResponseEntity<List<CapaciteEngagement>>(capaciteEngagementService.getCapaciteEngagement(),
                HttpStatus.OK);
    }

    @Autowired
    CodeEEEPaysService codeEEEPaysService;

    @GetMapping(inputEntryPoint + "/code-eee-pays")
    public ResponseEntity<List<CodeEEEPays>> getCodeEEEPays() {
        return new ResponseEntity<List<CodeEEEPays>>(codeEEEPaysService.getCodeEEEPays(), HttpStatus.OK);
    }

    @Autowired
    CodeInseePaysService codeInseePaysService;

    @GetMapping(inputEntryPoint + "/code-insee-pays")
    public ResponseEntity<List<CodeInseePays>> getCodeInseePays() {
        return new ResponseEntity<List<CodeInseePays>>(codeInseePaysService.getCodeInseePays(), HttpStatus.OK);
    }

    @Autowired
    CodeInseePaysNaissanceService codeInseePaysNaissanceService;

    @GetMapping(inputEntryPoint + "/code-insee-pays-naissance")
    public ResponseEntity<List<CodeInseePaysNaissance>> getCodeInseePaysNaissance() {
        return new ResponseEntity<List<CodeInseePaysNaissance>>(
                codeInseePaysNaissanceService.getCodeInseePaysNaissance(), HttpStatus.OK);
    }

    @Autowired
    CodeNationaliteService codeNationaliteService;

    @GetMapping(inputEntryPoint + "/code-nationalite")
    public ResponseEntity<List<CodeNationalite>> getCodeNationalite() {
        return new ResponseEntity<List<CodeNationalite>>(codeNationaliteService.getCodeNationalite(), HttpStatus.OK);
    }

    @Autowired
    CodePaysService codePaysService;

    @GetMapping(inputEntryPoint + "/code-pays")
    public ResponseEntity<List<CodePays>> getCodePays() {
        return new ResponseEntity<List<CodePays>>(codePaysService.getCodePays(), HttpStatus.OK);
    }

    @Autowired
    CodeRolePersonneQualifieeService codeRolePersonneQualifieeService;

    @GetMapping(inputEntryPoint + "/code-role-personne-qualifiee")
    public ResponseEntity<List<CodeRolePersonneQualifiee>> getCodeRolePersonneQualifiee() {
        return new ResponseEntity<List<CodeRolePersonneQualifiee>>(
                codeRolePersonneQualifieeService.getCodeRolePersonneQualifiee(), HttpStatus.OK);
    }

    @Autowired
    CodifNormeService codifNormeService;

    @GetMapping(inputEntryPoint + "/codif-norme")
    public ResponseEntity<List<CodifNorme>> getCodifNorme() {
        return new ResponseEntity<List<CodifNorme>>(codifNormeService.getCodifNorme(), HttpStatus.OK);
    }

    @Autowired
    ConditionVersementTVAService conditionVersementTVAService;

    @GetMapping(inputEntryPoint + "/condition-versement-tva")
    public ResponseEntity<List<ConditionVersementTVA>> getConditionVersementTVA() {
        return new ResponseEntity<List<ConditionVersementTVA>>(conditionVersementTVAService.getConditionVersementTVA(),
                HttpStatus.OK);
    }

    @Autowired
    DestinationService destinationService;

    @GetMapping(inputEntryPoint + "/destination")
    public ResponseEntity<List<Destination>> getDestination() {
        return new ResponseEntity<List<Destination>>(destinationService.getDestination(), HttpStatus.OK);
    }

    @Autowired
    DestinationEtablissementService destinationEtablissementService;

    @GetMapping(inputEntryPoint + "/destination-etablissement")
    public ResponseEntity<List<DestinationEtablissement>> getDestinationEtablissement() {
        return new ResponseEntity<List<DestinationEtablissement>>(
                destinationEtablissementService.getDestinationEtablissement(), HttpStatus.OK);
    }

    @Autowired
    DestinationLocationGeranceMandService destinationLocationGeranceMandService;

    @GetMapping(inputEntryPoint + "/destination-location-gerance-mand")
    public ResponseEntity<List<DestinationLocationGeranceMand>> getDestinationLocationGeranceMand() {
        return new ResponseEntity<List<DestinationLocationGeranceMand>>(
                destinationLocationGeranceMandService.getDestinationLocationGeranceMand(), HttpStatus.OK);
    }

    @Autowired
    DeviseCapitalService deviseCapitalService;

    @GetMapping(inputEntryPoint + "/devise-capital")
    public ResponseEntity<List<DeviseCapital>> getDeviseCapital() {
        return new ResponseEntity<List<DeviseCapital>>(deviseCapitalService.getDeviseCapital(), HttpStatus.OK);
    }

    @Autowired
    DiffusionINSEEService diffusionINSEEService;

    @GetMapping(inputEntryPoint + "/diffusion-insee")
    public ResponseEntity<List<DiffusionINSEE>> getDiffusionINSEE() {
        return new ResponseEntity<List<DiffusionINSEE>>(diffusionINSEEService.getDiffusionINSEE(), HttpStatus.OK);
    }

    @Autowired
    DocumentExtensionService documentExtensionService;

    @GetMapping(inputEntryPoint + "/document-extension")
    public ResponseEntity<List<DocumentExtension>> getDocumentExtension() {
        return new ResponseEntity<List<DocumentExtension>>(documentExtensionService.getDocumentExtension(),
                HttpStatus.OK);
    }

    @Autowired
    EventsService eventsService;

    @GetMapping(inputEntryPoint + "/events")
    public ResponseEntity<List<Events>> getEvents() {
        return new ResponseEntity<List<Events>>(eventsService.getEvents(), HttpStatus.OK);
    }

    @Autowired
    ExerciceActiviteService exerciceActiviteService;

    @GetMapping(inputEntryPoint + "/exercice-activite")
    public ResponseEntity<List<ExerciceActivite>> getExerciceActivite() {
        return new ResponseEntity<List<ExerciceActivite>>(exerciceActiviteService.getExerciceActivite(), HttpStatus.OK);
    }

    @Autowired
    FormeExerciceService formeExerciceService;

    @GetMapping(inputEntryPoint + "/forme-exercice")
    public ResponseEntity<List<FormeExercice>> getFormeExercice() {
        return new ResponseEntity<List<FormeExercice>>(formeExerciceService.getFormeExercice(), HttpStatus.OK);
    }

    @Autowired
    FormeExerciceActivitePrincipalService formeExerciceActivitePrincipalService;

    @GetMapping(inputEntryPoint + "/forme-exercice-activite-principal")
    public ResponseEntity<List<FormeExerciceActivitePrincipal>> getFormeExerciceActivitePrincipal() {
        return new ResponseEntity<List<FormeExerciceActivitePrincipal>>(
                formeExerciceActivitePrincipalService.getFormeExerciceActivitePrincipal(), HttpStatus.OK);
    }

    @Autowired
    FormeJuridiqueService formeJuridiqueService;

    @GetMapping(inputEntryPoint + "/forme-juridique")
    public ResponseEntity<List<FormeJuridique>> getFormeJuridique() {
        return new ResponseEntity<List<FormeJuridique>>(formeJuridiqueService.getFormeJuridique(), HttpStatus.OK);
    }

    @Autowired
    FormeSocialeService formeSocialeService;

    @GetMapping(inputEntryPoint + "/forme-sociale")
    public ResponseEntity<List<FormeSociale>> getFormeSociale() {
        return new ResponseEntity<List<FormeSociale>>(formeSocialeService.getFormeSociale(), HttpStatus.OK);
    }

    @Autowired
    GenreService genreService;

    @GetMapping(inputEntryPoint + "/genre")
    public ResponseEntity<List<Genre>> getGenre() {
        return new ResponseEntity<List<Genre>>(genreService.getGenre(), HttpStatus.OK);
    }

    @Autowired
    JeuneAgriculteurService jeuneAgriculteurService;

    @GetMapping(inputEntryPoint + "/jeune-agriculteur")
    public ResponseEntity<List<JeuneAgriculteur>> getJeuneAgriculteur() {
        return new ResponseEntity<List<JeuneAgriculteur>>(jeuneAgriculteurService.getJeuneAgriculteur(), HttpStatus.OK);
    }

    @Autowired
    LieuDeLiquidationService lieuDeLiquidationService;

    @GetMapping(inputEntryPoint + "/lieu-de-liquidation")
    public ResponseEntity<List<LieuDeLiquidation>> getLieuDeLiquidation() {
        return new ResponseEntity<List<LieuDeLiquidation>>(lieuDeLiquidationService.getLieuDeLiquidation(),
                HttpStatus.OK);
    }

    @Autowired
    MineurLienParenteService mineurLienParenteService;

    @GetMapping(inputEntryPoint + "/mineur-lien-parente")
    public ResponseEntity<List<MineurLienParente>> getMineurLienParente() {
        return new ResponseEntity<List<MineurLienParente>>(mineurLienParenteService.getMineurLienParente(),
                HttpStatus.OK);
    }

    @Autowired
    MineurNationaliteService mineurNationaliteService;

    @GetMapping(inputEntryPoint + "/mineur-nationalite")
    public ResponseEntity<List<MineurNationalite>> getMineurNationalite() {
        return new ResponseEntity<List<MineurNationalite>>(mineurNationaliteService.getMineurNationalite(),
                HttpStatus.OK);
    }

    @Autowired
    MineurSexeService mineurSexeService;

    @GetMapping(inputEntryPoint + "/mineur-sexe")
    public ResponseEntity<List<MineurSexe>> getMineurSexe() {
        return new ResponseEntity<List<MineurSexe>>(mineurSexeService.getMineurSexe(), HttpStatus.OK);
    }

    @Autowired
    ModalitesDeControleService modalitesDeControleService;

    @GetMapping(inputEntryPoint + "/modalites-de-controle")
    public ResponseEntity<List<ModalitesDeControle>> getModalitesDeControle() {
        return new ResponseEntity<List<ModalitesDeControle>>(modalitesDeControleService.getModalitesDeControle(),
                HttpStatus.OK);
    }

    @Autowired
    ModeExerciceService modeExerciceService;

    @GetMapping(inputEntryPoint + "/mode-exercice")
    public ResponseEntity<List<ModeExercice>> getModeExercice() {
        return new ResponseEntity<List<ModeExercice>>(modeExerciceService.getModeExercice(), HttpStatus.OK);
    }

    @Autowired
    MotifCessationService motifCessationService;

    @GetMapping(inputEntryPoint + "/motif-cessation")
    public ResponseEntity<List<MotifCessation>> getMotifCessation() {
        return new ResponseEntity<List<MotifCessation>>(motifCessationService.getMotifCessation(), HttpStatus.OK);
    }

    @Autowired
    MotifDisparitionService motifDisparitionService;

    @GetMapping(inputEntryPoint + "/motif-disparition")
    public ResponseEntity<List<MotifDisparition>> getMotifDisparition() {
        return new ResponseEntity<List<MotifDisparition>>(motifDisparitionService.getMotifDisparition(), HttpStatus.OK);
    }

    @Autowired
    MotifFinEirlService motifFinEirlService;

    @GetMapping(inputEntryPoint + "/motif-fin-eirl")
    public ResponseEntity<List<MotifFinEirl>> getMotifFinEirl() {
        return new ResponseEntity<List<MotifFinEirl>>(motifFinEirlService.getMotifFinEirl(), HttpStatus.OK);
    }

    @Autowired
    NatureCessationService natureCessationService;

    @GetMapping(inputEntryPoint + "/nature-cessation")
    public ResponseEntity<List<NatureCessation>> getNatureCessation() {
        return new ResponseEntity<List<NatureCessation>>(natureCessationService.getNatureCessation(), HttpStatus.OK);
    }

    @Autowired
    NatureDesActiviteService natureDesActiviteService;

    @GetMapping(inputEntryPoint + "/nature-des-activite")
    public ResponseEntity<List<NatureDesActivite>> getNatureDesActivite() {
        return new ResponseEntity<List<NatureDesActivite>>(natureDesActiviteService.getNatureDesActivite(),
                HttpStatus.OK);
    }

    @Autowired
    NatureDomaineService natureDomaineService;

    @GetMapping(inputEntryPoint + "/nature-domaine")
    public ResponseEntity<List<NatureDomaine>> getNatureDomaine() {
        return new ResponseEntity<List<NatureDomaine>>(natureDomaineService.getNatureDomaine(), HttpStatus.OK);
    }

    @Autowired
    NatureGeranceService natureGeranceService;

    @GetMapping(inputEntryPoint + "/nature-gerance")
    public ResponseEntity<List<NatureGerance>> getNatureGerance() {
        return new ResponseEntity<List<NatureGerance>>(natureGeranceService.getNatureGerance(), HttpStatus.OK);
    }

    @Autowired
    NatureVoletSocialService natureVoletSocialService;

    @GetMapping(inputEntryPoint + "/nature-volet-social")
    public ResponseEntity<List<NatureVoletSocial>> getNatureVoletSocial() {
        return new ResponseEntity<List<NatureVoletSocial>>(natureVoletSocialService.getNatureVoletSocial(),
                HttpStatus.OK);
    }

    @Autowired
    OptionEirlService optionEirlService;

    @GetMapping(inputEntryPoint + "/option-eirl")
    public ResponseEntity<List<OptionEirl>> getOptionEirl() {
        return new ResponseEntity<List<OptionEirl>>(optionEirlService.getOptionEirl(), HttpStatus.OK);
    }

    @Autowired
    OptionJQPAService optionJQPAService;

    @GetMapping(inputEntryPoint + "/option-jqpa")
    public ResponseEntity<List<OptionJQPA>> getOptionJQPA() {
        return new ResponseEntity<List<OptionJQPA>>(optionJQPAService.getOptionJQPA(), HttpStatus.OK);
    }

    @Autowired
    OptionParticuliereRegimeBenefiService optionParticuliereRegimeBenefiService;

    @GetMapping(inputEntryPoint + "/option-particuliere-regime-benefi")
    public ResponseEntity<List<OptionParticuliereRegimeBenefi>> getOptionParticuliereRegimeBenefi() {
        return new ResponseEntity<List<OptionParticuliereRegimeBenefi>>(
                optionParticuliereRegimeBenefiService.getOptionParticuliereRegimeBenefi(), HttpStatus.OK);
    }

    @Autowired
    OrganismeAssuranceMaladieActueService organismeAssuranceMaladieActueService;

    @GetMapping(inputEntryPoint + "/organisme-assurance-maladie-actue")
    public ResponseEntity<List<OrganismeAssuranceMaladieActue>> getOrganismeAssuranceMaladieActue() {
        return new ResponseEntity<List<OrganismeAssuranceMaladieActue>>(
                organismeAssuranceMaladieActueService.getOrganismeAssuranceMaladieActue(), HttpStatus.OK);
    }

    @Autowired
    OrigineFusionScissionService origineFusionScissionService;

    @GetMapping(inputEntryPoint + "/origine-fusion-scission")
    public ResponseEntity<List<OrigineFusionScission>> getOrigineFusionScission() {
        return new ResponseEntity<List<OrigineFusionScission>>(origineFusionScissionService.getOrigineFusionScission(),
                HttpStatus.OK);
    }

    @Autowired
    PerimetreService perimetreService;

    @GetMapping(inputEntryPoint + "/perimetre")
    public ResponseEntity<List<Perimetre>> getPerimetre() {
        return new ResponseEntity<List<Perimetre>>(perimetreService.getPerimetre(), HttpStatus.OK);
    }

    @Autowired
    PeriodiciteEtOptionsParticulieService periodiciteEtOptionsParticulieService;

    @GetMapping(inputEntryPoint + "/periodicite-et-options-particulie")
    public ResponseEntity<List<PeriodiciteEtOptionsParticulie>> getPeriodiciteEtOptionsParticulie() {
        return new ResponseEntity<List<PeriodiciteEtOptionsParticulie>>(
                periodiciteEtOptionsParticulieService.getPeriodiciteEtOptionsParticulie(), HttpStatus.OK);
    }

    @Autowired
    PeriodiciteVersementService periodiciteVersementService;

    @GetMapping(inputEntryPoint + "/periodicite-versement")
    public ResponseEntity<List<PeriodiciteVersement>> getPeriodiciteVersement() {
        return new ResponseEntity<List<PeriodiciteVersement>>(periodiciteVersementService.getPeriodiciteVersement(),
                HttpStatus.OK);
    }

    @Autowired
    PrecisionActiviteService precisionActiviteService;

    @GetMapping(inputEntryPoint + "/precision-activite")
    public ResponseEntity<List<PrecisionActivite>> getPrecisionActivite() {
        return new ResponseEntity<List<PrecisionActivite>>(precisionActiviteService.getPrecisionActivite(),
                HttpStatus.OK);
    }

    @Autowired
    QualiteDeNonSedentariteService qualiteDeNonSedentariteService;

    @GetMapping(inputEntryPoint + "/qualite-de-non-sedentarite")
    public ResponseEntity<List<QualiteDeNonSedentarite>> getQualiteDeNonSedentarite() {
        return new ResponseEntity<List<QualiteDeNonSedentarite>>(
                qualiteDeNonSedentariteService.getQualiteDeNonSedentarite(), HttpStatus.OK);
    }

    @Autowired
    QualiteNonSedentaireService qualiteNonSedentaireService;

    @GetMapping(inputEntryPoint + "/qualite-non-sedentaire")
    public ResponseEntity<List<QualiteNonSedentaire>> getQualiteNonSedentaire() {
        return new ResponseEntity<List<QualiteNonSedentaire>>(qualiteNonSedentaireService.getQualiteNonSedentaire(),
                HttpStatus.OK);
    }

    @Autowired
    RegimeImpositionBeneficesService regimeImpositionBeneficesService;

    @GetMapping(inputEntryPoint + "/regime-imposition-benefices")
    public ResponseEntity<List<RegimeImpositionBenefices>> getRegimeImpositionBenefices() {
        return new ResponseEntity<List<RegimeImpositionBenefices>>(
                regimeImpositionBeneficesService.getRegimeImpositionBenefices(), HttpStatus.OK);
    }

    @Autowired
    RegimeImpositionBenefices2Service regimeImpositionBenefices2Service;

    @GetMapping(inputEntryPoint + "/regime-imposition-benefices2")
    public ResponseEntity<List<RegimeImpositionBenefices2>> getRegimeImpositionBenefices2() {
        return new ResponseEntity<List<RegimeImpositionBenefices2>>(
                regimeImpositionBenefices2Service.getRegimeImpositionBenefices2(), HttpStatus.OK);
    }

    @Autowired
    RegimeImpositionTVAService regimeImpositionTVAService;

    @GetMapping(inputEntryPoint + "/regime-imposition-tva")
    public ResponseEntity<List<RegimeImpositionTVA>> getRegimeImpositionTVA() {
        return new ResponseEntity<List<RegimeImpositionTVA>>(regimeImpositionTVAService.getRegimeImpositionTVA(),
                HttpStatus.OK);
    }

    @Autowired
    RegistreEirlService registreEirlService;

    @GetMapping(inputEntryPoint + "/registre-eirl")
    public ResponseEntity<List<RegistreEirl>> getRegistreEirl() {
        return new ResponseEntity<List<RegistreEirl>>(registreEirlService.getRegistreEirl(), HttpStatus.OK);
    }

    @Autowired
    RegistreEirlDeLancienneEirlService registreEirlDeLancienneEirlService;

    @GetMapping(inputEntryPoint + "/registre-eirl-de-lancienne-eirl")
    public ResponseEntity<List<RegistreEirlDeLancienneEirl>> getRegistreEirlDeLancienneEirl() {
        return new ResponseEntity<List<RegistreEirlDeLancienneEirl>>(
                registreEirlDeLancienneEirlService.getRegistreEirlDeLancienneEirl(), HttpStatus.OK);
    }

    @Autowired
    RoleService roleService;

    @GetMapping(inputEntryPoint + "/role")
    public ResponseEntity<List<Role>> getRole() {
        return new ResponseEntity<List<Role>>(roleService.getRole(), HttpStatus.OK);
    }

    @Autowired
    RoleConjointService roleConjointService;

    @GetMapping(inputEntryPoint + "/role-conjoint")
    public ResponseEntity<List<RoleConjoint>> getRoleConjoint() {
        return new ResponseEntity<List<RoleConjoint>>(roleConjointService.getRoleConjoint(), HttpStatus.OK);
    }

    @Autowired
    RoleContratService roleContratService;

    @GetMapping(inputEntryPoint + "/role-contrat")
    public ResponseEntity<List<RoleContrat>> getRoleContrat() {
        return new ResponseEntity<List<RoleContrat>>(roleContratService.getRoleContrat(), HttpStatus.OK);
    }

    @Autowired
    RoleEntrepriseService roleEntrepriseService;

    @GetMapping(inputEntryPoint + "/role-entreprise")
    public ResponseEntity<List<RoleEntreprise>> getRoleEntreprise() {
        return new ResponseEntity<List<RoleEntreprise>>(roleEntrepriseService.getRoleEntreprise(), HttpStatus.OK);
    }

    @Autowired
    RolePourEntrepriseService rolePourEntrepriseService;

    @GetMapping(inputEntryPoint + "/role-pour-entreprise")
    public ResponseEntity<List<RolePourEntreprise>> getRolePourEntreprise() {
        return new ResponseEntity<List<RolePourEntreprise>>(rolePourEntrepriseService.getRolePourEntreprise(),
                HttpStatus.OK);
    }

    @Autowired
    SecondRoleEntrepriseService secondRoleEntrepriseService;

    @GetMapping(inputEntryPoint + "/second-role-entreprise")
    public ResponseEntity<List<SecondRoleEntreprise>> getSecondRoleEntreprise() {
        return new ResponseEntity<List<SecondRoleEntreprise>>(secondRoleEntrepriseService.getSecondRoleEntreprise(),
                HttpStatus.OK);
    }

    @Autowired
    SituationMatrimonialeService situationMatrimonialeService;

    @GetMapping(inputEntryPoint + "/situation-matrimoniale")
    public ResponseEntity<List<SituationMatrimoniale>> getSituationMatrimoniale() {
        return new ResponseEntity<List<SituationMatrimoniale>>(situationMatrimonialeService.getSituationMatrimoniale(),
                HttpStatus.OK);
    }

    @Autowired
    SituationVisAVisMsaService situationVisAVisMsaService;

    @GetMapping(inputEntryPoint + "/situation-visavis-msa")
    public ResponseEntity<List<SituationVisAVisMsa>> getSituationVisAVisMsa() {
        return new ResponseEntity<List<SituationVisAVisMsa>>(situationVisAVisMsaService.getSituationVisAVisMsa(),
                HttpStatus.OK);
    }

    @Autowired
    FormaliteGuichetUniqueStatusService statusService;

    @GetMapping(inputEntryPoint + "/status")
    public ResponseEntity<List<FormaliteGuichetUniqueStatus>> getStatus() {
        return new ResponseEntity<List<FormaliteGuichetUniqueStatus>>(statusService.getFormaliteGuichetUniqueStatus(),
                HttpStatus.OK);
    }

    @Autowired
    StatutContratService statutContratService;

    @GetMapping(inputEntryPoint + "/statut-contrat")
    public ResponseEntity<List<StatutContrat>> getStatutContrat() {
        return new ResponseEntity<List<StatutContrat>>(statutContratService.getStatutContrat(), HttpStatus.OK);
    }

    @Autowired
    StatutDomaineService statutDomaineService;

    @GetMapping(inputEntryPoint + "/statut-domaine")
    public ResponseEntity<List<StatutDomaine>> getStatutDomaine() {
        return new ResponseEntity<List<StatutDomaine>>(statutDomaineService.getStatutDomaine(), HttpStatus.OK);
    }

    @Autowired
    StatutExerciceActiviteSimultanService statutExerciceActiviteSimultanService;

    @GetMapping(inputEntryPoint + "/statut-exercice-activite-simultan")
    public ResponseEntity<List<StatutExerciceActiviteSimultan>> getStatutExerciceActiviteSimultan() {
        return new ResponseEntity<List<StatutExerciceActiviteSimultan>>(
                statutExerciceActiviteSimultanService.getStatutExerciceActiviteSimultan(), HttpStatus.OK);
    }

    @Autowired
    StatutFormaliteService statutFormaliteService;

    @GetMapping(inputEntryPoint + "/statut-formalite")
    public ResponseEntity<List<StatutFormalite>> getStatutFormalite() {
        return new ResponseEntity<List<StatutFormalite>>(statutFormaliteService.getStatutFormalite(), HttpStatus.OK);
    }

    @Autowired
    StatutPourFormaliteService statutPourFormaliteService;

    @GetMapping(inputEntryPoint + "/statut-pour-formalite")
    public ResponseEntity<List<StatutPourFormalite>> getStatutPourFormalite() {
        return new ResponseEntity<List<StatutPourFormalite>>(statutPourFormaliteService.getStatutPourFormalite(),
                HttpStatus.OK);
    }

    @Autowired
    StatutPourLaFormaliteService statutPourLaFormaliteService;

    @GetMapping(inputEntryPoint + "/statut-pour-la-formalite")
    public ResponseEntity<List<StatutPourLaFormalite>> getStatutPourLaFormalite() {
        return new ResponseEntity<List<StatutPourLaFormalite>>(statutPourLaFormaliteService.getStatutPourLaFormalite(),
                HttpStatus.OK);
    }

    @Autowired
    StatutPourLaFormaliteBlocReService statutPourLaFormaliteBlocReService;

    @GetMapping(inputEntryPoint + "/statut-pour-la-formalite-bloc-re")
    public ResponseEntity<List<StatutPourLaFormaliteBlocRe>> getStatutPourLaFormaliteBlocRe() {
        return new ResponseEntity<List<StatutPourLaFormaliteBlocRe>>(
                statutPourLaFormaliteBlocReService.getStatutPourLaFormaliteBlocRe(), HttpStatus.OK);
    }

    @Autowired
    StatutPraticienService statutPraticienService;

    @GetMapping(inputEntryPoint + "/statut-praticien")
    public ResponseEntity<List<StatutPraticien>> getStatutPraticien() {
        return new ResponseEntity<List<StatutPraticien>>(statutPraticienService.getStatutPraticien(), HttpStatus.OK);
    }

    @Autowired
    StatutVisAVisFormaliteService statutVisAVisFormaliteService;

    @GetMapping(inputEntryPoint + "/statut-visavis-formalite")
    public ResponseEntity<List<StatutVisAVisFormalite>> getStatutVisAVisFormalite() {
        return new ResponseEntity<List<StatutVisAVisFormalite>>(
                statutVisAVisFormaliteService.getStatutVisAVisFormalite(), HttpStatus.OK);
    }

    @Autowired
    SuccursaleOuFilialeService succursaleOuFilialeService;

    @GetMapping(inputEntryPoint + "/succursale-ou-filiale")
    public ResponseEntity<List<SuccursaleOuFiliale>> getSuccursaleOuFiliale() {
        return new ResponseEntity<List<SuccursaleOuFiliale>>(succursaleOuFilialeService.getSuccursaleOuFiliale(),
                HttpStatus.OK);
    }

    @Autowired
    TaciteReconductionService taciteReconductionService;

    @GetMapping(inputEntryPoint + "/tacite-reconduction")
    public ResponseEntity<List<TaciteReconduction>> getTaciteReconduction() {
        return new ResponseEntity<List<TaciteReconduction>>(taciteReconductionService.getTaciteReconduction(),
                HttpStatus.OK);
    }

    @Autowired
    TotalitePartieService totalitePartieService;

    @GetMapping(inputEntryPoint + "/totalite-partie")
    public ResponseEntity<List<TotalitePartie>> getTotalitePartie() {
        return new ResponseEntity<List<TotalitePartie>>(totalitePartieService.getTotalitePartie(), HttpStatus.OK);
    }

    @Autowired
    TutelleCuratelleService tutelleCuratelleService;

    @GetMapping(inputEntryPoint + "/tutelle-curatelle")
    public ResponseEntity<List<TutelleCuratelle>> getTutelleCuratelle() {
        return new ResponseEntity<List<TutelleCuratelle>>(tutelleCuratelleService.getTutelleCuratelle(), HttpStatus.OK);
    }

    @Autowired
    TypeDePersonneService typeDePersonneService;

    @GetMapping(inputEntryPoint + "/type-de-personne")
    public ResponseEntity<List<TypeDePersonne>> getTypeDePersonne() {
        return new ResponseEntity<List<TypeDePersonne>>(typeDePersonneService.getTypeDePersonne(), HttpStatus.OK);
    }

    @Autowired
    TypeDeStatutsService typeDeStatutsService;

    @GetMapping(inputEntryPoint + "/type-de-statuts")
    public ResponseEntity<List<TypeDeStatuts>> getTypeDeStatuts() {
        return new ResponseEntity<List<TypeDeStatuts>>(typeDeStatutsService.getTypeDeStatuts(), HttpStatus.OK);
    }

    @Autowired
    TypeDissolutionService typeDissolutionService;

    @GetMapping(inputEntryPoint + "/type-dissolution")
    public ResponseEntity<List<TypeDissolution>> getTypeDissolution() {
        return new ResponseEntity<List<TypeDissolution>>(typeDissolutionService.getTypeDissolution(), HttpStatus.OK);
    }

    @Autowired
    TypeExploitationService typeExploitationService;

    @GetMapping(inputEntryPoint + "/type-exploitation")
    public ResponseEntity<List<TypeExploitation>> getTypeExploitation() {
        return new ResponseEntity<List<TypeExploitation>>(typeExploitationService.getTypeExploitation(), HttpStatus.OK);
    }

    @Autowired
    TypeFormaliteService typeFormaliteService;

    @GetMapping(inputEntryPoint + "/type-formalite")
    public ResponseEntity<List<TypeFormalite>> getTypeFormalite() {
        return new ResponseEntity<List<TypeFormalite>>(typeFormaliteService.getTypeFormalite(), HttpStatus.OK);
    }

    @Autowired
    TypeLiasseService typeLiasseService;

    @GetMapping(inputEntryPoint + "/type-liasse")
    public ResponseEntity<List<TypeLiasse>> getTypeLiasse() {
        return new ResponseEntity<List<TypeLiasse>>(typeLiasseService.getTypeLiasse(), HttpStatus.OK);
    }

    @Autowired
    TypeLocataireGerantMandataireService typeLocataireGerantMandataireService;

    @GetMapping(inputEntryPoint + "/type-locataire-gerant-mandataire")
    public ResponseEntity<List<TypeLocataireGerantMandataire>> getTypeLocataireGerantMandataire() {
        return new ResponseEntity<List<TypeLocataireGerantMandataire>>(
                typeLocataireGerantMandataireService.getTypeLocataireGerantMandataire(), HttpStatus.OK);
    }

    @Autowired
    TypeOrigineService typeOrigineService;

    @GetMapping(inputEntryPoint + "/type-origine")
    public ResponseEntity<List<TypeOrigine>> getTypeOrigine() {
        return new ResponseEntity<List<TypeOrigine>>(typeOrigineService.getTypeOrigine(), HttpStatus.OK);
    }

    @Autowired
    TypePersonneService typePersonneService;

    @GetMapping(inputEntryPoint + "/type-personne")
    public ResponseEntity<List<TypePersonne>> getTypePersonne() {
        return new ResponseEntity<List<TypePersonne>>(typePersonneService.getTypePersonne(), HttpStatus.OK);
    }

    @Autowired
    TypePersonneBlocPreneurBailService typePersonneBlocPreneurBailService;

    @GetMapping(inputEntryPoint + "/type-personne-bloc-preneur-bail")
    public ResponseEntity<List<TypePersonneBlocPreneurBail>> getTypePersonneBlocPreneurBail() {
        return new ResponseEntity<List<TypePersonneBlocPreneurBail>>(
                typePersonneBlocPreneurBailService.getTypePersonneBlocPreneurBail(), HttpStatus.OK);
    }

    @Autowired
    TypePersonneAncienExploitantService typePersonneAncienExploitantService;

    @GetMapping(inputEntryPoint + "/type-personne-ancien-exploitant")
    public ResponseEntity<List<TypePersonneAncienExploitant>> getTypePersonneAncienExploitant() {
        return new ResponseEntity<List<TypePersonneAncienExploitant>>(
                typePersonneAncienExploitantService.getTypePersonneAncienExploitant(), HttpStatus.OK);
    }

    @Autowired
    TypePersonneContractanteService typePersonneContractanteService;

    @GetMapping(inputEntryPoint + "/type-personne-contractante")
    public ResponseEntity<List<TypePersonneContractante>> getTypePersonneContractante() {
        return new ResponseEntity<List<TypePersonneContractante>>(
                typePersonneContractanteService.getTypePersonneContractante(), HttpStatus.OK);
    }

    @Autowired
    TypeRepresentantService typeRepresentantService;

    @GetMapping(inputEntryPoint + "/type-representant")
    public ResponseEntity<List<TypeRepresentant>> getTypeRepresentant() {
        return new ResponseEntity<List<TypeRepresentant>>(typeRepresentantService.getTypeRepresentant(), HttpStatus.OK);
    }

    @Autowired
    TypeVoieService typeVoieService;

    @GetMapping(inputEntryPoint + "/type-voie")
    public ResponseEntity<List<TypeVoie>> getTypeVoie() {
        return new ResponseEntity<List<TypeVoie>>(typeVoieService.getTypeVoie(), HttpStatus.OK);
    }
}
