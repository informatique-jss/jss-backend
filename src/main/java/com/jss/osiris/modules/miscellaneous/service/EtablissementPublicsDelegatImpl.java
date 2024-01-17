package com.jss.osiris.modules.miscellaneous.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.City;
import com.jss.osiris.modules.miscellaneous.model.CompetentAuthority;
import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.EtablissementsPublics.Feature;
import com.jss.osiris.modules.miscellaneous.model.EtablissementsPublics.GeoCity;
import com.jss.osiris.modules.miscellaneous.model.EtablissementsPublics.Horaire;
import com.jss.osiris.modules.miscellaneous.model.EtablissementsPublics.Organisme;

@Service
public class EtablissementPublicsDelegatImpl implements EtablissementPublicsDelegate {
    @Value("${fr.gouv.etablissements.publics.api.entry.point}")
    private String etablissementPublicEntryPoint;

    @Value("${fr.gouv.geo.api.entry.point}")
    private String geoApiEntryPoint;

    private String rcsUrl = "/tribunal_commerce";
    private String cfpUrl = "/sie";
    private String cciUrl = "/cci";
    private String direccteUrl = "/direccte";
    private String chambreMetierUrl = "/chambre_metier";
    private String chambreAgricultureUrl = "/chambre_agriculture";
    private String urssafUrl = "/urssaf";
    private String prefectureUrl = "/prefecture";
    private String spfeUrl = "/hypotheque";

    @Autowired
    CompetentAuthorityService competentAuthorityService;

    @Autowired
    ConstantService constantService;

    @Autowired
    MailService mailService;

    @Autowired
    PhoneService phoneService;

    @Autowired
    CityService cityService;

    @Autowired
    BatchService batchService;

    private List<GeoCity> geoCities = new ArrayList<GeoCity>();
    private List<City> localCities = new ArrayList<City>();

    @Override
    @Transactional
    public void updateCompetentAuthorities() throws OsirisException {
        updateRcs();
        updateCfp();
        updateCci();
        updateChambreMetier();
        updateChambreAgriculture();
        updateUrssaf();
        updateDireccte();
        updatePrefecture();
        updateSpfe();
        geoCities = new ArrayList<GeoCity>();
        localCities = new ArrayList<City>();
    }

    private CompetentAuthority mergeCompetentAuthorityWithCityZonage(CompetentAuthority competentAuthority,
            Feature organisme) throws OsirisException {
        competentAuthority.setApiId(organisme.getProperties().getId());

        if (organisme.getProperties().getAdresses() != null
                && organisme.getProperties().getAdresses().size() > 0) {
            if (organisme.getProperties().getAdresses().get(0).getLignes() != null)
                competentAuthority.setAddress(
                        String.join("\n", organisme.getProperties().getAdresses().get(0).getLignes()));

            List<City> foundCities = getCitiesFromInseeCodeList(
                    Arrays.asList(organisme.getProperties().getCodeInsee()));
            if (foundCities != null)
                if (foundCities.size() == 1) {
                    competentAuthority.setCity(foundCities.get(0));
                } else if (foundCities.size() > 1) {
                    for (City foundCity : foundCities)
                        if (organisme.getProperties().getAdresses() != null
                                && organisme.getProperties().getAdresses().get(0).getCommune().toUpperCase()
                                        .contains(foundCity.getLabel().toUpperCase()))
                            competentAuthority.setCity(foundCity);
                }

            if (competentAuthority.getCity() != null
                    && organisme.getProperties().getAdresses().get(0).getCommune() != null)
                competentAuthority.setCedexComplement(
                        organisme.getProperties().getAdresses().get(0).getCommune().toUpperCase()
                                .replace(competentAuthority.getCity().getLabel().toUpperCase(), "")
                                .trim());

            competentAuthority
                    .setPostalCode(organisme.getProperties().getAdresses().get(0).getCodePostal());
        }

        if (organisme.getProperties().getZonage() != null
                && organisme.getProperties().getZonage().getCommunes() != null
                && organisme.getProperties().getZonage().getCommunes().size() > 0) {
            competentAuthority
                    .setCode(organisme.getProperties().getZonage().getCommunes().get(0).substring(0, 1));
            competentAuthority
                    .setCities(getCitiesFromInseeCodeList(organisme.getProperties().getZonage().getCommunes()));
        }

        competentAuthority.setCountry(constantService.getCountryFrance());
        competentAuthority.setLabel(organisme.getProperties().getNom());
        if (competentAuthority.getLabel() != null)
            competentAuthority.setLabel(StringUtils.stripAccents(competentAuthority.getLabel()).toUpperCase());

        if (organisme.getProperties().getEmail() != null
                && !isMailInList(organisme.getProperties().getEmail(), competentAuthority.getMails())) {
            Mail mail = new Mail();
            mail.setMail(organisme.getProperties().getEmail());
            ArrayList<Mail> mails = new ArrayList<Mail>();
            mails.add(mail);
            competentAuthority.setMails(mailService.populateMailIds(mails));
            ;
        }

        if (organisme.getProperties().getTelephone() != null
                && !isPhoneInList(organisme.getProperties().getTelephone(),
                        competentAuthority.getPhones())) {
            Phone phone = new Phone();
            phone.setPhoneNumber(organisme.getProperties().getTelephone().replaceAll(" ", ""));
            ArrayList<Phone> phones = new ArrayList<>();
            phones.add(phone);
            competentAuthority.setPhones(phoneService.populatePhoneIds(phones));
        }

        if (competentAuthority.getSchedulle() == null && organisme.getProperties().getHoraires() != null
                && organisme.getProperties().getHoraires().size() > 0) {
            List<String> schedulles = new ArrayList<String>();
            for (Horaire horaire : organisme.getProperties().getHoraires())
                schedulles.add("Du " + horaire.getDu() + " au " + horaire.getAu() + " de "
                        + horaire.getHeures().get(0).getDe() + " à " + horaire.getHeures().get(0).getA());
            competentAuthority.setSchedulle(String.join(" / ", schedulles));
        }
        return competentAuthority;
    }

    @SuppressWarnings({ "null" })
    private void updateRcs() throws OsirisException {
        ResponseEntity<Organisme> response = new RestTemplate().getForEntity(etablissementPublicEntryPoint + rcsUrl,
                Organisme.class);
        if (response.getBody() != null && response.getBody().getFeatures() != null
                && response.getBody().getFeatures().size() > 0
                && response.getBody().getFeatures().get(0) != null)
            for (Feature organisme : response.getBody().getFeatures().get(0)) {
                CompetentAuthority competentAuthority = null;
                competentAuthority = competentAuthorityService
                        .getCompetentAuthorityByApiId(organisme.getProperties().getId());
                if (competentAuthority == null)
                    competentAuthority = new CompetentAuthority();

                competentAuthority.setCompetentAuthorityType(constantService.getCompetentAuthorityTypeRcs());
                competentAuthority = mergeCompetentAuthorityWithCityZonage(competentAuthority, organisme);
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthority);
            }
    }

    @SuppressWarnings({ "null" })
    private void updateCfp() throws OsirisException {
        ResponseEntity<Organisme> response = new RestTemplate().getForEntity(etablissementPublicEntryPoint + cfpUrl,
                Organisme.class);
        if (response.getBody() != null && response.getBody().getFeatures() != null
                && response.getBody().getFeatures().size() > 0
                && response.getBody().getFeatures().get(0) != null)
            for (Feature organisme : response.getBody().getFeatures().get(0)) {
                CompetentAuthority competentAuthority = null;
                competentAuthority = competentAuthorityService
                        .getCompetentAuthorityByApiId(organisme.getProperties().getId());
                if (competentAuthority == null)
                    competentAuthority = new CompetentAuthority();

                competentAuthority.setCompetentAuthorityType(constantService.getCompetentAuthorityTypeCfp());
                competentAuthority = mergeCompetentAuthorityWithCityZonage(competentAuthority, organisme);
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthority);
            }
    }

    @SuppressWarnings({ "null" })
    private void updateCci() throws OsirisException {
        ResponseEntity<Organisme> response = new RestTemplate().getForEntity(etablissementPublicEntryPoint + cciUrl,
                Organisme.class);
        if (response.getBody() != null && response.getBody().getFeatures() != null
                && response.getBody().getFeatures().size() > 0
                && response.getBody().getFeatures().get(0) != null)
            for (Feature organisme : response.getBody().getFeatures().get(0)) {
                CompetentAuthority competentAuthority = null;
                competentAuthority = competentAuthorityService
                        .getCompetentAuthorityByApiId(organisme.getProperties().getId());
                if (competentAuthority == null)
                    competentAuthority = new CompetentAuthority();

                competentAuthority.setCompetentAuthorityType(constantService.getCompetentAuthorityTypeCci());
                competentAuthority = mergeCompetentAuthorityWithCityZonage(competentAuthority, organisme);
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthority);
            }
    }

    @SuppressWarnings({ "null" })
    private void updateChambreMetier() throws OsirisException {
        ResponseEntity<Organisme> response = new RestTemplate().getForEntity(
                etablissementPublicEntryPoint + chambreMetierUrl,
                Organisme.class);
        if (response.getBody() != null && response.getBody().getFeatures() != null
                && response.getBody().getFeatures().size() > 0
                && response.getBody().getFeatures().get(0) != null)
            for (Feature organisme : response.getBody().getFeatures().get(0)) {
                CompetentAuthority competentAuthority = null;
                competentAuthority = competentAuthorityService
                        .getCompetentAuthorityByApiId(organisme.getProperties().getId());
                if (competentAuthority == null)
                    competentAuthority = new CompetentAuthority();

                competentAuthority.setCompetentAuthorityType(constantService.getCompetentAuthorityTypeChambreMetier());
                competentAuthority = mergeCompetentAuthorityWithCityZonage(competentAuthority, organisme);
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthority);
            }
    }

    @SuppressWarnings({ "null" })
    private void updateChambreAgriculture() throws OsirisException {
        ResponseEntity<Organisme> response = new RestTemplate().getForEntity(
                etablissementPublicEntryPoint + chambreAgricultureUrl,
                Organisme.class);
        if (response.getBody() != null && response.getBody().getFeatures() != null
                && response.getBody().getFeatures().size() > 0
                && response.getBody().getFeatures().get(0) != null)
            for (Feature organisme : response.getBody().getFeatures().get(0)) {
                CompetentAuthority competentAuthority = null;
                competentAuthority = competentAuthorityService
                        .getCompetentAuthorityByApiId(organisme.getProperties().getId());
                if (competentAuthority == null)
                    competentAuthority = new CompetentAuthority();

                competentAuthority
                        .setCompetentAuthorityType(constantService.getCompetentAuthorityTypeChambreAgriculture());
                competentAuthority = mergeCompetentAuthorityWithCityZonage(competentAuthority, organisme);
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthority);
            }
    }

    @SuppressWarnings({ "null" })
    private void updateUrssaf() throws OsirisException {
        ResponseEntity<Organisme> response = new RestTemplate().getForEntity(
                etablissementPublicEntryPoint + urssafUrl,
                Organisme.class);
        if (response.getBody() != null && response.getBody().getFeatures() != null
                && response.getBody().getFeatures().size() > 0
                && response.getBody().getFeatures().get(0) != null)
            for (Feature organisme : response.getBody().getFeatures().get(0)) {
                CompetentAuthority competentAuthority = null;
                competentAuthority = competentAuthorityService
                        .getCompetentAuthorityByApiId(organisme.getProperties().getId());
                if (competentAuthority == null)
                    competentAuthority = new CompetentAuthority();

                competentAuthority.setCompetentAuthorityType(constantService.getCompetentAuthorityTypeUrssaf());
                competentAuthority = mergeCompetentAuthorityWithCityZonage(competentAuthority, organisme);
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthority);
            }
    }

    @SuppressWarnings({ "null" })
    private void updateDireccte() throws OsirisException {
        ResponseEntity<Organisme> response = new RestTemplate().getForEntity(
                etablissementPublicEntryPoint + direccteUrl,
                Organisme.class);
        if (response.getBody() != null && response.getBody().getFeatures() != null
                && response.getBody().getFeatures().size() > 0
                && response.getBody().getFeatures().get(0) != null)
            for (Feature organisme : response.getBody().getFeatures().get(0)) {
                CompetentAuthority competentAuthority = null;
                competentAuthority = competentAuthorityService
                        .getCompetentAuthorityByApiId(organisme.getProperties().getId());
                if (competentAuthority == null)
                    competentAuthority = new CompetentAuthority();

                competentAuthority.setCompetentAuthorityType(constantService.getCompetentAuthorityTypeDireccte());
                competentAuthority = mergeCompetentAuthorityWithCityZonage(competentAuthority, organisme);
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthority);
            }
    }

    @SuppressWarnings({ "null" })
    private void updatePrefecture() throws OsirisException {
        ResponseEntity<Organisme> response = new RestTemplate().getForEntity(
                etablissementPublicEntryPoint + prefectureUrl,
                Organisme.class);
        if (response.getBody() != null && response.getBody().getFeatures() != null
                && response.getBody().getFeatures().size() > 0
                && response.getBody().getFeatures().get(0) != null)
            for (Feature organisme : response.getBody().getFeatures().get(0)) {
                CompetentAuthority competentAuthority = null;
                competentAuthority = competentAuthorityService
                        .getCompetentAuthorityByApiId(organisme.getProperties().getId());
                if (competentAuthority == null)
                    competentAuthority = new CompetentAuthority();

                competentAuthority.setCompetentAuthorityType(constantService.getCompetentAuthorityTypePrefecture());
                competentAuthority = mergeCompetentAuthorityWithCityZonage(competentAuthority, organisme);
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthority);
            }
    }

    @SuppressWarnings({ "null" })
    private void updateSpfe() throws OsirisException {
        ResponseEntity<Organisme> response = new RestTemplate().getForEntity(
                etablissementPublicEntryPoint + spfeUrl,
                Organisme.class);
        if (response.getBody() != null && response.getBody().getFeatures() != null
                && response.getBody().getFeatures().size() > 0
                && response.getBody().getFeatures().get(0) != null)
            for (Feature organisme : response.getBody().getFeatures().get(0)) {
                CompetentAuthority competentAuthority = null;
                competentAuthority = competentAuthorityService
                        .getCompetentAuthorityByApiId(organisme.getProperties().getId());
                if (competentAuthority == null)
                    competentAuthority = new CompetentAuthority();

                competentAuthority.setCompetentAuthorityType(constantService.getCompetentAuthorityTypeSpfe());
                competentAuthority = mergeCompetentAuthorityWithCityZonage(competentAuthority, organisme);
                competentAuthorityService.addOrUpdateCompetentAuthority(competentAuthority);
            }
    }

    private List<City> getCitiesFromInseeCodeList(List<String> inseeCodeList) throws OsirisException {
        if (geoCities == null || geoCities.size() == 0)
            fetchGeoCities();
        if (localCities == null || localCities.size() == 0)
            fetchLocalCities();

        List<City> outCities = new ArrayList<City>();
        List<GeoCity> tempGeoCities = new ArrayList<GeoCity>();
        List<String> cleanInseeCodeList = new ArrayList<String>();
        if (inseeCodeList != null && inseeCodeList.size() > 0) {
            // Clear code
            for (String inseeCode : inseeCodeList) {
                if (inseeCode.contains(" "))
                    inseeCode = inseeCode.split(" ")[0];
                cleanInseeCodeList.add(inseeCode.trim());
            }

            // Do subset of geo cities
            for (GeoCity geoCity : geoCities) {
                for (String inseeCode : cleanInseeCodeList) {
                    if (inseeCode.equals(geoCity.getCode())) {
                        tempGeoCities.add(geoCity);
                        continue;
                    }
                }
            }

            // Search corresponding city
            loopCity: for (City localCity : localCities) {
                for (GeoCity geoCity : tempGeoCities) {
                    if (geoCity.getCodesPostaux() != null) {
                        for (String postalCode : geoCity.getCodesPostaux()) {
                            if (postalCode.equals(localCity.getPostalCode())) {
                                outCities.add(localCity);
                                continue loopCity;
                            }
                        }
                    }
                }
            }
        }

        return outCities;
    }

    private void fetchGeoCities() {
        ResponseEntity<GeoCity[]> response = new RestTemplate().getForEntity(
                geoApiEntryPoint + "?fields=codesPostaux",
                GeoCity[].class);
        if (response != null && response.getBody() != null) {
            geoCities = Arrays.asList(response.getBody());
        }
    }

    private void fetchLocalCities() {
        localCities = cityService.getCities();
    }

    private boolean isPhoneInList(String phoneIn, List<Phone> phoneList) {
        phoneIn = phoneIn.replaceAll(" ", "");
        if (phoneList != null && phoneList.size() > 0)
            for (Phone phone : phoneList)
                if (phone.getPhoneNumber().equals(phoneIn))
                    return true;
        return false;
    }

    private boolean isMailInList(String mailIn, List<Mail> mailList) {
        mailIn = mailIn.replaceAll(" ", "");
        if (mailList != null && mailList.size() > 0)
            for (Mail mail : mailList)
                if (mail.getMail().equals(mailIn))
                    return true;
        return false;
    }
}
