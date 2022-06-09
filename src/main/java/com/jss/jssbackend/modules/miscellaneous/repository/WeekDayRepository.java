package com.jss.jssbackend.modules.miscellaneous.repository;

import com.jss.jssbackend.modules.miscellaneous.model.WeekDay;

import org.springframework.data.repository.CrudRepository;

public interface WeekDayRepository extends CrudRepository<WeekDay, Integer> {
}