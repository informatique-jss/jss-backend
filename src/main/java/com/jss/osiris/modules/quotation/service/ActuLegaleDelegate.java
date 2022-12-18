package com.jss.osiris.modules.quotation.service;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.quotation.model.ActuLegaleAnnouncement;
import com.jss.osiris.modules.quotation.model.Affaire;
import com.jss.osiris.modules.quotation.model.Announcement;

public interface ActuLegaleDelegate {
    public ActuLegaleAnnouncement publishAnnouncement(Announcement announcement, Affaire affaire)
            throws OsirisException;
}
