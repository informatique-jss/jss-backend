package com.jss.jssbackend.modules.profile.service;

import java.util.Optional;

import com.jss.jssbackend.modules.profile.model.Team;
import com.jss.jssbackend.modules.profile.repository.TeamRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    TeamRepository teamRepository;

    @Override
    public Team getTeamById(Integer id) {
        Optional<Team> team = teamRepository.findById(id);
        if (!team.isEmpty())
            return team.get();
        return null;
    }

    @Override
    public Team getTeamIdByCode(String code) {
        return teamRepository.findByCode(code);
    }
}
