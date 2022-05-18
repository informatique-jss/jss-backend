package com.jss.jssbackend.modules.profile.repository;

import com.jss.jssbackend.modules.profile.model.Team;

import org.springframework.data.repository.CrudRepository;

public interface TeamRepository extends CrudRepository<Team, Integer> {
}