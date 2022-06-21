package com.jss.osiris.modules.miscellaneous.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.miscellaneous.model.WeekDay;

public interface WeekDayRepository extends CrudRepository<WeekDay, Integer> {
}