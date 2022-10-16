package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.AffaireStatus;
import com.jss.osiris.modules.quotation.model.AssoAffaireOrder;

public interface AssoAffaireOrderRepository extends CrudRepository<AssoAffaireOrder, Integer> {
    @Query("select a from AssoAffaireOrder a left join fetch a.affaire af  left join fetch a.provisions p where a.customerOrder is not null and  ( COALESCE(:affaireStatus) is null or a.affaireStatus in (:affaireStatus)) and (:responsible is null or a.assignedTo=:responsible) and (:assignedTo is null or p.assignedTo=:assignedTo) and (:label ='' or upper(af.denomination)  like '%' || cast(upper(:label) as string) || '%'  or upper(af.firstname)  like '%' || cast(upper(:label) as string) || '%') ")
    List<AssoAffaireOrder> findAsso(@Param("responsible") Employee responsible,
            @Param("assignedTo") Employee assignedTo,
            @Param("affaireStatus") List<AffaireStatus> affaireStatus,
            @Param("label") String label);
}