package com.jss.osiris.modules.osiris.profile.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = IOsirisUser.class)
public interface IOsirisUser {
    public Integer getId();

    public String getUsername();

    public String getFirstname();

    public String getLastname();

}
