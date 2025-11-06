package com.jss.osiris.modules.osiris.crm.service;

import java.util.List;

import com.jss.osiris.modules.osiris.crm.model.Webinar;

public interface WebinarService {

    public List<Webinar> getWebinars();

    public Webinar getWebinar(Integer id);

    public Webinar addOrUpdateWebinar(Webinar webinar);

    public Webinar getLastWebinar();

    public Webinar getNextWebinar();
}
