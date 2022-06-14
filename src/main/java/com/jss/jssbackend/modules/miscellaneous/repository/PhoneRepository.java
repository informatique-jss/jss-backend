package com.jss.jssbackend.modules.miscellaneous.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.jss.jssbackend.modules.miscellaneous.model.Phone;

public interface PhoneRepository extends CrudRepository<Phone, Integer> {

    List<Phone> findByPhoneNumberContainingIgnoreCase(String phone);
}