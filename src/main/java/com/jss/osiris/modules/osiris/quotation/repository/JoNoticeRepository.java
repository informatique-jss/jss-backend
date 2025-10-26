package com.jss.osiris.modules.osiris.quotation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.JoNotice;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface JoNoticeRepository extends QueryCacheCrudRepository<JoNotice, String> {

    @Query("select max(dateparution) from JoNotice")
    public LocalDate getLastLocalDate();

    public List<JoNotice> findByTitreIgnoreCaseOrderByDateparutionDesc(String titre);

    @Query("select p from Provision p join p.service s join s.assoAffaireOrder aao join aao.affaire a join aao.customerOrder co where a.denomination=:titre and co.customerOrderStatus=:customerOrderStatusInProgress and p.isNotifyJo = true")
    public List<Provision> getProvisionsToNotify(@Param("titre") String titre,
            @Param("customerOrderStatusInProgress") CustomerOrderStatus customerOrderStatusInProgress);
}