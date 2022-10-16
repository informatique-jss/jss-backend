package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional(rollbackFor = Exception.class)
    public Affaire addOrUpdateAffaire(Affaire affaire) {
        if (affaire.getRna() != null)
            affaire.setRna(affaire.getRna().toUpperCase().replaceAll(" ", ""));
        if (affaire.getSiren() != null)
            affaire.setSiren(affaire.getSiren().toUpperCase().replaceAll(" ", ""));
        if (affaire.getSiret() != null)
            affaire.setSiret(affaire.getSiret().toUpperCase().replaceAll(" ", ""));

        // If mails already exists, get their ids
        if (affaire != null && affaire.getMails() != null && affaire.getMails().size() > 0)
            mailService.populateMailIds(affaire.getMails());

        // If phones already exists, get their ids
        if (affaire != null && affaire.getPhones() != null && affaire.getPhones().size() > 0) {
            phoneService.populateMPhoneIds(affaire.getPhones());
        }

        Affaire affaireSaved = affaireRepository.save(affaire);
        return affaireSaved;
    }

}
