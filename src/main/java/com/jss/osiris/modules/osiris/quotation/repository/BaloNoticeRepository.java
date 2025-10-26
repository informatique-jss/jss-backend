package com.jss.osiris.modules.osiris.quotation.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.BaloNotice;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderStatus;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface BaloNoticeRepository extends QueryCacheCrudRepository<BaloNotice, String> {

    @Query("select max(dateparution) from BaloNotice")
    public LocalDate getLastLocalDate();

    public List<BaloNotice> findBySirenOrderByDateparutionDesc(String siren);

    @Query("select p from Provision p join p.service s join s.assoAffaireOrder aao join aao.affaire a join aao.customerOrder co where a.siren=:siren and co.customerOrderStatus=:customerOrderStatusInProgress and p.isNotifyBalo = true")
    public List<Provision> getProvisionsToNotify(@Param("siren") String siren,
            @Param("customerOrderStatusInProgress") CustomerOrderStatus customerOrderStatusInProgress);
}