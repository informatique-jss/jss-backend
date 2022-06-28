package com.jss.osiris.modules.profile.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.service.MailService;
import com.jss.osiris.modules.profile.model.Team;
import com.jss.osiris.modules.profile.repository.TeamRepository;

@Service
public class TeamServiceImpl implements TeamService {

    @Autowired
    TeamRepository teamRepository;

    @Autowired
    MailService mailService;

    @Override
    public List<Team> getTeams() {
        return IterableUtils.toList(teamRepository.findAll());
    }

    @Override
    public Team getTeam(Integer id) {
        Optional<Team> team = teamRepository.findById(id);
        if (!team.isEmpty())
            return team.get();
        return null;
    }

    @Override
    public Team addOrUpdateTeam(
            Team team) {
        // If mails already exists, get their ids
        if (team != null && team.getMails() != null && team.getMails().size() > 0)
            mailService.populateMailIds(team.getMails());
        return teamRepository.save(team);
    }

    @Override
    public Team getTeamIdByCode(String code) {
        return teamRepository.findByCode(code);
    }
}
