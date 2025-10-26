package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.JoNotice;

public interface JoNoticeService {

    public void updateJoNotices();

    public List<JoNotice> getJoNoticeByAffaire(Affaire affaire);

}
