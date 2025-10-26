package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.BaloNotice;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.repository.BaloNoticeRepository;

@Service
public class BaloNoticeServiceImpl implements BaloNoticeService {

    @Autowired
    BaloNoticeRepository baloNoticeRepository;

    @Autowired
    BaloDelegateService baloDelegateService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    NotificationService notificationService;

    @Transactional(rollbackFor = Exception.class)
    public void updateBaloNotices() {
        LocalDate lastDate = baloNoticeRepository.getLastLocalDate();
        CustomerOrderStatus customerOrderStatusInProgress = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED);
        if (lastDate != null) {
            List<BaloNotice> notices = baloDelegateService.getBaloAfterDate(lastDate.minusDays(1));
            if (notices != null && notices.size() > 0) {
                for (BaloNotice notice : notices) {

                    Optional<BaloNotice> currentNotice = baloNoticeRepository.findById(notice.getIdAnnonce());
                    boolean isNew = !currentNotice.isPresent();

                    if (notice.getSirenIn() != null && notice.getSirenIn().size() > 0)
                        notice.setSiren(notice.getSirenIn().get(0).replaceAll(" ", "").trim());

                    baloNoticeRepository.save(notice);

                    if (isNew && notice.getSiren() != null && notice.getSiren().length() > 0) {
                        List<Provision> provisions = baloNoticeRepository.getProvisionsToNotify(notice.getSiren(),
                                customerOrderStatusInProgress);
                        if (provisions != null)
                            for (Provision provision : provisions)
                                notificationService.notifyBaloNoticeAddToProvision(provision, notice);

                    }
                }
            }
        }
    }

    @Override
    public List<BaloNotice> getBaloNoticeByAffaire(Affaire affaire) {
        if (affaire.getSiren() != null && affaire.getSiren().length() > 0)
            return baloNoticeRepository.findBySirenOrderByDateparutionDesc(affaire.getSiren());
        return null;
    }

}
