package com.jss.osiris.modules.osiris.quotation.service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.ActuLegaleAnnouncement;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.Announcement;

public interface ActuLegaleDelegate {
    public ActuLegaleAnnouncement publishAnnouncement(Announcement announcement, Affaire affaire)
            throws OsirisException;
}
