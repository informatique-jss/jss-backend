package com.jss.osiris.libs.mail.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.libs.mail.model.CustomerMail;

public interface CustomerMailRepository extends CrudRepository<CustomerMail, Integer> {

    List<CustomerMail> findAllByOrderByCreatedDateTimeAsc();
}