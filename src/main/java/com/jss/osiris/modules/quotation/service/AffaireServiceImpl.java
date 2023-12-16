package com.jss.osiris.modules.quotation.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.search.service.IndexEntityService;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.service.CityService;
import com.jss.osiris.modules.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Rna;
import com.jss.osiris.modules.quotation.model.guichetUnique.AdresseDomicile;
import com.jss.osiris.modules.quotation.model.guichetUnique.AutresEtablissement;
import com.jss.osiris.modules.quotation.model.guichetUnique.PersonneMorale;
import com.jss.osiris.modules.quotation.model.guichetUnique.RneCompany;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.TypeVoie;
import com.jss.osiris.modules.quotation.repository.AffaireRepository;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.FormeExerciceActivitePrincipalService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.FormeJuridiqueService;
import com.jss.osiris.modules.quotation.service.guichetUnique.referentials.TypeVoieService;

@Service
public class AffaireServiceImpl implements AffaireService {

    @Autowired
    AffaireRepository affaireRepository;

    @Autowired
    MailService mailService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    RneDelegateService rneDelegateService;

    @Autowired
    RnaDelegateService rnaDelegateService;

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Autowired
    CityService cityService;

    @Autowired
    ConstantService constantService;

    @Autowired
    FormeJuridiqueService formeJuridiqueService;

    @Autowired
    FormeExerciceActivitePrincipalService formeExerciceActivitePrincipalService;

    @Autowired
    TypeVoieService typeVoieService;

    @Autowired
    GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    BatchService batchService;

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

    private Affaire getAffaireByRna(String rna) {
        return affaireRepository.findByRna(rna);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Affaire refreshAffaireFromRne(Affaire affaire)
            throws OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        affaire = getAffaire(affaire.getId());
        List<RneCompany> rneCompanies = rneDelegateService.getCompanyBySiret(affaire.getSiret());
        if (rneCompanies != null && rneCompanies.size() == 1)
            updateAffaireFromRneCompany(affaire, rneCompanies.get(0), true);
        addOrUpdateAffaire(affaire);
        return affaire;
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

    @Override
    public List<Affaire> getAffairesFromSiren(String siren) throws OsirisException, OsirisClientMessageException {
        List<RneCompany> rneCompanies = rneDelegateService.getCompanyBySiren(siren);
        List<Affaire> affaires = new ArrayList<Affaire>();
        if (rneCompanies != null && rneCompanies.size() > 0)
            for (RneCompany rneCompany : rneCompanies)
                affaires.add(getAffaireFromRneCompany(rneCompany, null));
        return affaires;
    }

    @Override
    public List<Affaire> getAffairesFromSiret(String siret) throws OsirisException, OsirisClientMessageException {
        List<RneCompany> rneCompanies = rneDelegateService.getCompanyBySiret(siret);
        List<Affaire> affaires = new ArrayList<Affaire>();
        if (rneCompanies != null && rneCompanies.size() > 0)
            for (RneCompany rneCompany : rneCompanies)
                affaires.add(getAffaireFromRneCompany(rneCompany, siret));
        return affaires;
    }

    @Override
    public List<Affaire> getAffairesFromRna(String rna) throws OsirisException {
        List<Rna> rnaCompanies = rnaDelegateService.getRna(rna);
        List<Affaire> affaires = new ArrayList<Affaire>();
        if (rnaCompanies != null && rnaCompanies.size() > 0)
            for (Rna rnaCompany : rnaCompanies)
                affaires.add(getAffaireFromRnaCompany(rnaCompany));
        return affaires;
    }

    private Affaire getAffaireFromRneCompany(RneCompany rneCompany, String specificSiret) throws OsirisException {
        Affaire affaire = new Affaire();

        if (rneCompany == null)
            return null;

        affaire.setSiret(specificSiret);
        affaire.setSiren(rneCompany.getSiren());
        updateAffaireFromRneCompany(affaire, rneCompany, false);

        // if already existing affaire, use it
        if (affaire.getSiret() != null) {
            Affaire existingAffaire = getAffaireBySiret(affaire.getSiret());
            if (existingAffaire != null) {
                affaire = existingAffaire;
                updateAffaireFromRneCompany(existingAffaire, rneCompany, false);
            }
        }

        return affaire;
    }

    private void updateAffaireFromRneCompany(Affaire affaire, RneCompany rneCompany, boolean isForceRefresh)
            throws OsirisException {
        PersonneMorale personneMorale = null;

        if (rneCompany == null)
            return;

        if (affaire.getIsIndividual() == null)
            affaire.setIsIndividual(false);

        if (affaire.getIsUnregistered() == null)
            affaire.setIsUnregistered(false);

        if (rneCompany != null && rneCompany.getFormality() != null && rneCompany.getFormality().getContent() != null
                && rneCompany.getFormality().getContent().getPersonneMorale() != null
                && rneCompany.getFormality().getContent() != null)
            personneMorale = rneCompany.getFormality().getContent().getPersonneMorale();

        if (personneMorale == null)
            return;

        if (affaire.getSiret() == null && personneMorale.getEtablissementPrincipal() != null
                && personneMorale.getEtablissementPrincipal().getDescriptionEtablissement() != null)
            affaire.setSiret(personneMorale.getEtablissementPrincipal().getDescriptionEtablissement().getSiret());

        AdresseDomicile address = getAddressFromRneCompany(rneCompany, affaire.getSiren(), affaire.getSiret());
        if (address != null && address != null) {

            City companyCity = null;
            String companyPostalCode = "";

            ArrayList<String> adressElements = new ArrayList<String>();
            adressElements.add(address.getNumVoie());

            if (address.getTypeVoie() != null) {
                TypeVoie typeVoie = typeVoieService.getTypeVoie(address.getTypeVoie().getCode());
                if (typeVoie != null)
                    adressElements.add(typeVoie.getLabel());
            }

            adressElements.add(address.getVoie());

            if (affaire.getAddress() == null || affaire.getAddress().length() == 0)
                if (isForceRefresh || affaire.getAddress() == null || affaire.getAddress().equals(""))
                    affaire.setAddress(adressElements.stream().filter(s -> s != null && !s.isEmpty())
                            .collect(Collectors.joining(" ")));

            companyPostalCode = address.getCodePostal();

            if (affaire.getPostalCode() == null || affaire.getPostalCode().length() == 0)
                if (isForceRefresh || affaire.getPostalCode() == null || affaire.getPostalCode().equals(""))
                    affaire.setPostalCode(companyPostalCode);

            if (affaire.getCedexComplement() == null || affaire.getCedexComplement().length() == 0)
                if (isForceRefresh || affaire.getCedexComplement() == null || affaire.getCedexComplement().equals(""))
                    affaire.setCedexComplement(
                            address.getDistributionSpeciale());

            if (address.getCommune() != null && affaire.getCity() == null) {
                List<City> foundCities = cityService
                        .getCitiesByLabel(address.getCommune());
                if (foundCities != null) {
                    if (foundCities.size() == 1)
                        companyCity = foundCities.get(0);
                    if (foundCities.size() > 1 && companyPostalCode != null)
                        for (City foundCity : foundCities)
                            if (foundCity.getPostalCode() != null
                                    && foundCity.getPostalCode().equals(companyPostalCode))
                                companyCity = foundCity;
                }
                if (companyCity == null && companyPostalCode != null) {
                    foundCities = cityService.getCitiesByPostalCode(companyPostalCode);
                    if (foundCities.size() == 1)
                        companyCity = foundCities.get(0);
                }
                if (isForceRefresh || affaire.getCity() == null)
                    affaire.setCity(companyCity);
            }

            if (isForceRefresh || affaire.getCountry() == null)
                affaire.setCountry(constantService.getCountryFrance());
        }

        if (affaire.getLegalForm() == null) {
            if (isForceRefresh || affaire.getLegalForm() == null)
                affaire.setLegalForm(formeJuridiqueService
                        .getFormeJuridique(
                                rneCompany.getFormality().getContent().getNatureCreation().getFormeJuridique()
                                        .getCode()));
        }

        if (affaire.getMainActivity() == null
                && rneCompany.getFormality().getContent().getFormeExerciceActivitePrincipale() != null) {
            if (isForceRefresh || affaire.getMainActivity() == null)
                affaire.setMainActivity(formeExerciceActivitePrincipalService.getFormeExerciceActivitePrincipal(
                        rneCompany.getFormality().getContent().getFormeExerciceActivitePrincipale().getCode()));
        }

        if ((affaire.getShareCapital() == null || affaire.getShareCapital() <= 0f)
                && personneMorale.getIdentite() != null
                && personneMorale.getIdentite().getDescription() != null
                && personneMorale.getIdentite().getDescription().getMontantCapital() != null)
            if (isForceRefresh || affaire.getShareCapital() == null || affaire.getShareCapital().equals(0f))
                affaire.setShareCapital(personneMorale.getIdentite().getDescription().getMontantCapital() * 1.0f);

        if ((affaire.getAcronym() == null || affaire.getAcronym().length() == 0)
                && personneMorale.getIdentite() != null && personneMorale.getIdentite().getDescription() != null
                && personneMorale.getIdentite().getDescription().getSigle() != null)
            if (isForceRefresh || affaire.getAcronym() == null || affaire.getAcronym().equals(""))
                affaire.setAcronym(personneMorale.getIdentite().getDescription().getSigle());

        if ((affaire.getDenomination() == null || affaire.getDenomination().length() == 0)
                && personneMorale.getIdentite() != null && personneMorale.getIdentite().getEntreprise() != null
                && personneMorale.getIdentite().getEntreprise().getDenomination() != null)
            if (isForceRefresh || affaire.getDenomination() == null || affaire.getDenomination().equals(""))
                affaire.setDenomination(personneMorale.getIdentite().getEntreprise().getDenomination());

        if (affaire.getCity() != null && affaire.getCompetentAuthority() == null) {
            List<CompetentAuthority> competentAuthoritiesFound = competentAuthorityService
                    .getCompetentAuthorityByCityAndAuthorityType(affaire.getCity(),
                            constantService.getCompetentAuthorityTypeRcs());
            if (competentAuthoritiesFound != null && competentAuthoritiesFound.size() == 1)
                if (isForceRefresh || affaire.getCompetentAuthority() == null)
                    affaire.setCompetentAuthority(competentAuthoritiesFound.get(0));
        }
    }

    private AdresseDomicile getAddressFromRneCompany(RneCompany company, String siren, String siret) {
        if (siren == null)
            return null;

        if (company == null || company.getFormality() == null || company.getFormality().getContent() == null
                || company.getFormality().getContent().getPersonneMorale() == null)
            return null;

        if (siret == null || siret.length() == 0) {
            if (company.getFormality().getContent().getPersonneMorale().getEtablissementPrincipal() == null) {
                return company.getFormality().getContent().getPersonneMorale().getAdresseEntreprise().getAdresse();
            } else {
                company.getFormality().getContent().getPersonneMorale().getEtablissementPrincipal().getAdresse();
            }
        } else if (siret != null) {
            if (company.getFormality().getContent().getPersonneMorale().getAutresEtablissements() != null)
                for (AutresEtablissement other : company.getFormality().getContent().getPersonneMorale()
                        .getAutresEtablissements()) {
                    if (other.getDescriptionEtablissement() != null
                            && other.getDescriptionEtablissement().getSiret() != null
                            && other.getDescriptionEtablissement().getSiret().equals(siret))
                        return other.getAdresse();
                }
            if (company.getFormality().getContent().getPersonneMorale()
                    .getEtablissementPrincipal() != null
                    && siret.equals(company.getFormality().getContent().getPersonneMorale()
                            .getEtablissementPrincipal().getDescriptionEtablissement().getSiret()))
                return company.getFormality().getContent().getPersonneMorale().getEtablissementPrincipal().getAdresse();
            return company.getFormality().getContent().getPersonneMorale().getAdresseEntreprise().getAdresse();
        }
        return null;
    }

    private Affaire getAffaireFromRnaCompany(Rna rnaCompany) throws OsirisException {
        Affaire affaire = new Affaire();

        if (rnaCompany == null)
            return null;

        affaire.setRna(rnaCompany.getAssociation().getId_association());
        updateAffaireFromRnaCompany(affaire, rnaCompany);

        // if already existing affaire, use it
        if (affaire.getRna() != null) {
            Affaire existingAffaire = getAffaireByRna(affaire.getRna());
            if (existingAffaire != null) {
                affaire = existingAffaire;
                updateAffaireFromRnaCompany(existingAffaire, rnaCompany);
            }
        }

        return affaire;
    }

    private void updateAffaireFromRnaCompany(Affaire affaire, Rna rnaCompany) throws OsirisException {
        if (rnaCompany == null)
            return;

        if (affaire.getIsIndividual() == null)
            affaire.setIsIndividual(false);

        if (affaire.getIsUnregistered() == null)
            affaire.setIsUnregistered(false);

        City companyCity = null;
        String companyPostalCode = "";

        ArrayList<String> adressElements = new ArrayList<String>();
        adressElements.add(rnaCompany.getAssociation().getAdresse_numero_voie());
        adressElements.add(rnaCompany.getAssociation().getAdresse_type_voie());
        adressElements.add(rnaCompany.getAssociation().getAdresse_libelle_voie());

        if (affaire.getAddress() == null || affaire.getAddress().length() == 0)
            affaire.setAddress(adressElements.stream().filter(s -> s != null && !s.isEmpty())
                    .collect(Collectors.joining(" ")));

        companyPostalCode = rnaCompany.getAssociation().getAdresse_code_postal();

        if (affaire.getPostalCode() == null || affaire.getPostalCode().length() == 0)
            affaire.setPostalCode(companyPostalCode);

        if (rnaCompany.getAssociation().getAdresse_libelle_commune() != null && affaire.getCity() == null) {
            List<City> foundCities = cityService
                    .getCitiesByLabel(rnaCompany.getAssociation().getAdresse_libelle_commune());
            if (foundCities != null) {
                if (foundCities.size() == 1)
                    companyCity = foundCities.get(0);
                if (foundCities.size() > 1 && companyPostalCode != null)
                    for (City foundCity : foundCities)
                        if (foundCity.getPostalCode() != null
                                && foundCity.getPostalCode().equals(companyPostalCode))
                            companyCity = foundCity;
            }
            if (companyCity == null && companyPostalCode != null) {
                foundCities = cityService.getCitiesByPostalCode(companyPostalCode);
                if (foundCities.size() == 1)
                    companyCity = foundCities.get(0);
            }
            affaire.setCity(companyCity);
        }

        if ((affaire.getDenomination() == null || affaire.getDenomination().length() == 0)
                && rnaCompany.getAssociation().getTitre_court() != null)
            affaire.setDenomination(rnaCompany.getAssociation().getTitre_court());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAffairesFromRne() throws OsirisException, OsirisClientMessageException {
        List<Affaire> affaires = getAffaires();
        if (affaires != null)
            for (Affaire affaire : affaires) {
                batchService.declareNewBatch(Batch.UPDATE_AFFAIRE_FROM_RNE, affaire.getId());
            }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAffaireFromRne(Affaire affaire) throws OsirisException, OsirisClientMessageException {
        if (affaire != null) {
            List<RneCompany> rneCompanies = new ArrayList<RneCompany>();
            if (affaire.getSiret() != null && affaire.getSiret().length() > 0)
                rneCompanies = rneDelegateService.getCompanyBySiret(affaire.getSiret());
            else if (affaire.getSiren() != null && affaire.getSiren().length() > 0)
                rneCompanies = rneDelegateService.getCompanyBySiren(affaire.getSiren());

            if (rneCompanies != null && rneCompanies.size() == 1)
                updateAffaireFromRneCompany(affaire, rneCompanies.get(0), false);
        }
    }

}
