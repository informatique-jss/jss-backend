package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.laPoste.LaPosteTracking;

public interface LaPosteTrackingRepository extends QueryCacheCrudRepository<LaPosteTracking, Integer> {

    List<LaPosteTracking> findByProvision(Provision provision);

    LaPosteTracking findByIdShip(String idShip);

    List<LaPosteTracking> findByShipment_IsFinal(Boolean isFinal);
}