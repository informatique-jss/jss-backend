package com.jss.jssbackend.modules.miscellaneous.repository;

import com.jss.jssbackend.modules.miscellaneous.model.Gift;

import org.springframework.data.repository.CrudRepository;

public interface GiftRepository extends CrudRepository<Gift, Integer> {
}