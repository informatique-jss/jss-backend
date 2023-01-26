package com.jss.osiris.modules.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.quotation.model.IProvisionBoardResult;
import com.jss.osiris.modules.quotation.model.Provision;

public interface ProvisionRepository extends CrudRepository<Provision, Integer> {


    
    @Query(nativeQuery = true, value = " " +
    "select p.id_employee as employee, count(*) as nbProvision, coalesce(sa.aggregate_label, sb.aggregate_label) as status, " +
    "	max(sa.aggregate_priority) as priority1, max(sb.aggregate_priority) as priority2, 1 as priority3 " +
    "from Provision p " +
    "    left join announcement a on p.id_announcement = a.id " +
    "    left join announcement_status sa on a.id_announcement_status = sa.id and sa.is_close_state = false " +
    "    left join bodacc b on p.id_bodacc = b.id " +
    "    left join bodacc_status sb on b.id_bodacc_status = sb.id and sb.is_close_state = false " +
    "	join asso_affaire_order asso_ord on p.id_asso_affaire_order = asso_ord.id " +
    "	join customer_order ord ON asso_ord.id_customer_order = ord.id " +
    "	join customer_order_status s_ord on ord.id_customer_order_status = s_ord.id and s_ord.code not in ('OPEN', 'ABANDONED', 'WAITING_DEPOSIT') " +
    "where (:employees is null or p.id_employee in (:employees)) " +
    "	and (p.id_announcement is not null or p.id_bodacc is not null) " +
    "group by p.id_employee, status " +
    "")
    List<IProvisionBoardResult> getBoardALs(@Param("employees") List<Integer> employees);

    
    @Query(nativeQuery = true, value = " " +
    "select p.id_employee as employee, count(*) as nbProvision, coalesce(ssi.aggregate_label, sf.aggregate_label, sd.aggregate_label) as status, " +
    "	max(ssi.aggregate_priority) as priority1, max(sf.aggregate_priority) as priority2, max(sd.aggregate_priority) as priority3 " +
    "from provision p  " +
    "    left join simple_provision sp on p.id_simple_provision = sp.id  " +
    "    left join simple_provision_status ssi on sp.id_simple_provision_status = ssi.id and ssi.is_close_state = false " +
    "    left join formalite f on p.id_formalite = f.id  " +
    "    left join formalite_status sf on f.id_formalite_status = sf.id and sf.is_close_state = false " +
    "    left join domiciliation d on p.id_domiciliation = d.id  " +
    "    left join domiciliation_status sd on d.id_domicilisation_status = sd.id and sd.is_close_state = false " +
    "	join asso_affaire_order asso_ord on p.id_asso_affaire_order = asso_ord.id " +
    "	join customer_order ord ON asso_ord.id_customer_order = ord.id " +
    "	join customer_order_status s_ord on ord.id_customer_order_status = s_ord.id and s_ord.code not in ('OPEN', 'ABANDONED', 'WAITING_DEPOSIT') " +
    "where (:employees is null or p.id_employee in (:employees)) " +
    "	and (p.id_simple_provision is not null or p.id_formalite is not null or p.id_domiciliation is not null) " +
    "group by p.id_employee, status " +
    "")
    List<IProvisionBoardResult> getBoardFormalite(@Param("employees") List<Integer> employees);
}