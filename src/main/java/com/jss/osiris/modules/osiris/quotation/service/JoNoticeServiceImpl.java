package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.JoNotice;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.repository.JoNoticeRepository;

@Service
public class JoNoticeServiceImpl implements JoNoticeService {

    @Autowired
    JoNoticeRepository joNoticeRepository;

    @Autowired
    JoDelegateService joDelegateService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    NotificationService notificationService;

    @Transactional(rollbackFor = Exception.class)
    public void updateJoNotices() {
        LocalDate lastDate = joNoticeRepository.getLastLocalDate();
        CustomerOrderStatus customerOrderStatusInProgress = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED);
        if (lastDate != null) {
            List<JoNotice> notices = joDelegateService.getJoAfterDate(lastDate.minusDays(1));
            if (notices != null && notices.size() > 0) {
                for (JoNotice notice : notices) {

                    Optional<JoNotice> currentNotice = joNoticeRepository.findById(notice.getId());
                    boolean isNew = !currentNotice.isPresent();

                    joNoticeRepository.save(notice);

                    if (isNew && notice.getTitre() != null && notice.getTitre().length() > 0) {
                        List<Provision> provisions = joNoticeRepository.getProvisionsToNotify(notice.getTitre(),
                                customerOrderStatusInProgress);
                        if (provisions != null)
                            for (Provision provision : provisions)
                                notificationService.notifyJoNoticeAddToProvision(provision, notice);

                    }
                }
            }
        }
    }

    @Override
    public List<JoNotice> getJoNoticeByAffaire(Affaire affaire) {
        if (affaire.getDenomination() != null && affaire.getDenomination().length() > 0)
            return joNoticeRepository.findByTitreIgnoreCaseOrderByDateparutionDesc(affaire.getDenomination());
        return null;
    }

}
