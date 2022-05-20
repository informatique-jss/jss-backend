package com.jss.jssbackend.modules.tiers.repository;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.Mail;

import org.springframework.data.repository.CrudRepository;

public interface MailRepository extends CrudRepository<Mail, Integer> {

    List<Mail> findByMailContainingIgnoreCase(String mail);
}