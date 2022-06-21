package com.jss.osiris.modules.profile.service;

import com.jss.osiris.modules.profile.model.Team;

public interface TeamService {
    public Team getTeam(Integer id);

    public Team getTeamIdByCode(String code);
}
