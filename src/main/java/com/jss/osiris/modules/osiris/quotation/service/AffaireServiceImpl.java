package com.jss.osiris.modules.osiris.quotation.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.audit.service.AuditService;
import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Attachment;
import com.jss.osiris.modules.osiris.miscellaneous.model.City;
import com.jss.osiris.modules.osiris.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.model.Phone;
import com.jss.osiris.modules.osiris.miscellaneous.service.CityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.CompetentAuthorityService;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.miscellaneous.service.PhoneService;
import com.jss.osiris.modules.osiris.profile.service.EmployeeService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.AssoAffaireOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.Rna;
import com.jss.osiris.modules.osiris.quotation.model.Service;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.Activite;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.AdresseDomicile;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.AutresEtablissement;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.PersonneMorale;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.PersonnePhysique;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneCompany;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.RneResult;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeExerciceActivitePrincipal;
import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeJuridique;
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
    CustomerOrderService customerOrderService;

    @Autowired
    ServiceService serviceService;

    @Autowired
    EmployeeService employeeService;

    @Autowired
    AffaireRneUpdateHelper affaireRneUpdateHelper;

    @Autowired
    ValidationHelper validationHelper;

    @Autowired
    AuditService auditService;

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
    public List<Affaire> getAffaireBySiret(String siret) {
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
            updateAffaireFromRneCompany(affaire, rneCompanies.get(0));
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
        if (affaire.getAssoAffaireOrders() != null)
            for (AssoAffaireOrder assoAffaireOrder : affaire.getAssoAffaireOrders()) {
                if (assoAffaireOrder.getCustomerOrder() != null)
                    batchService.declareNewBatch(Batch.REINDEX_CUSTOMER_ORDER,
                            assoAffaireOrder.getCustomerOrder().getId());
                if (assoAffaireOrder.getQuotation() != null)
                    batchService.declareNewBatch(Batch.REINDEX_QUOTATION, assoAffaireOrder.getQuotation().getId());
            }

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
        List<RneCompany> rneCompanies = rneDelegateService.getCompanyBySiren(siren);
        List<Affaire> affaires = new ArrayList<Affaire>();
        if (rneCompanies != null && rneCompanies.size() > 0)
            for (RneCompany rneCompany : rneCompanies)
                affaires.add(getAffaireFromRneCompany(rneCompany, null, false));
        return affaires;
    }

    @Override
    public List<Affaire> getAffairesFromSiret(String siret) throws OsirisException, OsirisClientMessageException {
        List<Affaire> affaires = affaireRepository.findAllBySiret(siret);
        if (affaires != null && affaires.size() > 0)
            return affaires;
        List<RneCompany> rneCompanies = rneDelegateService.getCompanyBySiret(siret);
        affaires = new ArrayList<Affaire>();
        if (rneCompanies != null && rneCompanies.size() > 0) {
            for (RneCompany rneCompany : rneCompanies)
                affaires.add(getAffaireFromRneCompany(rneCompany, siret, false));
        } else {
            return getAffairesFromSiren(siret);
        }
        return affaires;
    }

    @Override
    public List<Affaire> getAffairesFromSiretFromWebsite(String siret)
            throws OsirisException, OsirisClientMessageException {
        siret = siret.replaceAll(" ", "");
        if (validationHelper.validateSiret(siret)) {
            // Find in local DB
            List<Affaire> affaires = affaireRepository.findAllBySiret(siret);
            if (affaires != null && affaires.size() > 0)
                return affaires;

            List<RneCompany> rneCompanies = rneDelegateService.getCompanyBySiret(siret);
            affaires = new ArrayList<Affaire>();
            if (rneCompanies != null && rneCompanies.size() > 0) {
                for (RneCompany rneCompany : rneCompanies)
                    affaires.add(getAffaireFromRneCompany(rneCompany, siret, false));
            }
            return affaires;
        } else if (validationHelper.validateSiren(siret)) {
            // Generate all possibilities from siret
            List<RneCompany> rneCompanies = rneDelegateService.getCompanyBySiren(siret);
            List<Affaire> affaires = new ArrayList<Affaire>();
            if (rneCompanies != null && rneCompanies.size() > 0)
                for (RneCompany rneCompany : rneCompanies) {
                    List<String> sirets = getSiretsFromRneCompany(rneCompany, false);
                    if (siret != null) {
                        for (String siretFromRne : sirets) {
                            Affaire affaireFromRne = getAffaireFromRneCompany(rneCompany, siretFromRne, false);
                            if (affaireFromRne != null)
                                if (affaireFromRne.getIsMainOffice())
                                    affaires.add(0, affaireFromRne);
                                else
                                    affaires.add(affaireFromRne);
                            if (affaires.size() > 50)
                                return affaires;
                        }
                    }
                }
            return affaires;
        }
        return null;
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

    private Affaire getAffaireFromRneCompany(RneCompany rneCompany, String specificSiret, Boolean persistEntity)
            throws OsirisException {
        Affaire affaire = new Affaire();

        if (rneCompany == null)
            return null;

        affaire.setSiret(specificSiret);
        affaire.setSiren(rneCompany.getSiren());
        updateAffaireFromRneCompany(affaire, rneCompany);

        // if already existing affaire, use it
        if (affaire.getSiret() != null) {
            List<Affaire> existingAffaires = getAffaireBySiret(affaire.getSiret());
            if (existingAffaires != null && existingAffaires.size() > 0) {
                affaire = existingAffaires.get(0);
                updateAffaireFromRneCompany(affaire, rneCompany);
            } else if (persistEntity) {
                // else persist it
                affaire = addOrUpdateAffaire(affaire);
            }
        }

        return affaire;
    }

    @Override
    public void updateAffaireFromRneCompany(Affaire affaire, RneCompany rneCompany)
            throws OsirisException {
        PersonneMorale personneMorale = null;
        PersonnePhysique personnePhysique = null;

        if (rneCompany == null)
            return;

        if (affaire.getIsIndividual() == null)
            affaire.setIsIndividual(false);

        if (affaire.getIsUnregistered() == null)
            affaire.setIsUnregistered(false);

        affaire.setSiren(rneCompany.getSiren());

        if (rneCompany != null && rneCompany.getFormality() != null && rneCompany.getFormality().getContent() != null
                && rneCompany.getFormality().getContent().getPersonneMorale() != null)
            personneMorale = rneCompany.getFormality().getContent().getPersonneMorale();

        if (rneCompany != null && rneCompany.getFormality() != null && rneCompany.getFormality().getContent() != null
                && rneCompany.getFormality().getContent().getPersonnePhysique() != null)
            personnePhysique = rneCompany.getFormality().getContent().getPersonnePhysique();

        if (personneMorale != null) {
            if (affaire.getSiret() == null && personneMorale.getEtablissementPrincipal() != null
                    && personneMorale.getEtablissementPrincipal().getDescriptionEtablissement() != null)
                affaire.setSiret(personneMorale.getEtablissementPrincipal().getDescriptionEtablissement().getSiret());

            if (affaire.getSiret() == null && personneMorale.getAutresEtablissements() != null
                    && personneMorale.getAutresEtablissements().size() > 0
                    && personneMorale.getAutresEtablissements().get(0).getDescriptionEtablissement() != null)
                affaire.setSiret(
                        personneMorale.getAutresEtablissements().get(0).getDescriptionEtablissement().getSiret());
        }

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

            affaire.setAddress(
                    adressElements.stream().filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining(" ")));

            companyPostalCode = address.getCodePostal();

            if (companyPostalCode != null)
                affaire.setPostalCode(companyPostalCode);

            if (address.getDistributionSpeciale() != null)
                affaire.setCedexComplement(address.getDistributionSpeciale());

            if (address.getCommune() != null) {
                List<City> foundCities = cityService.getCitiesByLabel(address.getCommune().replaceAll("-", " "));
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
                if (companyCity == null) {
                    companyCity = cityService.getCityByInpiLabel(address.getCommune());
                }
                if (companyCity != null)
                    affaire.setCity(companyCity);
            }

            if (affaire.getCountry() == null)
                affaire.setCountry(constantService.getCountryFrance());
        }

        List<Activite> activites = getActivitesFromRneCompany(rneCompany, affaire.getSiren(), affaire.getSiret());
        if (activites != null && activites.size() > 0) {
            affaire.setApeCodes(String.join(";",
                    activites.stream().map(Activite::getCodeApe).distinct().collect(Collectors.toList())));

            Integer nbrEmployee = 0;
            for (Activite activite : activites)
                if (activite.getEffectifSalarie() != null && activite.getEffectifSalarie().getNombreSalarie() != null)
                    nbrEmployee += activite.getEffectifSalarie().getNombreSalarie();
            if (nbrEmployee > 0)
                affaire.setEmployeeNumber(nbrEmployee);

        }

        FormeJuridique legalForm = formeJuridiqueService.getFormeJuridique(
                rneCompany.getFormality().getContent().getNatureCreation().getFormeJuridique().getCode());
        if (legalForm != null)
            affaire.setLegalForm(legalForm);

        affaire.setIsMainOffice(isEtablissementPrincipal(rneCompany, affaire.getSiren(), affaire.getSiret()));

        if (rneCompany.getFormality().getContent().getFormeExerciceActivitePrincipale() != null) {
            FormeExerciceActivitePrincipal activity = formeExerciceActivitePrincipalService
                    .getFormeExerciceActivitePrincipal(
                            rneCompany.getFormality().getContent().getFormeExerciceActivitePrincipale().getCode());
            if (activity != null)
                affaire.setMainActivity(activity);
        }

        if (personneMorale != null) {
            if (personneMorale.getIdentite() != null
                    && personneMorale.getIdentite().getDescription() != null
                    && personneMorale.getIdentite().getDescription().getMontantCapital() != null)
                affaire.setShareCapital(
                        personneMorale.getIdentite().getDescription().getMontantCapital()
                                .multiply(new BigDecimal(1)));

            if (personneMorale.getIdentite() != null && personneMorale.getIdentite().getDescription() != null
                    && personneMorale.getIdentite().getDescription().getSigle() != null)
                affaire.setAcronym(personneMorale.getIdentite().getDescription().getSigle());

            if (personneMorale.getIdentite() != null && personneMorale.getIdentite().getEntreprise() != null
                    && personneMorale.getIdentite().getEntreprise().getDenomination() != null)
                affaire.setDenomination(personneMorale.getIdentite().getEntreprise().getDenomination());
        } else if (personnePhysique != null) {
            affaire.setIsIndividual(true);
            if (personnePhysique.getIdentite() != null
                    && personnePhysique.getIdentite().getDescription() != null
                    && personnePhysique.getIdentite().getDescription().getMontantCapital() != null)
                affaire.setShareCapital(personnePhysique.getIdentite().getDescription().getMontantCapital()
                        .multiply(new BigDecimal(1)));

            if (personnePhysique.getIdentite() != null && personnePhysique.getIdentite().getDescription() != null
                    && personnePhysique.getIdentite().getDescription().getSigle() != null)
                affaire.setAcronym(personnePhysique.getIdentite().getDescription().getSigle());

            if (personnePhysique.getIdentite() != null && personnePhysique.getIdentite().getEntreprise() != null
                    && personnePhysique.getIdentite().getEntreprise().getDenomination() != null)
                affaire.setDenomination(personnePhysique.getIdentite().getEntreprise().getDenomination());
        }

        if (affaire.getCity() != null) {
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
                || company.getFormality().getContent().getPersonneMorale() == null
                        && company.getFormality().getContent().getPersonnePhysique() == null)
            return null;

        if (siren != null && (siret == null || siret.length() == 0)) {
            return true;
        } else if (siret != null) {
            if (company.getFormality().getContent().getPersonneMorale() != null) {
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
            } else if (company.getFormality().getContent().getPersonnePhysique() != null) {
                if (company.getFormality().getContent().getPersonnePhysique().getAutresEtablissements() != null)
                    for (AutresEtablissement other : company.getFormality().getContent().getPersonnePhysique()
                            .getAutresEtablissements()) {
                        if (other.getDescriptionEtablissement() != null
                                && other.getDescriptionEtablissement().getSiret() != null
                                && other.getDescriptionEtablissement().getSiret().equals(siret))
                            return false;
                    }
                if (company.getFormality().getContent().getPersonnePhysique()
                        .getEtablissementPrincipal() != null
                        && siret.equals(company.getFormality().getContent().getPersonnePhysique()
                                .getEtablissementPrincipal().getDescriptionEtablissement().getSiret()))
                    return true;
                return false;
            }
        }
        return false;
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
            if (company.getFormality().getContent().getPersonneMorale() != null) {
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
                    return company.getFormality().getContent().getPersonneMorale().getEtablissementPrincipal()
                            .getAdresse();
                return company.getFormality().getContent().getPersonneMorale().getAdresseEntreprise().getAdresse();
            } else if (company.getFormality().getContent().getPersonnePhysique() != null) {
                if (company.getFormality().getContent().getPersonneMorale().getAutresEtablissements() != null)
                    for (AutresEtablissement other : company.getFormality().getContent().getPersonnePhysique()
                            .getAutresEtablissements()) {
                        if (other.getDescriptionEtablissement() != null
                                && other.getDescriptionEtablissement().getSiret() != null
                                && other.getDescriptionEtablissement().getSiret().equals(siret))
                            return other.getAdresse();
                    }
                if (company.getFormality().getContent().getPersonnePhysique()
                        .getEtablissementPrincipal() != null
                        && siret.equals(company.getFormality().getContent().getPersonnePhysique()
                                .getEtablissementPrincipal().getDescriptionEtablissement().getSiret()))
                    return company.getFormality().getContent().getPersonnePhysique().getEtablissementPrincipal()
                            .getAdresse();
                return company.getFormality().getContent().getPersonnePhysique().getAdresseEntreprise().getAdresse();
            }
        }
        return null;
    }

    private List<String> getSiretsFromRneCompany(RneCompany company, boolean onlyPrincipalEtablissment) {
        HashSet<String> sirets = new HashSet<String>();

        if (company == null || company.getFormality() == null || company.getFormality().getContent() == null
                || company.getFormality().getContent().getPersonneMorale() == null
                        && company.getFormality().getContent().getPersonnePhysique() == null)
            return null;

        if (company.getFormality().getContent().getPersonneMorale() != null
                && company.getFormality().getContent().getPersonneMorale().getAutresEtablissements() != null)
            for (AutresEtablissement other : company.getFormality().getContent().getPersonneMorale()
                    .getAutresEtablissements()) {
                if (other.getDescriptionEtablissement() != null
                        && other.getDescriptionEtablissement().getSiret() != null) {
                    sirets.add(other.getDescriptionEtablissement().getSiret());
                    if (onlyPrincipalEtablissment && Boolean.TRUE
                            .equals(other.getDescriptionEtablissement().getIndicateurEtablissementPrincipal()))
                        return Arrays.asList(other.getDescriptionEtablissement().getSiret());
                }
            }
        if (company.getFormality().getContent().getPersonneMorale() != null
                && company.getFormality().getContent().getPersonneMorale()
                        .getEtablissementPrincipal() != null
                && company.getFormality().getContent().getPersonneMorale()
                        .getEtablissementPrincipal().getDescriptionEtablissement().getSiret() != null) {
            sirets.add(company.getFormality().getContent().getPersonneMorale()
                    .getEtablissementPrincipal().getDescriptionEtablissement().getSiret());
            if (onlyPrincipalEtablissment)
                return Arrays.asList(company.getFormality().getContent().getPersonneMorale()
                        .getEtablissementPrincipal().getDescriptionEtablissement().getSiret());
        }

        if (company.getFormality().getContent().getPersonnePhysique() != null
                && company.getFormality().getContent().getPersonnePhysique().getAutresEtablissements() != null)
            for (AutresEtablissement other : company.getFormality().getContent().getPersonnePhysique()
                    .getAutresEtablissements()) {
                if (other.getDescriptionEtablissement() != null
                        && other.getDescriptionEtablissement().getSiret() != null)
                    sirets.add(other.getDescriptionEtablissement().getSiret());
            }
        if (company.getFormality().getContent().getPersonnePhysique() != null
                && company.getFormality().getContent().getPersonnePhysique()
                        .getEtablissementPrincipal() != null
                && company.getFormality().getContent().getPersonnePhysique()
                        .getEtablissementPrincipal().getDescriptionEtablissement().getSiret() != null)
            sirets.add(company.getFormality().getContent().getPersonnePhysique()
                    .getEtablissementPrincipal().getDescriptionEtablissement().getSiret());

        if (onlyPrincipalEtablissment)
            return null;

        return new ArrayList<String>(sirets);

    }

    private List<Activite> getActivitesFromRneCompany(RneCompany company, String siren, String siret) {
        if (company == null || company.getFormality() == null || company.getFormality().getContent() == null
                || company.getFormality().getContent().getPersonneMorale() == null
                        && company.getFormality().getContent().getPersonnePhysique() == null)
            return null;

        if (siren != null && (siret == null || siret.length() == 0)) {
            if (company.getFormality().getContent().getPersonneMorale() != null)
                if (company.getFormality().getContent().getPersonneMorale().getEtablissementPrincipal() == null) {
                    return null;
                } else {
                    company.getFormality().getContent().getPersonneMorale().getEtablissementPrincipal().getActivites();
                }
        } else if (siret != null) {
            if (company.getFormality().getContent().getPersonneMorale() != null) {
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
            } else if (company.getFormality().getContent().getPersonnePhysique() != null) {
                if (company.getFormality().getContent().getPersonnePhysique().getAutresEtablissements() != null)
                    for (AutresEtablissement other : company.getFormality().getContent().getPersonnePhysique()
                            .getAutresEtablissements()) {
                        if (other.getDescriptionEtablissement() != null
                                && other.getDescriptionEtablissement().getSiret() != null
                                && other.getDescriptionEtablissement().getSiret().equals(siret))
                            return other.getActivites();
                    }
                if (company.getFormality().getContent().getPersonnePhysique()
                        .getEtablissementPrincipal() != null
                        && siret.equals(company.getFormality().getContent().getPersonnePhysique()
                                .getEtablissementPrincipal().getDescriptionEtablissement().getSiret()))
                    return company.getFormality().getContent().getPersonnePhysique().getEtablissementPrincipal()
                            .getActivites();
            }
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
    public List<Affaire> searchAffaireForCorrection() {
        List<Affaire> affaires = affaireRepository.getAffairesForCorrection();
        if (affaires != null)
            for (Affaire affaire : affaires)
                affaire.setCreatedDateTime(
                        auditService.getCreationDateTimeForEntity(Affaire.class.getSimpleName(), affaire.getId()));
        return affaires;
    }

    @Override
    @Transactional
    public void updateNewAffaireFromRne()
            throws OsirisException, OsirisClientMessageException, OsirisDuplicateException {

        // Update and search new ones
        List<Affaire> affaires = affaireRepository.getNextAffaireToUpdate(LocalDate.now());
        List<String> sirets = null;

        for (Affaire affaire : affaires) {
            try {
                if (affaire.getDenomination() != null && affaire.getPostalCode() != null
                        && affaire.getDenomination().length() > 0 && affaire.getPostalCode().length() > 0) {
                    List<RneCompany> results = rneDelegateService
                            .getCompanyByDenominationAndPostalCode(affaire.getDenomination(),
                                    affaire.getPostalCode());

                    if (results != null && results.size() == 1) {
                        RneCompany company = results.get(0);
                        if (company.getSiren() != null) {
                            sirets = getSiretsFromRneCompany(company, true);
                            if (sirets != null && sirets.size() == 1) {
                                Affaire affaireToCheck = getAffaireFromRneCompany(company, sirets.get(0), false);
                                if (affaire.getDenomination().equals(affaireToCheck.getDenomination())
                                        && affaire.getPostalCode().equals(affaireToCheck.getPostalCode())) {
                                    affaireRneUpdateHelper.updateAffaireSiretFromRne(affaire, company.getSiren(),
                                            sirets.get(0));
                                    affaireRneUpdateHelper.updateAffaireFromRne(affaire, company);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new OsirisException(e, "Error when searching for affaire " + affaire.getId()
                        + " in RNE with SIRET " + (sirets != null && sirets.size() > 0 ? sirets.get(0) : ""));
            }
            affaireRneUpdateHelper.updateAffairelastRneCheck(affaire);
        }
    }

    @Override
    @Transactional
    public void updateAffaireFromRne()
            throws OsirisException, OsirisClientMessageException, OsirisDuplicateException {

        // Update existing ones
        List<Affaire> affaires = null;
        List<String> sirets = null;

        List<Affaire> affairesComplete = affaireRepository.getNextAffaireToUpdateForRne(LocalDate.now(),
                LocalDate.now().minusYears(200));

        if (affairesComplete.size() > 100)
            affaires = affairesComplete.subList(0, 100);
        else
            affaires = affairesComplete;

        Set<Affaire> affaireToMarkAsChecked = new HashSet<Affaire>();
        affaireToMarkAsChecked.addAll(affaires);

        LocalDate minusDate = affaires.stream()
                .map(a -> (a.getLastRneUpdate() != null ? a.getLastRneUpdate() : LocalDate.now().minusYears(100)))
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now().minusYears(100));
        RneResult result = rneDelegateService.getCompanyModifiedForDay(minusDate, null,
                affaires.stream().map(a -> a.getSiren()).distinct().toList());

        for (RneCompany company : result.getCompanies()) {
            if (company.getSiren() != null) {
                sirets = getSiretsFromRneCompany(company, false);
                if (sirets != null)
                    for (String siret : sirets) {
                        Optional<Affaire> affaireWithSiret = affairesComplete.stream()
                                .filter(a -> a.getSiret() != null && a.getSiret().equals(siret)).findFirst();
                        if (!affaireWithSiret.isEmpty()) {
                            affaireToMarkAsChecked.add(affaireWithSiret.get());
                            affaireRneUpdateHelper.updateAffaireFromRne(affaireWithSiret.get(), company);
                        }
                    }
            }
        }

        for (Affaire affaire : affaireToMarkAsChecked)
            affaireRneUpdateHelper.updateAffairelastRneCheck(affaire);
        affairesComplete = affaireRepository.getNextAffaireToUpdateForRne(LocalDate.now(),
                LocalDate.now().minusYears(100));
    }

    @Override
    public List<Affaire> getAffairesForCurrentUser(List<Integer> responsableIdToFilter, Integer page, String sortBy,
            String searchText) {

        Responsable currentUser = employeeService.getCurrentMyJssUser();
        if (currentUser != null) {
            List<Responsable> responsablesToFilter = new ArrayList<Responsable>();

            if (responsableIdToFilter == null || responsableIdToFilter.contains(currentUser.getId()))
                responsablesToFilter.add(currentUser);

            if (Boolean.TRUE.equals(currentUser.getCanViewAllTiersInWeb())
                    && currentUser.getTiers().getResponsables() != null)
                for (Responsable respo : currentUser.getTiers().getResponsables()) {
                    if (responsableIdToFilter == null || responsableIdToFilter.contains(respo.getId()))
                        responsablesToFilter.add(respo);
                }

            if (responsablesToFilter == null || responsablesToFilter.size() == 0)
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
            return affaireRepository.getAffairesForResponsables(pageableRequest, responsablesToFilter, searchText,
                    idAffaire);
        }
        return new ArrayList<>();
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

    @Override
    public Affaire getAffaireFromResponsable(Responsable responsable)
            throws OsirisDuplicateException, OsirisException {
        Affaire affaire = affaireRepository.findByFirstnameAndLastname(responsable.getFirstname(),
                responsable.getLastname());
        if (affaire != null)
            return affaire;

        return addOrUpdateAffaire(createAffaireWithResponsable(responsable));
    }

    private Affaire createAffaireWithResponsable(Responsable responsable) {
        Affaire affaire = new Affaire();
        affaire.setFirstname(responsable.getFirstname());
        affaire.setLastname(responsable.getLastname());
        affaire.setIsIndividual(true);
        affaire.setCivility(responsable.getCivility());
        if (responsable.getCity() != null) {
            affaire.setAddress(responsable.getAddress());
            affaire.setPostalCode(responsable.getPostalCode());
            affaire.setCity(responsable.getCity());
            affaire.setCountry(responsable.getCountry());
        } else {
            affaire.setAddress(responsable.getTiers().getAddress());
            affaire.setPostalCode(responsable.getTiers().getPostalCode());
            affaire.setCity(responsable.getTiers().getCity());
            affaire.setCountry(responsable.getTiers().getCountry());
        }
        List<Mail> mails = new ArrayList<>();
        if (responsable.getMails() != null) {
            for (Mail mail : responsable.getMails()) {
                mails.add(mail);
            }
        }
        affaire.setMails(mails);
        List<Phone> phones = new ArrayList<>();
        if (responsable.getPhones() != null) {
            for (Phone phone : responsable.getPhones()) {
                phones.add(phone);
            }
        }
        affaire.setPhones(phones);
        return affaire;
    }

}
