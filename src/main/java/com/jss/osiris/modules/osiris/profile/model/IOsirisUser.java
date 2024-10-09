package com.jss.osiris.modules.osiris.profile.model;

import com.fasterxml.jackson.annotation.JsonView;
import com.jss.osiris.libs.jackson.JacksonViews;

public interface IOsirisUser {
    public Integer getId();

    public String getUsername();

    public String getFirstname();

    public String getLastname();

    public String getEmail();

    @JsonView(JacksonViews.MyJssView.class)
    public void setId(Integer id);

    @JsonView(JacksonViews.MyJssView.class)
    public void setUsername(String username);

    @JsonView(JacksonViews.MyJssView.class)
    public void setFirstname(String firstname);

    @JsonView(JacksonViews.MyJssView.class)
    public void setLastname(String lastname);

    @JsonView(JacksonViews.MyJssView.class)
    public void setEmail(String mail);

}
