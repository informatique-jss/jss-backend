package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Mail;

public interface MailService {
    public List<Mail> findMails(String mail);

    public Mail getMail(Integer id);

    public void populateMailIds(List<Mail> mails);
}
