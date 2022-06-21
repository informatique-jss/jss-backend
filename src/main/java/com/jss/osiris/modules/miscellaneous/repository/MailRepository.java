package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.Mail;

public interface MailRepository extends CrudRepository<Mail, Integer> {

    List<Mail> findByMailContainingIgnoreCase(String mail);
}