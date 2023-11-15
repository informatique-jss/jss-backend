package com.jss.osiris.modules.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.quotation.model.Quotation;
import com.jss.osiris.modules.reporting.model.ICustomerOrderReporting;

public interface CustomerOrderReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        " select " +
                        " count(distinct id) as nbrCustomerOrder, " +
                        " customerOrderStatusLabel, " +
                        " customerOrderAssignedEmployee, " +
                        " aggregateProvisionTypeLabel, " +
                        " lastReminderDate " +
                        " from " +
                        " ( " +
                        " select " +
                        " co.id, " +
                        " cos2.label as customerOrderStatusLabel, " +
                        " concat(e.firstname, " +
                        " ' ', " +
                        " e.lastname) as customerOrderAssignedEmployee, " +
                        " array_to_string(array[ " +
                        " case " +
                        " when sum(case when pft.code like 'B%' then 1 else 0 end)>0 then 'Formalité' " +
                        " end, " +
                        " case " +
                        " when sum(case when p.id_announcement is not null then 1 else 0 end)>0 then 'AL' " +
                        " end, " +
                        " case " +
                        " when sum(case when pft.code not like 'B%' and p.id_announcement is null then 1 else 0 end)>0 then 'Autre' "
                        +
                        " end], " +
                        " ' / ') as aggregateProvisionTypeLabel, " +
                        " to_char(coalesce(third_reminder_date_time,second_reminder_date_time,first_reminder_date_time),'YYYY-MM-DD') as lastReminderDate "
                        +
                        " from " +
                        " customer_order co " +
                        " join customer_order_status cos2 on " +
                        " cos2.id = co.id_customer_order_status " +
                        " left join asso_affaire_order aao on " +
                        " aao.id_customer_order = co.id " +
                        " left join provision p on " +
                        " p.id_asso_affaire_order = aao.id " +
                        " left join provision_family_type pft on " +
                        " pft.id = p.id_provision_family_type " +
                        " left join employee e on " +
                        " e.id = co.id_assigned_to " +
                        " group by " +
                        " co.id, " +
                        " cos2.label, " +
                        " concat(e.firstname, " +
                        " ' ', " +
                        " e.lastname) , " +
                        " co.id " +
                        " ) t " +
                        " group by " +
                        " customerOrderStatusLabel, " +
                        " customerOrderAssignedEmployee, " +
                        " aggregateProvisionTypeLabel, " +
                        " lastReminderDate "
                        +
                        "")
        List<ICustomerOrderReporting> getCustomerOrderReporting();
}