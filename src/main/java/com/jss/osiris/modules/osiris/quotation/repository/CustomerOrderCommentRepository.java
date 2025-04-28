package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrder;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderComment;
import com.jss.osiris.modules.osiris.quotation.model.Provision;
import com.jss.osiris.modules.osiris.quotation.model.Quotation;

public interface CustomerOrderCommentRepository extends QueryCacheCrudRepository<CustomerOrderComment, Integer> {

    List<CustomerOrderComment> findByCustomerOrder(CustomerOrder customerOrder);

    List<CustomerOrderComment> findByQuotation(Quotation quotation);

    List<CustomerOrderComment> findByProvision(Provision provision);
}