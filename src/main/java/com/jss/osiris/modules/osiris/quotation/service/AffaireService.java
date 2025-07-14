package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;

public interface AffaireService {
        public List<Affaire> getAffaires();

        public Affaire getAffaire(Integer id);

        public Affaire addOrUpdateAffaire(Affaire affaire) throws OsirisDuplicateException, OsirisException;

        public void reindexAffaire() throws OsirisException;

        public Affaire getAffaireBySiret(String siret);

        public List<Affaire> getAffairesBySiren(String siren);

        public List<Affaire> getAffairesFromSiren(String siren) throws OsirisException, OsirisClientMessageException;

        public List<Affaire> getAffairesFromSiret(String siret) throws OsirisException, OsirisClientMessageException;

        public List<Affaire> getAffairesFromRna(String rna) throws OsirisException, OsirisClientMessageException;

        public void updateAffairesFromRne() throws OsirisException, OsirisClientMessageException;

        public void updateAffaireFromRne(Affaire affaire)
                        throws OsirisException, OsirisClientMessageException, OsirisDuplicateException;

        public Affaire refreshAffaireFromRne(Affaire affaire)
                        throws OsirisException, OsirisClientMessageException, OsirisDuplicateException;

        public List<Affaire> getAffairesForCurrentUser(Integer page, String sortBy, String searchText);

        public List<Attachment> getAttachmentsForAffaire(Affaire affaire) throws OsirisException;

}
