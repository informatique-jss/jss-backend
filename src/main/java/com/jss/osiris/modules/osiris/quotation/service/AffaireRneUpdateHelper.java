package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneCompany;

@Service
public class AffaireRneUpdateHelper {

    @Autowired
    AffaireService affaireService;

    @Transactional
    public void updateAffaireSiretFromRne(Affaire affaire, String siren, String siret)
            throws OsirisDuplicateException, OsirisException {
        affaire = affaireService.getAffaire(affaire.getId());
        affaire.setSiren(siren);
        affaire.setSiret(siret);
        affaire.setIsUnregistered(false);
        affaire.setLastRneUpdate(LocalDate.now());
        affaireService.addOrUpdateAffaire(affaire);
    }

    @Transactional
    public void updateAffaireFromRne(Affaire affaire, RneCompany company)
            throws OsirisDuplicateException, OsirisException {
        affaire = affaireService.getAffaire(affaire.getId());
        affaireService.updateAffaireFromRneCompany(affaire, company);
        affaire.setLastRneUpdate(LocalDate.now());
        affaireService.addOrUpdateAffaire(affaire);
    }
}
