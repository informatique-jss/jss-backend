package com.jss.osiris.modules.miscellaneous.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;

import com.jss.osiris.modules.miscellaneous.model.Phone;

public interface PhoneRepository extends QueryCacheCrudRepository<Phone, Integer> {

    List<Phone> findByPhoneNumberContainingIgnoreCase(String phone);
}