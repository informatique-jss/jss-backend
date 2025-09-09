package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneCompany;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface AffaireService {
        public List<Affaire> getAffaires();

        public Affaire getAffaire(Integer id);

        public Affaire addOrUpdateAffaire(Affaire affaire) throws OsirisDuplicateException, OsirisException;

        public void reindexAffaire() throws OsirisException;

        public List<Affaire> getAffaireBySiret(String siret);

        public List<Affaire> getAffairesBySiren(String siren);

        public List<Affaire> getAffairesFromSiren(String siren) throws OsirisException, OsirisClientMessageException;

        public List<Affaire> getAffairesFromSiret(String siret) throws OsirisException, OsirisClientMessageException;

        public List<Affaire> getAffairesFromRna(String rna) throws OsirisException, OsirisClientMessageException;

        public void updateAffairesFromRne() throws OsirisException, OsirisClientMessageException;

        public void updateAffaireFromRne()
                        throws OsirisException, OsirisClientMessageException, OsirisDuplicateException;

        public Affaire refreshAffaireFromRne(Affaire affaire)
                        throws OsirisException, OsirisClientMessageException, OsirisDuplicateException;

        public List<Affaire> getAffairesForCurrentUser(List<Integer> responsableIdToFilter, Integer page, String sortBy,
                        String searchText);

        public List<Attachment> getAttachmentsForAffaire(Affaire affaire) throws OsirisException;

        public void updateAffaireFromRneCompany(Affaire affaire, RneCompany rneCompany)
                        throws OsirisException;

        public List<Affaire> searchAffaireForCorrection();

        public Affaire getAffaireFromResponsable(Responsable responsable)
                        throws OsirisDuplicateException, OsirisException;

}
