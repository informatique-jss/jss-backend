package com.jss.osiris.modules.osiris.quotation.controller;

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
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormaliteGuichetUniqueStatus;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeExerciceActivitePrincipal;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.StatutFormalite;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeDocument;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeVoie;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.FormaliteGuichetUniqueStatusService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.FormeExerciceActivitePrincipalService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.FormeJuridiqueService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.StatutFormaliteService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.TypeDocumentService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.TypeVoieService;

@RestController
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
    FormaliteGuichetUniqueStatusService statusService;

    @GetMapping(inputEntryPoint + "/status")
    public ResponseEntity<List<FormaliteGuichetUniqueStatus>> getStatus() {
        return new ResponseEntity<List<FormaliteGuichetUniqueStatus>>(statusService.getFormaliteGuichetUniqueStatus(),
                HttpStatus.OK);
    }

    @Autowired
    StatutFormaliteService statutFormaliteService;

    @GetMapping(inputEntryPoint + "/statut-formalite")
    public ResponseEntity<List<StatutFormalite>> getStatutFormalite() {
        return new ResponseEntity<List<StatutFormalite>>(statutFormaliteService.getStatutFormalite(), HttpStatus.OK);
    }

    @Autowired
    TypeVoieService typeVoieService;

    @GetMapping(inputEntryPoint + "/type-voie")
    public ResponseEntity<List<TypeVoie>> getTypeVoie() {
        return new ResponseEntity<List<TypeVoie>>(typeVoieService.getTypeVoie(), HttpStatus.OK);
    }
}
