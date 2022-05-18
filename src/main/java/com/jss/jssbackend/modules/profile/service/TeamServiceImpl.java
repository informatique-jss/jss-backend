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
        Optional<Team> employee = teamRepository.findById(id);
        if (!employee.isEmpty())
            return employee.get();
        return null;
    }

}
