package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.profile.model.Employee;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;

public interface CustomerOrderCommentRepository extends QueryCacheCrudRepository<CustomerOrderComment, Integer> {

        List<CustomerOrderComment> findByCustomerOrder(CustomerOrder customerOrder);

        List<CustomerOrderComment> findByQuotation(Quotation quotation);

        List<CustomerOrderComment> findByProvision(Provision provision);

        List<CustomerOrderComment> findByCustomerOrderAndIsFromChat(CustomerOrder customerOrder, Boolean isFromChat);

        @Query("""
                        select c
                        from CustomerOrderComment c
                        where c.isRead = false
                                and c.isFromChat = true
                                and exists (
                                select 1
                                from Responsable r
                                where r.salesEmployee = :employee
                                and (
                                        r = c.customerOrder.responsable
                                        or r = c.quotation.responsable
                                )
                                )""")
        List<CustomerOrderComment> findUnreadCommmentsForSalesEmployee(Employee employee);
}