package com.jss.osiris.modules.osiris.quotation.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.BaloNotice;
import com.jss.osiris.modules.osiris.quotation.model.BodaccNotice;
import com.jss.osiris.modules.osiris.quotation.model.JoNotice;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;
import com.jss.osiris.modules.osiris.quotation.service.BaloNoticeService;
import com.jss.osiris.modules.osiris.quotation.service.BodaccNoticeService;
import com.jss.osiris.modules.osiris.quotation.service.JoNoticeService;

@Service
public class CompetentAuthorityFacade {

    @Autowired
    AffaireService affaireService;

    @Autowired
    BodaccNoticeService bodaccNoticeService;

    @Autowired
    BaloNoticeService baloNoticeService;

    @Autowired
    JoNoticeService joNoticeService;

    @Transactional(rollbackFor = Exception.class)
    public List<BodaccNotice> getBodaccNoticeByAffaire(Integer idAffaire) {
        Affaire affaire = affaireService.getAffaire(idAffaire);
        if (affaire != null)
            return bodaccNoticeService.getBodaccNoticeByAffaire(affaire);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<BaloNotice> getBaloNoticeByAffaire(Integer idAffaire) {
        Affaire affaire = affaireService.getAffaire(idAffaire);
        if (affaire != null)
            return baloNoticeService.getBaloNoticeByAffaire(affaire);
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<JoNotice> getJoNoticeByAffaire(Integer idAffaire) {
        Affaire affaire = affaireService.getAffaire(idAffaire);
        if (affaire != null)
            return joNoticeService.getJoNoticeByAffaire(affaire);
        return null;
    }
}
