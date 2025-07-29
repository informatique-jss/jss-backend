package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderAssignation;
import com.jss.osiris.modules.osiris.quotation.model.ICustomerOrderAssignationStatistics;

public interface CustomerOrderAssignationRepository
                extends QueryCacheCrudRepository<CustomerOrderAssignation, Integer> {

        List<CustomerOrderAssignation> findByCustomerOrder_Id(Integer customerOrder);

        @Query(nativeQuery = true, value = "" +
                        "         select " +
                        " 	e.id as idEmployee , " +
                        " 	to_char( c.production_effective_date_time, 'YYYY-MM-DD') as productionDate, " +
                        " 	count(distinct c.id) as number   " +
                        " from " +
                        " 	customer_order c " +
                        " left join customer_order_assignation c2 on " +
                        " 	c2.id_customer_order = c.id " +
                        " join responsable r on " +
                        " 	r.id = c.id_responsable " +
                        " join employee e on " +
                        " 	e.id = r.id_formaliste " +
                        " where " +
                        " 	(c2.id is null " +
                        " 		or c2.id_employee is null " +
                        " 		or coalesce(c2.is_assigned, false) = false) " +
                        " 	and c.id_customer_order_status = :inProgressCustomerOrderStatusId and c2.id_assignation_type=:assignationTypeId "
                        +
                        " group by " +
                        " 	e.id , " +
                        " 	to_char(c.production_effective_date_time, 'YYYY-MM-DD') " +
                        "")
        List<ICustomerOrderAssignationStatistics> getCustomerOrderAssignationStatisticsForFormalistes(
                        @Param("inProgressCustomerOrderStatusId") Integer inProgressCustomerOrderStatusId,
                        @Param("assignationTypeId") Integer assignationTypeId);

        @Query(nativeQuery = true, value = "" +
                        "         select " +
                        " 	e.id as idEmployee , " +
                        " 	to_char( c.production_effective_date_time, 'YYYY-MM-DD') as productionDate, " +
                        " 	count(distinct c.id) as number   " +
                        " from " +
                        " 	customer_order c " +
                        " left join customer_order_assignation c2 on " +
                        " 	c2.id_customer_order = c.id " +
                        " join responsable r on " +
                        " 	r.id = c.id_responsable " +
                        " join employee e on " +
                        " 	e.id = r.id_insertion " +
                        " where " +
                        " 	(c2.id is null " +
                        " 		or c2.id_employee is null " +
                        " 		or coalesce(c2.is_assigned, false) = false) " +
                        " 	and c.id_customer_order_status = :inProgressCustomerOrderStatusId  and c2.id_assignation_type=:assignationTypeId "
                        +
                        " group by " +
                        " 	e.id , " +
                        " 	to_char(c.production_effective_date_time, 'YYYY-MM-DD') " +
                        "")
        List<ICustomerOrderAssignationStatistics> getCustomerOrderAssignationStatisticsForInsertions(
                        @Param("inProgressCustomerOrderStatusId") Integer inProgressCustomerOrderStatusId,
                        @Param("assignationTypeId") Integer assignationTypeId);
}