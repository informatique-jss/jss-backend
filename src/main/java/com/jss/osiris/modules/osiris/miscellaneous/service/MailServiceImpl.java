package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.mail.MailHelper;
import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;
import com.jss.osiris.modules.osiris.miscellaneous.repository.MailRepository;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    MailRepository mailRepository;

    @Autowired
    MailHelper mailHelper;

    @Override
    public List<Mail> findMails(String mail) {
        if (mail != null)
            return mailRepository.findByMailContainingIgnoreCase(mail);
        return null;
    }

    @Override
    public Mail getMail(Integer id) {
        Optional<Mail> mail = mailRepository.findById(id);
        if (mail.isPresent())
            return mail.get();
        return null;
    }

    @Override
    public List<Mail> populateMailIds(List<Mail> mails) {
        if (mails != null)
            for (Mail mail : mails) {
                if (mail.getId() == null) {
                    List<Mail> existingMails = findMails(mail.getMail());
                    if (existingMails != null && existingMails.size() == 1) {
                        mail.setId(existingMails.get(0).getId());
                    }
                    mailRepository.save(mail);
                }
            }
        return mails;
    }

    @Override
    public Mail populateMailId(Mail mail) {
        if (mail != null)
            if (mail.getId() == null) {
                List<Mail> existingMails = findMails(mail.getMail());
                if (existingMails != null && existingMails.size() >= 1) {
                    mail.setId(existingMails.get(0).getId());
                }
                mailRepository.save(mail);
            }
        return mail;
    }
}
