package com.jss.osiris.modules.osiris.quotation.repository;

import java.util.List;

import com.jss.osiris.libs.QueryCacheCrudRepository;
import com.jss.osiris.modules.osiris.quotation.model.CustomerOrderAssignation;

public interface CustomerOrderAssignationRepository
                extends QueryCacheCrudRepository<CustomerOrderAssignation, Integer> {

        List<CustomerOrderAssignation> findByCustomerOrder_Id(Integer customerOrder);
}