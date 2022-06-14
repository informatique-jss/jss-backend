package com.jss.jssbackend.modules.profile.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.jssbackend.modules.profile.model.Team;
import com.jss.jssbackend.modules.profile.repository.TeamRepository;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    TeamRepository teamRepository;

    @Override
    public Team getTeam(Integer id) {
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
