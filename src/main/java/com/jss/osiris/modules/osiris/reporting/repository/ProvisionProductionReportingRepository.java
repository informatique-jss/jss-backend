package com.jss.osiris.modules.osiris.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.reporting.model.IProvisionProductionReporting;

public interface ProvisionProductionReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        " select " +
                        " provisionAssignedToLabel, " +
                        " to_char(date_trunc('year', " +
                        " max(datetime) ), " +
                        " 'YYYY') as yearField, " +
                        " to_char(date_trunc('month', " +
                        " max(datetime) ), " +
                        " 'YYYY-MM') as monthField, " +
                        " to_char(date_trunc('week', " +
                        " max(datetime) ), " +
                        " 'YYYY-MM - tmw') as weekField, " +
                        " to_char(date_trunc('day', " +
                        " max(datetime) ), " +
                        " 'YYYY-MM-DD') as dayField , " +
                        " count(*) as provisionNumber " +
                        " from " +
                        " ( " +
                        " select " +
                        " concat(e.firstname, " +
                        " ' ', " +
                        " e.lastname) as provisionAssignedToLabel, " +
                        " date_trunc('day', " +
                        " max(coalesce(aa.datetime, asp.datetime, af.datetime, ad.datetime))) as datetime, "
                        +
                        " p.id " +
                        " from " +
                        " provision p " +
                        " left join employee e on " +
                        " e.id = p.id_employee " +
                        " left join announcement a on " +
                        " a.id = p.id_announcement " +
                        " left join announcement_status as2 on " +
                        " as2.id = a.id_announcement_status " +
                        " and as2.is_close_state " +
                        " left join simple_provision sp on " +
                        " sp.id = p.id_simple_provision " +
                        " left join simple_provision_status sps on " +
                        " sps.id = sp.id_simple_provision_status " +
                        " and sps.is_close_state " +
                        " left join formalite f on " +
                        " f.id = p.id_formalite " +
                        " left join formalite_status fst on " +
                        " fst.id = f.id_formalite_status " +
                        " and fst.is_close_state " +
                        " left join domiciliation d on " +
                        " d.id = p.id_domiciliation " +
                        " left join domiciliation_status ds on " +
                        " ds.id = d.id_domicilisation_status " +
                        " and ds.is_close_state " +
                        " left join audit aa on " +
                        " aa.entity = 'Announcement' " +
                        " and aa.entity_id = a.id " +
                        " and aa.field_name = 'announcementStatus' " +
                        " and aa.new_value = as2.code " +
                        " left join audit asp on " +
                        " asp.entity = 'SimpleProvision' " +
                        " and asp.entity_id = sp.id " +
                        " and asp.field_name = 'simpleProvisionStatus' " +
                        " and asp.new_value = sps.code " +
                        " left join audit af on " +
                        " af.entity = 'Formalite' " +
                        " and af.entity_id = f.id " +
                        " and af.field_name = 'formaliteStatus' " +
                        " and af.new_value = fst.code " +
                        " left join audit ad on " +
                        " ad.entity = 'Domiciliation' " +
                        " and ad.entity_id = d.id " +
                        " and ad.field_name = 'domiciliationStatus' " +
                        " and ad.new_value = ds.code " +
                        " where " +
                        " coalesce(as2.id, " +
                        " sps.id, " +
                        " fst.id, " +
                        " ds.id  " +
                        " ) is not null " +
                        " group by " +
                        " concat(e.firstname, " +
                        " ' ', " +
                        " e.lastname), " +
                        " p.id " +
                        " ) t " +
                        " group by " +
                        " provisionAssignedToLabel, " +
                        " date_trunc('day', " +
                        " datetime ) " +
                        "")
        List<IProvisionProductionReporting> getProvisionProductionReporting();
}