package com.jss.osiris.modules.profile.service;

import java.util.List;

import com.jss.osiris.modules.profile.model.Team;

public interface TeamService {
    public List<Team> getTeams();

    public Team getTeam(Integer id);

    public Team addOrUpdateTeam(Team team);

    public Team getTeamIdByCode(String code);
}
