package com.jss.osiris.modules.osiris.quotation.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.GlobalExceptionHandler;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.myjss.profile.service.UserScopeService;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.service.CityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Rna;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.Activite;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.AdresseDomicile;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.AutresEtablissement;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.PersonneMorale;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneCompany;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeVoie;
import com.jss.osiris.modules.osiris.quotation.repository.AffaireRepository;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.FormeExerciceActivitePrincipalService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.FormeJuridiqueService;
import com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials.TypeVoieService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@org.springframework.stereotype.Service
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

    @Autowired
    UserScopeService userScopeService;

    @Autowired
    CustomerOrderService customerOrderService;

    @Autowired
    ServiceService serviceService;

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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Affaire addOrUpdateAffaire(Affaire affaire) throws OsirisDuplicateException, OsirisException {
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
            phoneService.populatePhoneIds(affaire.getPhones());
        }

        Affaire affaireSaved = affaireRepository.save(affaire);
        batchService.declareNewBatch(Batch.REINDEX_AFFAIRE, affaire.getId());
        return affaireSaved;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reindexAffaire() throws OsirisException {
        List<Affaire> affaires = IterableUtils.toList(affaireRepository.findAll());
        if (affaires != null)
            for (Affaire affaire : affaires)
                batchService.declareNewBatch(Batch.REINDEX_AFFAIRE, affaire.getId());
    }

    @Override
    public List<Affaire> getAffairesFromSiren(String siren) throws OsirisException, OsirisClientMessageException {
        List<Affaire> existingAffaires = affaireRepository.findBySiren(siren);
        if (existingAffaires != null && existingAffaires.size() > 0)
            return existingAffaires;
        List<RneCompany> rneCompanies = rneDelegateService.getCompanyBySiren(siren);
        List<Affaire> affaires = new ArrayList<Affaire>();
        if (rneCompanies != null && rneCompanies.size() > 0)
            for (RneCompany rneCompany : rneCompanies)
                affaires.add(getAffaireFromRneCompany(rneCompany, null));
        return affaires;
    }

    @Override
    public List<Affaire> getAffairesFromSiret(String siret) throws OsirisException, OsirisClientMessageException {
        Affaire affaire = affaireRepository.findBySiret(siret);
        if (affaire != null)
            return Arrays.asList(affaire);
        List<RneCompany> rneCompanies = rneDelegateService.getCompanyBySiret(siret);
        List<Affaire> affaires = new ArrayList<Affaire>();
        if (rneCompanies != null && rneCompanies.size() > 0) {
            for (RneCompany rneCompany : rneCompanies)
                affaires.add(getAffaireFromRneCompany(rneCompany, siret));
        } else {
            return getAffairesFromSiren(siret);
        }
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
            } else {
                // else persist it
                affaire = addOrUpdateAffaire(affaire);
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

        if (isForceRefresh) {
            affaire.setAcronym(null);
            affaire.setAddress(null);
            affaire.setCedexComplement(null);
            affaire.setCity(null);
            affaire.setCompetentAuthority(null);
            affaire.setCountry(null);
            affaire.setDenomination(null);
            affaire.setLegalForm(null);
            affaire.setMainActivity(null);
            affaire.setPostalCode(null);
            affaire.setShareCapital(null);
        }

        affaire.setSiren(rneCompany.getSiren());

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
                if (affaire.getAddress() == null || affaire.getAddress().equals(""))
                    affaire.setAddress(adressElements.stream().filter(s -> s != null && !s.isEmpty())
                            .collect(Collectors.joining(" ")));

            companyPostalCode = address.getCodePostal();

            if (affaire.getPostalCode() == null || affaire.getPostalCode().length() == 0)
                if (affaire.getPostalCode() == null || affaire.getPostalCode().equals(""))
                    affaire.setPostalCode(companyPostalCode);

            if (affaire.getCedexComplement() == null || affaire.getCedexComplement().length() == 0)
                if (affaire.getCedexComplement() == null || affaire.getCedexComplement().equals(""))
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
                if (affaire.getCity() == null)
                    affaire.setCity(companyCity);
            }

            if (affaire.getCountry() == null)
                affaire.setCountry(constantService.getCountryFrance());
        }

        List<Activite> activites = getActivitesFromRneCompany(rneCompany, affaire.getSiren(), affaire.getSiret());
        if (activites != null && activites.size() > 0) {
            Integer nbrEmployee = 0;
            for (Activite activite : activites)
                if (activite.getEffectifSalarie() != null && activite.getEffectifSalarie().getNombreSalarie() != null)
                    nbrEmployee += activite.getEffectifSalarie().getNombreSalarie();
            if (nbrEmployee > 0)
                affaire.setEmployeeNumber(nbrEmployee);

            affaire.setApeCodes(String.join(";",
                    activites.stream().map(Activite::getCodeApe).distinct().collect(Collectors.toList())));
        }

        if (affaire.getLegalForm() == null) {
            if (affaire.getLegalForm() == null)
                affaire.setLegalForm(formeJuridiqueService
                        .getFormeJuridique(
                                rneCompany.getFormality().getContent().getNatureCreation().getFormeJuridique()
                                        .getCode()));
        }

        affaire.setIsMainOffice(isEtablissementPrincipal(rneCompany, affaire.getSiren(), affaire.getSiret()));

        if (affaire.getMainActivity() == null
                && rneCompany.getFormality().getContent().getFormeExerciceActivitePrincipale() != null) {
            if (affaire.getMainActivity() == null)
                affaire.setMainActivity(formeExerciceActivitePrincipalService.getFormeExerciceActivitePrincipal(
                        rneCompany.getFormality().getContent().getFormeExerciceActivitePrincipale().getCode()));
        }

        if ((affaire.getShareCapital() == null || affaire.getShareCapital().compareTo(new BigDecimal(0)) <= 0)
                && personneMorale.getIdentite() != null
                && personneMorale.getIdentite().getDescription() != null
                && personneMorale.getIdentite().getDescription().getMontantCapital() != null)
            if (affaire.getShareCapital() == null || affaire.getShareCapital().equals(0f))
                affaire.setShareCapital(
                        personneMorale.getIdentite().getDescription().getMontantCapital().multiply(new BigDecimal(1)));

        if ((affaire.getAcronym() == null || affaire.getAcronym().length() == 0)
                && personneMorale.getIdentite() != null && personneMorale.getIdentite().getDescription() != null
                && personneMorale.getIdentite().getDescription().getSigle() != null)
            if (affaire.getAcronym() == null || affaire.getAcronym().equals(""))
                affaire.setAcronym(personneMorale.getIdentite().getDescription().getSigle());

        if ((affaire.getDenomination() == null || affaire.getDenomination().length() == 0)
                && personneMorale.getIdentite() != null && personneMorale.getIdentite().getEntreprise() != null
                && personneMorale.getIdentite().getEntreprise().getDenomination() != null)
            if (affaire.getDenomination() == null || affaire.getDenomination().equals(""))
                affaire.setDenomination(personneMorale.getIdentite().getEntreprise().getDenomination());

        if (affaire.getCity() != null && affaire.getCompetentAuthority() == null) {
            List<CompetentAuthority> competentAuthoritiesFound = competentAuthorityService
                    .getCompetentAuthorityByCityAndAuthorityType(affaire.getCity(),
                            constantService.getCompetentAuthorityTypeRcs());
            if (competentAuthoritiesFound != null && competentAuthoritiesFound.size() == 1)
                if (affaire.getCompetentAuthority() == null)
                    affaire.setCompetentAuthority(competentAuthoritiesFound.get(0));
        }
    }

    private Boolean isEtablissementPrincipal(RneCompany company, String siren, String siret) {
        if (company == null || company.getFormality() == null || company.getFormality().getContent() == null
                || company.getFormality().getContent().getPersonneMorale() == null)
            return null;

        if (siren != null && (siret == null || siret.length() == 0)) {
            return true;
        } else if (siret != null) {
            if (company.getFormality().getContent().getPersonneMorale().getAutresEtablissements() != null)
                for (AutresEtablissement other : company.getFormality().getContent().getPersonneMorale()
                        .getAutresEtablissements()) {
                    if (other.getDescriptionEtablissement() != null
                            && other.getDescriptionEtablissement().getSiret() != null
                            && other.getDescriptionEtablissement().getSiret().equals(siret))
                        return false;
                }
            if (company.getFormality().getContent().getPersonneMorale()
                    .getEtablissementPrincipal() != null
                    && siret.equals(company.getFormality().getContent().getPersonneMorale()
                            .getEtablissementPrincipal().getDescriptionEtablissement().getSiret()))
                return true;
            return false;
        }
        return null;
    }

    private AdresseDomicile getAddressFromRneCompany(RneCompany company, String siren, String siret) {
        if (company == null || company.getFormality() == null || company.getFormality().getContent() == null
                || company.getFormality().getContent().getPersonneMorale() == null)
            return null;

        if (siren != null && (siret == null || siret.length() == 0)) {
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

    private List<Activite> getActivitesFromRneCompany(RneCompany company, String siren, String siret) {
        if (company == null || company.getFormality() == null || company.getFormality().getContent() == null
                || company.getFormality().getContent().getPersonneMorale() == null)
            return null;

        if (siren != null && (siret == null || siret.length() == 0)) {
            if (company.getFormality().getContent().getPersonneMorale().getEtablissementPrincipal() == null) {
                return null;
            } else {
                company.getFormality().getContent().getPersonneMorale().getEtablissementPrincipal().getActivites();
            }
        } else if (siret != null) {
            if (company.getFormality().getContent().getPersonneMorale().getAutresEtablissements() != null)
                for (AutresEtablissement other : company.getFormality().getContent().getPersonneMorale()
                        .getAutresEtablissements()) {
                    if (other.getDescriptionEtablissement() != null
                            && other.getDescriptionEtablissement().getSiret() != null
                            && other.getDescriptionEtablissement().getSiret().equals(siret))
                        return other.getActivites();
                }
            if (company.getFormality().getContent().getPersonneMorale()
                    .getEtablissementPrincipal() != null
                    && siret.equals(company.getFormality().getContent().getPersonneMorale()
                            .getEtablissementPrincipal().getDescriptionEtablissement().getSiret()))
                return company.getFormality().getContent().getPersonneMorale().getEtablissementPrincipal()
                        .getActivites();
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
        List<Affaire> affaires = affaireRepository.getAffairesForUpdate();
        if (affaires != null)
            for (Affaire affaire : affaires) {
                batchService.declareNewBatch(Batch.UPDATE_AFFAIRE_FROM_RNE, affaire.getId());
            }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAffaireFromRne(Affaire affaire)
            throws OsirisException, OsirisClientMessageException, OsirisDuplicateException {
        if (affaire != null) {
            List<RneCompany> rneCompanies = new ArrayList<RneCompany>();
            if (affaire.getSiret() != null && affaire.getSiret().length() > 0)
                rneCompanies = rneDelegateService.getCompanyBySiret(affaire.getSiret());
            else if (affaire.getSiren() != null && affaire.getSiren().length() > 0)
                rneCompanies = rneDelegateService.getCompanyBySiren(affaire.getSiren());

            if (rneCompanies != null && rneCompanies.size() == 1)
                updateAffaireFromRneCompany(affaire, rneCompanies.get(0), false);
            affaire.setLastRneUpdate(LocalDate.now());
            addOrUpdateAffaire(affaire);
        }
    }

    @Override
    public List<Affaire> getAffairesForCurrentUser(Integer page, String sortBy, String searchText) {
        List<Responsable> responsables = userScopeService.getUserCurrentScopeResponsables();
        if (responsables == null || responsables.size() == 0)
            return new ArrayList<Affaire>();

        Order orderDenomination = new Order(Direction.ASC, "denomination");
        Order orderFirstname = new Order(Direction.ASC, "firstname");
        Order orderLastname = new Order(Direction.ASC, "lastname");

        Integer idAffaire = 0;
        if (searchText != null && searchText.length() > 0)
            try {
                idAffaire = Integer.parseInt(searchText);
            } catch (Exception e) {
            }

        if (sortBy.equals("nameDesc")) {
            orderDenomination = new Order(Direction.DESC, "denomination");
            orderFirstname = new Order(Direction.DESC, "firstname");
            orderLastname = new Order(Direction.DESC, "lastname");
        }
        Sort sort = Sort.by(Arrays.asList(orderDenomination, orderLastname, orderFirstname));
        Pageable pageableRequest = PageRequest.of(page, 10, sort);
        return affaireRepository.getAffairesForResponsables(pageableRequest, responsables, searchText, idAffaire);
    }

    @Override
    public List<Attachment> getAttachmentsForAffaire(Affaire affaire) throws OsirisException {
        List<Attachment> attachments = new ArrayList<Attachment>();
        List<CustomerOrder> customerOrders = customerOrderService.searchOrdersForCurrentUserAndAffaire(affaire);

        if (customerOrders != null)
            for (CustomerOrder customerOrder : customerOrders)
                if (customerOrder.getAssoAffaireOrders() != null)
                    for (AssoAffaireOrder assoAffaireOrder : customerOrder.getAssoAffaireOrders())
                        if (assoAffaireOrder.getServices() != null)
                            for (Service service : assoAffaireOrder.getServices()) {
                                List<Attachment> serviceAttachment = serviceService
                                        .getAttachmentsForProvisionOfService(service);
                                if (serviceAttachment != null)
                                    attachments.addAll(serviceAttachment);
                            }

        if (attachments.size() > 0)
            attachments.sort(new Comparator<Attachment>() {
                @Override
                public int compare(Attachment c0, Attachment c1) {
                    if (c0 == null && c1 == null)
                        return 0;
                    if (c0 != null && c1 == null)
                        return 1;
                    if (c0 == null && c1 != null)
                        return -1;
                    if (c1 != null && c0 != null)
                        return c1.getCreatDateTime().compareTo(c0.getCreatDateTime());
                    return 0;
                }
            });
        return attachments;
    }

}
