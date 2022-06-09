package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.Mail;
import com.jss.jssbackend.modules.tiers.repository.MailRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
