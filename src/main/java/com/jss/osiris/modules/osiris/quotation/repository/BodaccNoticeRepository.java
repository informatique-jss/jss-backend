package com.jss.osiris.modules.osiris.quotation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.BodaccNotice;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface BodaccNoticeRepository extends QueryCacheCrudRepository<BodaccNotice, String> {

    @Query("select max(dateparution) from BodaccNotice")
    public LocalDate getLastLocalDate();

    public List<BodaccNotice> findBySirenOrderByDateparutionDesc(String siren);

    public List<BodaccNotice> findByCommercantIgnoreCaseOrderByDateparutionDesc(String denomination);

    @Query("select p from Provision p join p.service s join s.assoAffaireOrder aao join aao.affaire a join aao.customerOrder co where a.siren=:siren and co.customerOrderStatus=:customerOrderStatusInProgress and p.isNotifyBodacc=true")
    public List<Provision> getProvisionsToNotify(@Param("siren") String siren,
            @Param("customerOrderStatusInProgress") CustomerOrderStatus customerOrderStatusInProgress);
}