package com.jss.jssbackend.modules.tiers.repository;

import java.util.List;

import com.jss.jssbackend.modules.tiers.model.Phone;

import org.springframework.data.repository.CrudRepository;

public interface PhoneRepository extends CrudRepository<Phone, Integer> {

    List<Phone> findByPhoneNumberContainingIgnoreCase(String phone);
}