package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;

import com.jss.osiris.modules.miscellaneous.model.Mail;

public interface MailRepository extends QueryCacheCrudRepository<Mail, Integer> {

    List<Mail> findByMailContainingIgnoreCase(String mail);
}