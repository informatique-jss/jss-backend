package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.service.NotificationService;
import com.jss.osiris.modules.osiris.quotation.model.Affaire;
import com.jss.osiris.modules.osiris.quotation.model.BodaccNotice;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.repository.BodaccNoticeRepository;

@Service
public class BodaccNoticeServiceImpl implements BodaccNoticeService {

    @Autowired
    BodaccNoticeRepository bodaccNoticeRepository;

    @Autowired
    BodaccDelegateService bodaccDelegateService;

    @Autowired
    AffaireService affaireService;

    @Autowired
    CustomerOrderStatusService customerOrderStatusService;

    @Autowired
    NotificationService notificationService;

    @Transactional(rollbackFor = Exception.class)
    public void updateBodacNotices() {
        LocalDate lastDate = bodaccNoticeRepository.getLastLocalDate();
        CustomerOrderStatus customerOrderStatusInProgress = customerOrderStatusService
                .getCustomerOrderStatusByCode(CustomerOrderStatus.BEING_PROCESSED);
        if (lastDate != null) {
            List<BodaccNotice> notices = bodaccDelegateService.getBodaccAfterDate(lastDate.minusDays(1));
            List<String> knownSiren = bodaccNoticeRepository.getListOfKnownSiren();
            if (notices != null && notices.size() > 0) {
                for (BodaccNotice notice : notices) {

                    Optional<BodaccNotice> currentNotice = bodaccNoticeRepository.findById(notice.getId());

                    if (!currentNotice.isPresent()) {
                        if (notice.getRegistre() != null && notice.getRegistre().size() > 0)
                            notice.setSiren(notice.getRegistre().get(0).replaceAll(" ", "").trim());

                        bodaccNoticeRepository.save(notice);

                        if (notice.getSiren() != null && notice.getSiren().length() > 0
                                && knownSiren.indexOf(notice.getSiren()) >= 0) {
                            List<Provision> provisions = bodaccNoticeRepository.getProvisionsToNotify(notice.getSiren(),
                                    customerOrderStatusInProgress);
                            if (provisions != null)
                                for (Provision provision : provisions)
                                    notificationService.notifyBodaccNoticeAddToProvision(provision, notice);

                        }
                    }
                }
            }
        }
    }

    @Override
    public List<BodaccNotice> getBodaccNoticeByAffaire(Affaire affaire) {
        if (affaire.getSiren() != null && affaire.getSiren().length() > 0)
            return bodaccNoticeRepository.findBySirenOrderByDateparutionDesc(affaire.getSiren());
        else if (affaire.getDenomination() != null && affaire.getDenomination().length() > 0)
            return bodaccNoticeRepository.findByCommercantIgnoreCaseOrderByDateparutionDesc(affaire.getDenomination());
        return null;
    }

}
