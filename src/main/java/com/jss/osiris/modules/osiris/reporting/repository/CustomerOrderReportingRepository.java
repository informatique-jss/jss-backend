package com.jss.osiris.modules.osiris.reporting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.jss.osiris.modules.osiris.quotation.model.Quotation;
import com.jss.osiris.modules.osiris.reporting.model.ICustomerOrderReporting;

public interface CustomerOrderReportingRepository extends CrudRepository<Quotation, Integer> {

        @Query(nativeQuery = true, value = "" +
                        " select " +
                        " count(distinct id) as nbrCustomerOrder, " +
                        " customerOrderStatusLabel, " +
                        " aggregateProvisionTypeLabel, " +
                        " lastReminderDate, " +
                        " customerOrderCreator, " +
                        " customerOrderCreatedDateYear, customerOrderCreatedDateMonth, customerOrderCreatedDateDay, customerOrderCreatedDateWeek "
                        +
                        " from " +
                        " ( " +
                        " select " +
                        " co.id as id, " +
                        " cos2.label as customerOrderStatusLabel, " +
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
                        " to_char(coalesce(third_reminder_date_time,second_reminder_date_time,first_reminder_date_time),'YYYY-MM-DD') as lastReminderDate, "
                        +
                        " adt.username as customerOrderCreator, " +
                        " to_char(adt.datetime, 'YYYY') as customerOrderCreatedDateYear, " +
                        " to_char(adt.datetime, 'YYYY-MM') as customerOrderCreatedDateMonth, " +
                        " to_char(adt.datetime, 'YYYY-MM-DD') as customerOrderCreatedDateDay," +
                        " to_char(adt.datetime, 'YYYY-MM - tmw') as customerOrderCreatedDateWeek" +
                        " from " +
                        " customer_order co " +
                        " join customer_order_status cos2 on " +
                        " cos2.id = co.id_customer_order_status " +
                        " left join audit adt on co.id = adt.entity_id and adt.entity='CustomerOrder' and adt.field_name='id'"
                        +
                        " left join asso_affaire_order aao on " +
                        " aao.id_customer_order = co.id " +
                        " left join service on service.id_asso_affaire_order = aao.id " +
                        " left join provision p on " +
                        " p.id_service = service.id " +
                        " left join provision_family_type pft on " +
                        " pft.id = p.id_provision_family_type " +
                        " group by " +
                        " co.id, " +
                        " cos2.label, " +
                        " co.id, " +
                        " adt.username, " +
                        " to_char(adt.datetime, 'YYYY')  , " +
                        " to_char(adt.datetime, 'YYYY-MM')  , " +
                        " to_char(adt.datetime, 'YYYY-MM - tmw')  , " +
                        " to_char(adt.datetime, 'YYYY-MM-DD') " +
                        " ) t " +
                        " group by " +
                        " customerOrderStatusLabel, " +
                        " aggregateProvisionTypeLabel, " +
                        " lastReminderDate, " +
                        " customerOrderCreator ," +
                        " customerOrderCreatedDateYear, customerOrderCreatedDateMonth, customerOrderCreatedDateDay, customerOrderCreatedDateWeek "
                        +
                        "")
        List<ICustomerOrderReporting> getCustomerOrderReporting();
}