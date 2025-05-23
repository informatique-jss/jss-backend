package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.Mail;

public interface MailService {
        public List<Mail> findMails(String mail);

        public Mail getMail(Integer id);

        public List<Mail> populateMailIds(List<Mail> mails);

        public Mail populateMailId(Mail mail);

}
