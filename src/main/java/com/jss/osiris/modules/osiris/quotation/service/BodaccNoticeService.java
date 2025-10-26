package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.BodaccNotice;

public interface BodaccNoticeService {

    public void updateBodacNotices();

    public List<BodaccNotice> getBodaccNoticeByAffaire(Affaire affaire);

}
