package com.jss.osiris.libs.mail.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.libs.mail.model.CustomerMail;

public interface CustomerMailRepository extends CrudRepository<CustomerMail, Integer> {

    @Query("select m from CustomerMail m where hasErrors=false order by createdDateTime asc")
    List<CustomerMail> findAllByOrderByCreatedDateTimeAsc();
}