package com.jss.osiris.modules.osiris.quotation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.libs.batch.service.BatchService;
import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.laPoste.LaPosteTracking;
import com.jss.osiris.modules.osiris.quotation.repository.LaPosteTrackingRepository;

@Service
public class LaPosteTrackingServiceImpl implements LaPosteTrackingService {

    @Autowired
    LaPosteTrackingRepository laPosteTrackingRepository;

    @Autowired
    BatchService batchService;

    @Autowired
    LaPosteDelegateService laPosteDelegateService;

    private LaPosteTracking getLaPosteTracking(Integer id) {
        Optional<LaPosteTracking> laPosteTracking = laPosteTrackingRepository.findById(id);
        if (laPosteTracking.isPresent())
            return laPosteTracking.get();
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public LaPosteTracking addOrUpdateLaPosteTracking(LaPosteTracking laPosteTracking) {
        return laPosteTrackingRepository.save(laPosteTracking);
    }

    @Override
    public List<LaPosteTracking> getLaPosteTrackingsByProvision(Provision provision) {
        return laPosteTrackingRepository.findByProvision(provision);
    }

    @Override
    public void refreshAllOpenLaPosteTrackings() throws OsirisException {
        List<LaPosteTracking> openedTracking = laPosteTrackingRepository.findByShipment_IsFinal(false);
        if (openedTracking != null)
            for (LaPosteTracking tracking : openedTracking)
                batchService.declareNewBatch(Batch.UPDATE_LA_POSTE_TRACKING, tracking.getId());
    }

    @Override
    public void updateTracking(Integer entityId) throws OsirisClientMessageException, OsirisException {
        LaPosteTracking tracking = getLaPosteTracking(entityId);
        if (tracking != null) {
            LaPosteTracking apiTracking = laPosteDelegateService.getLaPosteTracking(tracking.getIdShip());
            if (apiTracking != null) {
                Integer id = tracking.getId();
                tracking = apiTracking;
                tracking.setId(id);
                addOrUpdateLaPosteTracking(tracking);
            }
        }
    }
}
