package com.jss.jssbackend.modules.tiers.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.libs.search.model.IndexEntity;
import com.jss.jssbackend.libs.search.service.IndexEntityService;
import com.jss.jssbackend.libs.search.service.SearchService;
import com.jss.jssbackend.modules.miscellaneous.model.Document;
import com.jss.jssbackend.modules.tiers.model.Responsable;
import com.jss.jssbackend.modules.tiers.model.Tiers;
import com.jss.jssbackend.modules.tiers.repository.TiersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiersServiceImpl implements TiersService {

    @Autowired
    TiersRepository tiersRepository;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    MailService mailService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    IndexEntityService indexEntityService;

    @Autowired
    SearchService searchService;

    @Override
    public Tiers getTiersById(Integer id) {
        Optional<Tiers> tiers = tiersRepository.findById(id);
        if (!tiers.isEmpty())
            return tiers.get();
        return null;
    }

    @Override
    public Tiers addOrUpdateTiers(Tiers tiers) {
        // If mails already exists, get their ids
        if (tiers != null && tiers.getMails() != null && tiers.getMails().size() > 0)
            mailService.populateMailIds(tiers.getMails());

        // If phones already exists, get their ids
        if (tiers != null && tiers.getPhones() != null && tiers.getPhones().size() > 0) {
            phoneService.populateMPhoneIds(tiers.getPhones());
        }

        // If document mails already exists, get their ids
        if (tiers.getDocuments() != null && tiers.getDocuments().size() > 0) {
            for (Document document : tiers.getDocuments()) {
                if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
                    mailService.populateMailIds(document.getMailsAffaire());
                if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
                    mailService.populateMailIds(document.getMailsClient());
            }
        }

        tiers = tiersRepository.save(tiers);
        indexEntityService.indexEntity(tiers, tiers.getId());
        if (tiers.getResponsables() != null && tiers.getResponsables().size() > 0) {
            for (Responsable responsable : tiers.getResponsables()) {
                indexEntityService.indexEntity(responsable, responsable.getId());

                if (responsable.getDocuments() != null && responsable.getDocuments().size() > 0) {
                    for (Document document : responsable.getDocuments()) {
                        if (document.getMailsAffaire() != null && document.getMailsAffaire().size() > 0)
                            mailService.populateMailIds(document.getMailsAffaire());
                        if (document.getMailsClient() != null && document.getMailsClient().size() > 0)
                            mailService.populateMailIds(document.getMailsClient());
                    }
                }
            }
        }
        return tiers;
    }

    @Override
    public Tiers getTiersByIdResponsable(Integer idResponsable) {
        Responsable responsable = responsableService.getResponsable(idResponsable);
        if (responsable != null)
            return this.getTiersById(responsable.getTiers().getId());
        return null;
    }

    @Override
    public List<Tiers> getIndividualTiersByKeyword(String searchedValue) {
        List<Tiers> foundTiers = new ArrayList<Tiers>();
        List<IndexEntity> tiers = searchService.searchForEntities(searchedValue, Tiers.class.getSimpleName());
        if (tiers != null && tiers.size() > 0) {
            for (IndexEntity t : tiers) {
                List<Tiers> individualTiers = tiersRepository.findByIsIndividualAndIdTiers(t.getEntityId());
                if (individualTiers != null && individualTiers.size() == 1)
                    foundTiers.add(individualTiers.get(0));
            }
        }
        return foundTiers;
    }
}
