package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.Mail;
import com.jss.osiris.modules.miscellaneous.repository.MailRepository;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    MailRepository mailRepository;

    @Override
    public List<Mail> findMails(String mail) {
        return IterableUtils.toList(mailRepository.findByMailContainingIgnoreCase(mail));
    }

    @Override
    public Mail getMail(Integer id) {
        Optional<Mail> mail = mailRepository.findById(id);
        if (!mail.isEmpty())
            return mail.get();
        return null;
    }

    @Override
    public void populateMailIds(List<Mail> mails) {
        for (Mail mail : mails) {
            if (mail.getId() == null) {
                List<Mail> existingMails = findMails(mail.getMail());
                if (existingMails != null && existingMails.size() == 1)
                    mail.setId(existingMails.get(0).getId());
            }
        }
    }
}
