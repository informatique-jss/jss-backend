package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.BaloNotice;

public interface BaloNoticeService {

    public void updateBaloNotices();

    public List<BaloNotice> getBaloNoticeByAffaire(Affaire affaire);

}
