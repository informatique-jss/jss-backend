package com.jss.osiris.libs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.jss.osiris.libs.exception.OsirisDuplicateException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.service.ConstantService;
import com.jss.osiris.modules.osiris.miscellaneous.service.MailService;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;
import com.jss.osiris.modules.osiris.tiers.model.Subscriber;
import com.jss.osiris.modules.osiris.tiers.model.Tiers;
import com.jss.osiris.modules.osiris.tiers.repository.TiersRepository;
import com.jss.osiris.modules.osiris.tiers.service.ResponsableService;
import com.jss.osiris.modules.osiris.tiers.service.TiersService;

@Service
public class GenerateResponsableForSubscribersHelper {

    @Autowired
    TiersService tiersService;

    @Autowired
    ResponsableService responsableService;

    @Autowired
    ConstantService constantService;

    @Autowired
    MailService mailService;

    @Autowired
    TiersRepository tiersRepository;

    @Autowired
    ValidationHelper validationHelper;

    public List<Subscriber> parseCsv() throws Exception {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';');

        File csvFile = new File("C:/Users/coanet/Downloads/abonnes_import.csv");

        try (MappingIterator<Subscriber> it = mapper.readerFor(Subscriber.class)
                .with(schema)
                .readValues(csvFile)) {
            List<Subscriber> result = it.readAll();
            return result;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void generateFromSubscribers(List<Subscriber> subscribers)
            throws OsirisDuplicateException, OsirisException {
        if (subscribers != null && subscribers.size() > 0)
            for (Subscriber subscriber : subscribers)
                createTiersFromSubscriber(subscriber);

        System.out.println("DONE");
    }

    private Tiers createTiersFromSubscriber(Subscriber subscriber) throws OsirisDuplicateException, OsirisException {
        if (subscriber != null) {
            Tiers tiers = new Tiers();
            tiers.setTiersType(constantService.getTiersTypeClient());
            tiers.setIsIndividual(false);
            tiers.setIsProvisionalPaymentMandatory(false);
            tiers.setIsSepaMandateReceived(false);

            if (subscriber.getAdresse() != null && subscriber.getAdresse().length() > 0)
                tiers.setAddress(subscriber.getAdresse());
            if (subscriber.getSocieteDenomination() != null && subscriber.getSocieteDenomination().length() > 0)
                tiers.setDenomination(subscriber.getSocieteDenomination());
            if (subscriber.getPostalCode() != null && subscriber.getPostalCode().length() > 0)
                tiers.setPostalCode(subscriber.getPostalCode());

            if (tiers.getDenomination() == null) {
                tiers.setIsIndividual(true);
                tiers.setLastname(subscriber.getNom());
                tiers.setFirstname(subscriber.getPrenom());
            }

            if (subscriber.getMail() != null && validationHelper.validateMail(subscriber.getMail())) {
                Mail mail = new Mail();
                mail.setMail(subscriber.getMail());
                tiers.setMails(new ArrayList<>());
                tiers.getMails().add(mailService.populateMailId(mail));
            }

            List<Tiers> tiersDuplicates = new ArrayList<Tiers>();
            if (tiers.getIsIndividual() != null && tiers.getIsIndividual() == true)
                tiersDuplicates = tiersRepository.findByPostalCodeAndName(tiers.getPostalCode(), tiers.getFirstname(),
                        tiers.getLastname());
            else
                tiersDuplicates = tiersRepository.findByPostalCodeAndDenomination(tiers.getPostalCode(),
                        tiers.getDenomination());

            if (tiersDuplicates.size() == 0)
                tiers = tiersService.addOrUpdateTiers(tiers);
            else
                tiers = tiersDuplicates.get(0);

            Responsable responsable = createResponsableForSubscribers(subscriber);
            if (responsable != null) {
                if (tiers.getResponsables() == null)
                    tiers.setResponsables(new ArrayList<>());
                tiers.getResponsables().add(responsable);

                if (tiers.getMails() != null && tiers.getMails().size() > 0)
                    responsable.setMail(tiers.getMails().get(0));
                responsable.setTiers(tiers);
                responsableService.addOrUpdateResponsable(responsable);
            }
            System.out.println(subscriber.getNumSubscription());
            return tiersService.addOrUpdateTiers(tiers);
        }
        return null;
    }

    public Responsable createResponsableForSubscribers(Subscriber subscriber) {
        Responsable responsable = new Responsable();

        if (subscriber.getNom() != null && subscriber.getNom().length() > 0)
            responsable.setLastname(subscriber.getNom());
        if (subscriber.getPrenom() != null && subscriber.getPrenom().length() > 0)
            responsable.setFirstname(subscriber.getPrenom());
        if (subscriber.getPostalCode() != null && subscriber.getPostalCode().length() > 0)
            responsable.setPostalCode(subscriber.getPostalCode());

        if (subscriber.getMail() != null && validationHelper.validateMail(subscriber.getMail())) {
            mailService.findMails(subscriber.getMail());
            responsable.setMail(mailService.findMails(subscriber.getMail()).get(0));
        }
        responsable.setIsActive(true);
        responsable.setIsBouclette(false);
        responsable.setSubscriberNumber(Integer.parseInt(subscriber.getNumSubscription().trim()));
        return responsable;
    }
}
