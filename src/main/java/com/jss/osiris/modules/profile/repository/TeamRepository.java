package com.jss.osiris.modules.profile.repository;

import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.profile.model.Team;

public interface TeamRepository extends CrudRepository<Team, Integer> {

    Team findByCode(String code);
}