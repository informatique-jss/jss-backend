package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.osiris.crm.model.Candidacy;
import com.jss.osiris.modules.osiris.crm.repository.CandidacyRepository;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;

@Service
public class CandidacyServiceImpl implements CandidacyService {

    @Autowired
    CandidacyRepository candidacyRepository;

    @Autowired
    NotificationService notificationService;

    @Autowired
    MailService mailService;

    @Autowired
    MailHelper mailHelper;

    @Override
    public List<Candidacy> getCandidacies() {
        return IterableUtils.toList(candidacyRepository.findAll());
    }

    @Override
    public List<Candidacy> getCandidacies(Boolean isDisplayTreated) {
        return candidacyRepository.findByIsTrated(isDisplayTreated);
    }

    @Override
    public Candidacy getCandidacy(Integer id) {
        Optional<Candidacy> candidacy = candidacyRepository.findById(id);
        if (candidacy.isPresent())
            return candidacy.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Candidacy declareNewCandidacy(
            Candidacy candidacy) throws OsirisException {
        addOrUpdateCandidacy(candidacy);
        mailHelper.sendConfirmationCandidacyMyJss(candidacy);
        notificationService.notifyNewCandidacy(candidacy);

        return candidacy;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Candidacy addOrUpdateCandidacy(Candidacy candidacy) throws OsirisException {
        if (candidacy.getIsTreated() == null)
            candidacy.setIsTreated(false);
        mailService.populateMailId(candidacy.getMail());
        candidacy = candidacyRepository.save(candidacy);
        return candidacy;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markCandidacyAsUnTreated(Candidacy candidacy) throws OsirisException {
        candidacy = getCandidacy(candidacy.getId());
        candidacy.setIsTreated(false);
        addOrUpdateCandidacy(candidacy);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markCandidacyAsTreated(Candidacy candidacy) throws OsirisException {
        candidacy = getCandidacy(candidacy.getId());
        candidacy.setIsTreated(true);
        addOrUpdateCandidacy(candidacy);
    }
}
