package com.jss.jssbackend.modules.profile.service;

import com.jss.jssbackend.modules.profile.model.Team;

public interface TeamService {
    public Team getTeam(Integer id);

    public Team getTeamIdByCode(String code);
}
