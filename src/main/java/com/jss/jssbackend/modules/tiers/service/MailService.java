package com.jss.jssbackend.modules.tiers.service;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.Mail;

public interface MailService {
    public List<Mail> findMails(String mail);

    public Mail getMail(Integer id);
}
