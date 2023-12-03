package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.repository.AffaireRepository;

@Service
public class AffaireServiceImpl implements AffaireService {

    @Autowired
    AffaireRepository affaireRepository;

    @Autowired
    MailService mailService;

    @Autowired
    PhoneService phoneService;

    @Override
    public List<Affaire> getAffaires() {
        return IterableUtils.toList(affaireRepository.findAll());
    }

    @Override
    public Affaire getAffaire(Integer id) {
        Optional<Affaire> affaire = affaireRepository.findById(id);
        if (affaire.isPresent())
            return affaire.get();
        return null;
    }

    @Override
    public Affaire getAffaireBySiret(String siret) {
        return affaireRepository.findBySiret(siret);
    }

    @Override
    public List<Affaire> getAffairesBySiren(String siren) {
        return affaireRepository.findBySiren(siren);
    }

    @Autowired
    IndexEntityService indexEntityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Affaire addOrUpdateAffaire(Affaire affaire) throws OsirisDuplicateException {
        if (affaire.getRna() != null)
            affaire.setRna(affaire.getRna().toUpperCase().replaceAll(" ", ""));
        if (affaire.getSiren() != null)
            affaire.setSiren(affaire.getSiren().toUpperCase().replaceAll(" ", ""));
        if (affaire.getSiret() != null)
            affaire.setSiret(affaire.getSiret().toUpperCase().replaceAll(" ", ""));

        // Check duplicate

        if (affaire.getId() == null) {
            List<Affaire> affairesDuplicates = new ArrayList<Affaire>();
            if (affaire.getSiret() != null && affaire.getSiret().length() > 0) {
                Affaire affaireSameSiret = affaireRepository.findBySiret(affaire.getSiret());
                if (affaireSameSiret != null)
                    affairesDuplicates.add(affaireSameSiret);
            }
            if (affairesDuplicates.size() == 0) {
                if (affaire.getIsIndividual() != null && affaire.getIsIndividual() == true)
                    affairesDuplicates = affaireRepository.findByPostalCodeAndName(affaire.getPostalCode(),
                            affaire.getFirstname(), affaire.getLastname());
                else
                    affairesDuplicates = affaireRepository.findByPostalCodeAndDenomination(affaire.getPostalCode(),
                            affaire.getDenomination());
            }

            if (affairesDuplicates.size() > 0) {
                boolean authorize = false;
                // If current affaire is not registered and found affaires got SIRET =>
                // authorize it
                if (affaire.getIsUnregistered())
                    for (Affaire affaireDuplicate : affairesDuplicates)
                        if (affaireDuplicate.getSiren() != null || affaireDuplicate.getSiret() != null)
                            authorize = true;

                if (!authorize)
                    throw new OsirisDuplicateException(affairesDuplicates.stream().map(Affaire::getId).toList());
            }
        }

        // If mails already exists, get their ids
        if (affaire != null && affaire.getMails() != null && affaire.getMails().size() > 0)
            mailService.populateMailIds(affaire.getMails());

        // If phones already exists, get their ids
        if (affaire != null && affaire.getPhones() != null && affaire.getPhones().size() > 0) {
            phoneService.populatePhoneIds(affaire.getPhones());
        }

        Affaire affaireSaved = affaireRepository.save(affaire);
        indexEntityService.indexEntity(affaire);
        return affaireSaved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexAffaire() {
        List<Affaire> affaires = IterableUtils.toList(affaireRepository.findAll());
        if (affaires != null)
            for (Affaire affaire : affaires)
                indexEntityService.indexEntity(affaire);
    }

}
