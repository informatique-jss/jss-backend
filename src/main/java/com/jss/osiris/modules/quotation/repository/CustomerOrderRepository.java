package com.jss.osiris.modules.quotation.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.CustomerOrder;
import com.jss.osiris.modules.quotation.model.QuotationStatus;

public interface CustomerOrderRepository extends CrudRepository<CustomerOrder, Integer> {

    @Query("select i from CustomerOrder i  left join fetch i.tiers t left join fetch i.responsable r left join fetch i.confrere c  where ( i.quotationStatus in (:quotationStatus)) and i.createdDate>=:startDate and i.createdDate<=:endDate and (:salesEmployee is null or t.salesEmployee=:salesEmployee or r.salesEmployee=:salesEmployee or c.salesEmployee=:salesEmployee)")
    List<CustomerOrder> findCustomerOrders(@Param("salesEmployee") Employee salesEmployee,
            @Param("quotationStatus") List<QuotationStatus> quotationStatus,
            @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}