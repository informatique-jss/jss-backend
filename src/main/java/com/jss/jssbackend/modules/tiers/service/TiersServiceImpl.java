package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.Mail;
import com.jss.jssbackend.modules.tiers.model.Phone;
import com.jss.jssbackend.modules.tiers.model.Tiers;
import com.jss.jssbackend.modules.tiers.repository.TiersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiersServiceImpl implements TiersService {

    @Autowired
    TiersRepository tiersRepository;

    @Autowired
    MailService mailService;

    @Autowired
    PhoneService phoneService;

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
            for (Mail mail : tiers.getMails()) {
                if (mail.getId() == null) {
                    List<Mail> existingMails = mailService.findMails(mail.getMail());
                    if (existingMails != null && existingMails.size() == 1)
                        mail.setId(existingMails.get(0).getId());
                }
            }

        // If phones already exists, get their ids
        if (tiers != null && tiers.getPhones() != null && tiers.getPhones().size() > 0)
            for (Phone phone : tiers.getPhones()) {
                if (phone.getId() == null) {
                    List<Phone> existingPhones = phoneService.findPhones(phone.getPhoneNumber());
                    if (existingPhones != null && existingPhones.size() == 1)
                        phone.setId(existingPhones.get(0).getId());
                }
            }
        return tiersRepository.save(tiers);
    }
}
