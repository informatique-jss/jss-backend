package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.reporting.model.IProvisionReporting;

public interface ProvisionReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        "         select " +
                        "         to_char(date_trunc('year', " +
                        " co.created_date), " +
                        " 'YYYY') as customerOrderCreatedDateYear, " +
                        " to_char(date_trunc('month', " +
                        " co.created_date), " +
                        " 'YYYY-MM') as customerOrderCreatedDateMonth, " +
                        " to_char(date_trunc('week', " +
                        " co.created_date), " +
                        " 'YYYY-MM - tmw') as customerOrderCreatedDateWeek, " +
                        " to_char(date_trunc('day', " +
                        " co.created_date), " +
                        " 'YYYY-MM-DD') as customerOrderCreatedDateDay,   " +
                        "     cos2.label as customerOrderStatusLabel,  " +
                        "     concat (e.firstname, " +
                        " ' ', " +
                        " e.lastname) as provisionAssignedToLabel, " +
                        "     pft.label as provisionFamilyTypeLabel, " +
                        "     count(distinct p.id) as provisionNumber, " +
                        "     coalesce(as2.label, " +
                        " sps.label, " +
                        " fst.label, " +
                        " ds.label, " +
                        " bs.label) as provisionStatus " +
                        " from " +
                        "     customer_order co " +
                        " join customer_order_status cos2 on " +
                        "     cos2.id = co.id_customer_order_status " +
                        " left join asso_affaire_order aao on " +
                        "     aao.id_customer_order = co.id " +
                        " left join provision p on " +
                        "     p.id_asso_affaire_order = aao.id " +
                        " left join provision_family_type pft on " +
                        "     pft.id = p.id_provision_family_type " +
                        " left join employee e on " +
                        "     e.id = p.id_employee " +
                        " left join announcement a on " +
                        " a.id = p.id_announcement " +
                        " left join announcement_status as2 on " +
                        " as2.id = a.id_announcement_status " +
                        " left join simple_provision sp on " +
                        " sp.id = p.id_simple_provision " +
                        " left join simple_provision_status sps on " +
                        " sps.id = sp.id_simple_provision_status " +
                        " left join formalite f on " +
                        " f.id = p.id_formalite " +
                        " left join formalite_status fst on " +
                        " fst.id = f.id_formalite_status " +
                        " left join domiciliation d on " +
                        " d.id = p.id_domiciliation " +
                        " left join domiciliation_status ds on " +
                        " ds.id = d.id_domicilisation_status " +
                        " left join bodacc b on " +
                        " b.id = p.id_bodacc " +
                        " left join bodacc_status bs on " +
                        " bs.id = b.id_bodacc_status " +
                        " group by " +
                        " to_char(date_trunc('year', " +
                        " co.created_date), " +
                        " 'YYYY') , " +
                        " to_char(date_trunc('month', " +
                        " co.created_date), " +
                        " 'YYYY-MM') , " +
                        " to_char(date_trunc('week', " +
                        " co.created_date), " +
                        " 'YYYY-MM - tmw'), " +
                        " to_char(date_trunc('day', " +
                        " co.created_date), " +
                        " 'YYYY-MM-DD'), " +
                        "     cos2.label , " +
                        "     concat (e.firstname, " +
                        " ' ', " +
                        " e.lastname) , " +
                        "     pft.label,  " +
                        "     coalesce(as2.label, " +
                        " sps.label, " +
                        " fst.label, " +
                        " ds.label, " +
                        " bs.label) " +
                        "")
        List<IProvisionReporting> getProvisionReporting();
}